package com.chinanetcenter.wcs.android.internal;

import android.content.Context;

import com.chinanetcenter.wcs.android.ClientConfig;
import com.chinanetcenter.wcs.android.entity.SliceResponse;
import com.chinanetcenter.wcs.android.network.CancellationHandler;
import com.chinanetcenter.wcs.android.network.ExecutionContext;
import com.chinanetcenter.wcs.android.network.ResponseParser;
import com.chinanetcenter.wcs.android.network.WcsAsyncTask;
import com.chinanetcenter.wcs.android.network.WcsRequest;
import com.chinanetcenter.wcs.android.network.WcsRequestTask;
import com.chinanetcenter.wcs.android.network.WcsResult;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

/**
 * @author :yanghuan
 * @version :1.6.1
 * @package : com.chinanetcenter.wcs.android.internal
 * @class : InternalRequest
 * @time : 2017/6/11 16:38
 * @description :网络请求
 */
public class InternalRequest {

    private OkHttpClient innerClient;

    //可配置
    private ExecutorService executorService;
    private Map<Context, List<CancellationHandler>> requestMap;
    private int maxRetryCount;

    public InternalRequest(ClientConfig conf) {

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .followRedirects(false)//returnUrlWeb端文件上传后，浏览器会执行303跳转的URL，故不能禁止重定向
//                .followSslRedirects(false)
                .retryOnConnectionFailure(false)
                .cache(null);

        int maxConcurrentRequest;
        if (conf != null) {
            Dispatcher dispatcher = new Dispatcher();
            maxConcurrentRequest = conf.getMaxConcurrentRequest();
            dispatcher.setMaxRequests(maxConcurrentRequest);

            builder.connectTimeout(conf.getConnectionTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(conf.getSocketTimeout(), TimeUnit.MILLISECONDS)
                    .writeTimeout(conf.getSocketTimeout(), TimeUnit.MILLISECONDS)
                    .dispatcher(dispatcher);

//            if (conf.getProxyHost() != null && conf.getProxyPort() != 0) {
//                builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(conf.getProxyHost(), conf.getProxyPort())));
//            }

            this.maxRetryCount = conf.getMaxErrorRetry();
        } else {
            maxConcurrentRequest = ClientConfig.DEFAULT_CONCURRENT_REQUEST;
        }
        executorService = Executors.newFixedThreadPool(maxConcurrentRequest);

        this.innerClient = builder.build();
        requestMap = new WeakHashMap<Context, List<CancellationHandler>>();
    }

    public WcsAsyncTask<UploadFileResult> upload(
            UploadFileRequest request,
            WcsCompletedCallback<UploadFileRequest, UploadFileResult> completedCallback, Context context) {

        return upload(request, completedCallback, context, null);
    }

    /**
     * 普通上传
     *
     * @param request
     * @param completedCallback
     * @param tag
     * @return
     */
    public WcsAsyncTask<UploadFileResult> upload(
            UploadFileRequest request,
            WcsCompletedCallback<UploadFileRequest, UploadFileResult> completedCallback, Context context, Object tag) {

        ExecutionContext<UploadFileRequest> executionContext =
                new ExecutionContext<>(innerClient, request);

        if (completedCallback != null) {
            executionContext.setCompletedCallback(completedCallback);
        }

        if (request.getProgressCallback() != null) {
            executionContext.setProgressCallback(request.getProgressCallback());
        }

        ResponseParser<UploadFileResult> parser = new ResponseParsers.UploadResponseParser();
        Callable<UploadFileResult> callable = new WcsRequestTask<>(request, parser, executionContext, maxRetryCount);

        return sendRequest(executionContext, callable, context, tag);
    }

    /**
     * 为了加入取消，需要统一调用这个方法来提交callable
     *
     * @param executionContext
     * @param callable
     * @param tag
     * @return
     */
    private WcsAsyncTask sendRequest(ExecutionContext executionContext, Callable callable, Context context, Object tag) {
        if (context != null) {
            //使用时才初始化
            CancellationHandler cancellationHandler = new CancellationHandler();
            cancellationHandler.setTag(tag);
            executionContext.setCancellationHandler(cancellationHandler);
            // Add request to request map
            List<CancellationHandler> requestList = requestMap.get(context);
            if (requestList == null) {
                requestList = new CopyOnWriteArrayList<>();
                requestMap.put(context, requestList);
            }

            requestList.add(cancellationHandler);
            for (int i = 0; i < requestList.size(); i++) {
                if (requestList.get(i).shouldBeGarbageCollected()) {
                    requestList.remove(i);
                }
            }
        }
        return WcsAsyncTask.wrapRequestTask(executorService.submit(callable), executionContext);
    }


    public void cancelRequests(Context context) {
        List<CancellationHandler> requestList = requestMap.get(context);
        if (requestList != null) {
            for (CancellationHandler cancellationHandler : requestList) {
                cancellationHandler.cancel();
            }
            requestMap.remove(context);
        }
        //通知系统gc
        System.gc();
    }

    public void cancelRequests(Context context, Object tag) {
        List<CancellationHandler> requestList = requestMap.get(context);
        if (requestList != null) {
            List<CancellationHandler> cancelledHandler = new CopyOnWriteArrayList<CancellationHandler>();
            for (CancellationHandler cancellationHandler : requestList) {
                boolean shouldCancel = tag == null;
                if (!shouldCancel) {
                    shouldCancel = tag.equals(cancellationHandler.getTag());
                }
                if (shouldCancel) {
                    cancellationHandler.cancel();
                    cancelledHandler.add(cancellationHandler);
                }
            }
            requestList.removeAll(cancelledHandler);
            if (requestList.size() == 0) {
                requestMap.remove(context);
            }
            System.gc();
        }
    }


    public WcsAsyncTask<SliceResponse> uploadBlock(
            Object tag, SliceUploadRequest request,
            WcsCompletedCallback<WcsRequest, SliceResponse> completedCallback, Context context) {
        ExecutionContext<SliceUploadRequest> executionContext =
                new ExecutionContext<>(innerClient, request);

        if (completedCallback != null) {
            executionContext.setCompletedCallback(completedCallback);
        }

        executionContext.setProgressCallback(request.getProgressCallback());

        ResponseParser<SliceResponse> parser = new ResponseParsers.UploadBlockResponseParser();
        Callable<SliceResponse> callable = new WcsRequestTask<>(request, parser, executionContext, maxRetryCount);

        return sendRequest(executionContext, callable, context, tag);
    }

    public WcsAsyncTask<WcsResult> mergeBlock(
            Object tag, WcsRequest request,
            WcsCompletedCallback<WcsRequest, WcsResult> completedCallback, Context context) {

        ExecutionContext<WcsRequest> executionContext =
                new ExecutionContext<>(innerClient, request);

        if (completedCallback != null) {
            executionContext.setCompletedCallback(completedCallback);
        }

        ResponseParser<WcsResult> parser = new ResponseParsers.BaseResponseParser();
        Callable<WcsResult> callable = new WcsRequestTask<>(request, parser, executionContext, maxRetryCount);

        return sendRequest(executionContext, callable, context, tag);
    }


}
