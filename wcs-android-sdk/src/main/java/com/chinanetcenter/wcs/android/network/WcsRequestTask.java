package com.chinanetcenter.wcs.android.network;

import android.text.TextUtils;
import android.util.Log;

import com.chinanetcenter.wcs.android.Config;
import com.chinanetcenter.wcs.android.LogRecorder;
import com.chinanetcenter.wcs.android.entity.OperationMessage;
import com.chinanetcenter.wcs.android.exception.ClientException;
import com.chinanetcenter.wcs.android.exception.ServiceException;
import com.chinanetcenter.wcs.android.internal.SliceUploadRequest;
import com.chinanetcenter.wcs.android.internal.UploadFileRequest;
import com.chinanetcenter.wcs.android.internal.WcsProgressCallback;
import com.chinanetcenter.wcs.android.internal.WcsRetryHandler;
import com.chinanetcenter.wcs.android.utils.WCSLogUtil;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class WcsRequestTask<T extends WcsResult> implements Callable<T> {
    private ResponseParser<T> mResponseParser;
    private WcsRequest mParams;
    private ExecutionContext mExecutionContext;
    private OkHttpClient mOkHttpClient;
    private WcsRetryHandler retryHandler;
    private int currentRetryCount;
    private static HashMap<String, String> sIpList;

    /**
     * 计算进度
     */
    class ProgressRequestBody extends RequestBody {
        private static final int SEGMENT_SIZE = 2048; // okio.Segment.SIZE

        private byte[] data;
        private File file;
        private InputStream inputStream;
        private String contentType;
        private long contentLength;
        private WcsProgressCallback callback;
        private BufferedSink bufferedSink;

        public ProgressRequestBody(File file, String contentType, WcsProgressCallback callback) {
            this.file = file;
            this.contentType = contentType;
            this.callback = callback;
            this.contentLength = file.length();
        }

        public ProgressRequestBody(byte[] data, String contentType, WcsProgressCallback callback) {
            this.data = data;
            this.contentType = contentType;
            this.contentLength = data.length;
            this.callback = callback;
        }

        public ProgressRequestBody(InputStream input, long contentLength, String contentType, WcsProgressCallback callback) {
            this.inputStream = input;
            this.contentType = contentType;
            this.contentLength = contentLength;
            this.callback = callback;
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse(this.contentType);
        }

        @Override
        public long contentLength() throws IOException {
            return this.contentLength;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {
            Source source = null;
            if (this.file != null) {
                source = Okio.source(this.file);
            } else if (this.data != null) {
                source = Okio.source(new ByteArrayInputStream(this.data));
            } else if (this.inputStream != null) {
                source = Okio.source(this.inputStream);
            }
            long total = 0;
            long read, toRead, remain;

            //供普通上传统计次数
            int count = 0;
            //供控制普通上传回调频率
            long number = setNormalProgressFrequency(0);

            while (total < contentLength) {
                remain = contentLength - total;
                toRead = Math.min(remain, SEGMENT_SIZE);

                read = source.read(sink.buffer(), toRead);
                if (read == -1) {
                    break;
                }

                total += read;
                sink.flush();
                if (callback != null && WcsRequestTask.this.mExecutionContext.getRequest() instanceof UploadFileRequest
                        && (++count == number || remain <= SEGMENT_SIZE)) {//加上判断，修改进度回调频率
                    count = 0;
                    //普通上传，一个线程，在这里累加
                    //按SEGMENT_SIZE的频率调用接口过于频繁，此处修改为1/10文件回调一次（可根据需求调整）
                    callback.onProgress(WcsRequestTask.this.mExecutionContext.getRequest(), total, contentLength);
                }
            }

            mParams.setReadStreamLength(contentLength);
            if (callback != null && WcsRequestTask.this.mExecutionContext.getRequest() instanceof SliceUploadRequest) {
                //分片上传多线程，ProgressNotifier中累加
                //每片调用一次onProgress接口。
                callback.onProgress(WcsRequestTask.this.mExecutionContext.getRequest(), contentLength, contentLength);
            }
            if (source != null) {
                source.close();
            }
        }

        /**
         * 设置普通上传进度回调的频率
         *
         * @param number
         * @return
         */
        private long setNormalProgressFrequency(long number) {
            if (callback != null && WcsRequestTask.this.mExecutionContext.getRequest() instanceof UploadFileRequest) {
                number = contentLength / SEGMENT_SIZE;
                number = contentLength % SEGMENT_SIZE == 0 ? number : ++number;
                //此处为1/10,不足以分成10份则不分
                number = number / 10 > 0 ? number / 10 : 1;
            }
            return number;
        }
    }


    public WcsRequestTask(WcsRequest params, ResponseParser responseParser,
                          ExecutionContext executionContext, int maxRetry) {

        this.mParams = params;
        this.mResponseParser = responseParser;
        this.mExecutionContext = executionContext;
        this.mOkHttpClient = executionContext.getClient();
        this.retryHandler = new WcsRetryHandler(maxRetry);
    }

    @Override
    public T call() throws Exception {
        Request request = null;
        Response response = null;
        Exception exception = null;
        Call call = null;
        T result = null;
        try {
            if (mExecutionContext.getCancellationHandler() != null && mExecutionContext.getCancellationHandler().isCancelled()) {
                throw new InterruptedIOException("the task is cancelled");
            }

            Request.Builder requestBuilder = new Request.Builder();

            // build request url
            String url = mParams.getUrl();
            WCSLogUtil.i("url: " + mParams.getUrl());
            requestBuilder = requestBuilder.url(url);
            // set request headers
            for (String key : mParams.getHeaders().keySet()) {
                requestBuilder = requestBuilder.addHeader(key, mParams.getHeaders().get(key));
            }
            //User-Agent: WCS-Android-SDK-<version>-<device>-<os>(https://www.chinanetcenter.com)
            requestBuilder.header("User-Agent",
                    String.format("WCS-Android-SDK-%s-%s-%s(%s)", Config.VERSION, android.os.Build.MODEL, android.os.Build.VERSION.RELEASE,"https://www.chinanetcenter.com"));
            String contentType = mParams.getHeaders().get(HttpHeaders.CONTENT_TYPE);
            switch (mParams.getMethod()) {
                case PUT:
                case POST:

                    if (mParams.getUploadData() != null) {
                        //用于分片上传
                        requestBuilder = requestBuilder.method(mParams.getMethod().toString(),
                                new ProgressRequestBody(mParams.getUploadData(), contentType,
                                        mExecutionContext.getProgressCallback()));
                    } else if (mParams.getUploadFilePath() != null) {
                        requestBuilder = requestBuilder.method(mParams.getMethod().toString(),
                                new ProgressRequestBody(new File(mParams.getUploadFilePath()), contentType,
                                        mExecutionContext.getProgressCallback()));
                    } else if (mParams.getFile() != null) {
                        //用于普通上传
                        MultipartBody.Builder formBuilder = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM);
                        addParams(formBuilder, mParams.getParameters());
                        RequestBody body = formBuilder.addFormDataPart("file", mParams.getName(), new ProgressRequestBody(mParams.getFile(),
                                contentType, mExecutionContext.getProgressCallback()))
                                .build();
                        requestBuilder = requestBuilder.post(body);

                    } else if (mParams.getUploadInputStream() != null) {
                        requestBuilder = requestBuilder.method(mParams.getMethod().toString(),
                                new ProgressRequestBody(mParams.getUploadInputStream(),
                                        mParams.getReadStreamLength(), contentType,
                                        mExecutionContext.getProgressCallback()));
                    } else {
                        requestBuilder = requestBuilder.method(mParams.getMethod().toString(), RequestBody.create(null, new byte[0]));
                    }

                    break;

                case GET:
                    requestBuilder = requestBuilder.get();
                    break;
                case HEAD:
                    requestBuilder = requestBuilder.head();
                    break;
                case DELETE:
                    requestBuilder = requestBuilder.delete();
                    break;
                default:
                    break;

            }
            request = requestBuilder.build();
            call = mOkHttpClient.newCall(request);
            if (mExecutionContext.getCancellationHandler() != null) {
                mExecutionContext.getCancellationHandler().setCall(call);
            }

            // send request
            response = call.execute();
            result = mResponseParser.parse(response);
            dump(url, result);

            if (response.isSuccessful()) {
                if (mExecutionContext.getCompletedCallback() != null) {
                    mExecutionContext.getCompletedCallback().onSuccess(mExecutionContext.getRequest(),
                            result);
                }
                return result;
            } else {
                exception = new ServiceException(response.code(), result.getRequestId(), result.getResponse());
            }
        } catch (Exception e) {
            WCSLogUtil.e(e.toString());
            dump(e);
            exception = new ClientException(e.getMessage(), e);
        } finally {
            if (mExecutionContext.getCancellationHandler() != null) {
                mExecutionContext.getCancellationHandler().setFinished(true);
            }
            if (response != null) {
                response.close();
            }

        }

        // reconstruct exception caused by manually cancelling
        if ((call != null && call.isCanceled())
                || (mExecutionContext.getCancellationHandler() != null && mExecutionContext.getCancellationHandler().isCancelled())) {
            exception = new ClientException("Task is cancelled!", exception.getCause(), true);
        }
        boolean shouldRetry = this.retryHandler.shouldRetry(exception, currentRetryCount);
        WCSLogUtil.e("[run] - retry, should retry: " + shouldRetry);
        if (shouldRetry) {
            this.currentRetryCount++;
            if (mExecutionContext.getProgressCallback() != null && mExecutionContext.getRequest() instanceof SliceUploadRequest && mParams.getReadStreamLength() > 0) {
                //分片上传多线程，重试减去已传值
                mExecutionContext.getProgressCallback().onProgress(mExecutionContext.getRequest(), -mParams.getReadStreamLength(), 0);
                WCSLogUtil.e("[run] - retry, progress: " + -mParams.getReadStreamLength());
            }
            return call();
        } else {
            OperationMessage operationMessage;
            if (exception instanceof ServiceException) {
                try {
                    operationMessage = OperationMessage.fromJsonString(exception.getMessage(), ((ServiceException) exception).getRequestId());
                } catch (JSONException e) {
                    Log.e("CNCLog", "json error : " + exception.getMessage());
                    String message = String.format("url : %s,\r\n status : %s,\r\n header : %s\r\n",
                            mParams.getUrl(), result.getStatusCode(), result.getHeaders());
                    operationMessage = new OperationMessage(0, message);
                }
            } else {
                operationMessage = new OperationMessage(exception);
            }
            if (mExecutionContext.getCompletedCallback() != null) {
                mExecutionContext.getCompletedCallback().onFailure(mExecutionContext.getRequest(),
                        operationMessage);
            }
            throw exception;
        }

    }

    private void dump(Exception e) {
        if (!LogRecorder.getInstance().getLock()) {
            return;
        }
        long timestamp = System.currentTimeMillis();
        String responseIdentifier = String.format("*** time : %s,\r\n *** exception : %s\r\n", timestamp,
                e.getLocalizedMessage());
        LogRecorder.getInstance().dumpLog(responseIdentifier);
    }

    private void dump(String url, WcsResult result) {
        if (!LogRecorder.getInstance().getLock()) {
            return;
        }
        long timestamp = System.currentTimeMillis();
        long contentLength = result.getResponse().length();
        String destIp = getIpByUrl(url);
        String responseIdentifier = String.format("*** time : %s,\r\n *** destIp : %s,\r\n *** uri : %s,\r\n *** status : %s,\r\n *** length : %s\r\n", timestamp,
                destIp, url, result.getStatusCode(), contentLength);
        LogRecorder.getInstance().dumpLog(responseIdentifier);
        LogRecorder.getInstance().dumpLog(result.getHeaders() + "\n");
        LogRecorder.getInstance().dumpLog("*** response : " + result.getResponse() + "\n");
    }

    private void addParams(MultipartBody.Builder builder, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
            }
        }
    }

    private String getIpByUrl(String url) {
        if (sIpList == null) {
            sIpList = new HashMap<String, String>();
        }
        String urlHost = "";
        try {
            URL uploadURL = new URL(url);
            urlHost = uploadURL.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (TextUtils.isEmpty(urlHost)) {
            return "0.0.0.0";
        }
        String ipString = sIpList.get(urlHost);
        if (!TextUtils.isEmpty(ipString)) {
            return ipString;
        }

        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(urlHost);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (null == addr) {
            return "0.0.0.0";
        }

        byte[] ipParts = addr.getAddress();
        StringBuffer sb = new StringBuffer();
        for (byte b : ipParts) {
            int i = b & 0xFF;
            sb.append(i + ".");
        }

        String resolvedIpString = sb.toString();
        sIpList.put(urlHost, resolvedIpString);
        return resolvedIpString;
    }

}

