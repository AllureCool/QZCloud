package com.chinanetcenter.wcs.android.api;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.chinanetcenter.wcs.android.ClientConfig;
import com.chinanetcenter.wcs.android.Config;
import com.chinanetcenter.wcs.android.LogRecorder;
import com.chinanetcenter.wcs.android.entity.OperationMessage;
import com.chinanetcenter.wcs.android.entity.SliceCache;
import com.chinanetcenter.wcs.android.entity.SliceCacheManager;
import com.chinanetcenter.wcs.android.entity.SliceResponse;
import com.chinanetcenter.wcs.android.internal.SliceUploadRequest;
import com.chinanetcenter.wcs.android.internal.UploadFileRequest;
import com.chinanetcenter.wcs.android.internal.WcsCompletedCallback;
import com.chinanetcenter.wcs.android.internal.WcsProgressCallback;
import com.chinanetcenter.wcs.android.listener.FileUploaderListener;
import com.chinanetcenter.wcs.android.listener.FileUploaderStringListener;
import com.chinanetcenter.wcs.android.listener.SliceUploaderListener;
import com.chinanetcenter.wcs.android.network.HttpMethod;
import com.chinanetcenter.wcs.android.network.WcsRequest;
import com.chinanetcenter.wcs.android.network.WcsResult;
import com.chinanetcenter.wcs.android.slice.Block;
import com.chinanetcenter.wcs.android.slice.ByteArray;
import com.chinanetcenter.wcs.android.slice.Slice;
import com.chinanetcenter.wcs.android.utils.Crc32;
import com.chinanetcenter.wcs.android.utils.EncodeUtils;
import com.chinanetcenter.wcs.android.utils.FileUtil;
import com.chinanetcenter.wcs.android.utils.WCSLogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static com.chinanetcenter.wcs.android.Config.baseUrl;
import static com.chinanetcenter.wcs.android.api.BaseApi.FORM_TOKEN;
import static com.chinanetcenter.wcs.android.api.BaseApi.getInternalRequest;

public class FileUploader {

    private static final String FORM_FILE = "file";
    private static final String FORM_FILE_DESC = "desc";
    public static final String SLICE_UPLOAD_MESSAGE_FORMAT = "[%s] %s";

    private static ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
    //每次运行时参数可能不一样，具体方法内部再赋值(传递不同值的参数)
    private static ParamsConf sParamsConf;
    private static ClientConfig sClientConfig;

    private FileUploader() {
    }

    /**
     * 供测试使用
     *
     * @param conf
     */
    public static void setParams(ParamsConf conf) {
        sParamsConf = conf;
    }

    /**
     * @param uploadUrl upload domain
     * @return 设置是否成功
     */
    public static boolean setUploadUrl(String uploadUrl) {
        if (uploadUrl == null) {
            return false;
        }
        baseUrl = TextUtils.isEmpty(uploadUrl) ? Config.PUT_URL : uploadUrl;
        return true;
    }

    /**
     * 取消上传中的任务
     *
     * @param context 当前任务关联的context，必须与上传时使用到的是同一个实例。
     */
    public static void cancelRequests(final Context context) {

        if (!mExecutorService.isShutdown())
            mExecutorService.shutdownNow();
        getInternalRequest(context, null).cancelRequests(context);
    }

    /**
     * 根据tag、context取消上传中的任务
     *
     * @param context 当前任务关联的context，必须与上传时使用到的是同一个实例。
     * @param tag     上传时设置tag（目前只有分片可以设置。）
     */
    public static void cancelRequests(Context context, Object tag) {
        getInternalRequest(context, null).cancelRequests(context, tag);
    }

    /**
     * @param clientConfig 网络请求相关参数：并发数、超时时间、连接超时时间、重试次数
     *                     注意：需要在任务开始前设置，且只能设置一次
     * @return 设置是否成功
     */
    public static boolean setClientConfig(ClientConfig clientConfig) {
        if (clientConfig == null || clientConfig.getMaxErrorRetry() < 0 || clientConfig.getSocketTimeout() < 0 || clientConfig.getConnectionTimeout() < 0
                || clientConfig.getMaxConcurrentRequest() < 5 || clientConfig.getMaxConcurrentRequest() > 10) {
            return false;
        }
        sClientConfig = clientConfig;
        return true;
    }

    /**
     * 设置分片上传块、片的大小
     *
     * @param blockSize 设置块的大小，默认为4M
     *                  注意：块的大小必须是4M的倍数，最大不能超过100M
     * @param sliceSize 设置片的大小，默认为256KB
     *                  注意：片的大小必须是64K的倍数，最大不能超过块的大小。
     */
    public static void setBlockConfigs(int blockSize, int sliceSize) {
        Block.setBlockSize(blockSize);
        Block.setSliceSize(sliceSize);
    }


    private static String addQueryParameter(String url, Map<String, Object> params) {
        if (params == null) {
            return url;
        }
        StringBuilder sd = new StringBuilder(url + "?");

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            sd.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue()).append("&");
        }
        sd.setLength(sd.length() - 1);
        return sd.toString();
    }

    /**
     * 上传文件到网宿云存储
     *
     * @param context          当前上下文
     * @param token            上传文件所需要的凭证
     * @param fileUri          要上传的文件的URI
     * @param callbackBody     自定义参数及callbackBody，没有则传空
     * @param uploaderListener 上传的回调
     */
    public static void upload(Context context, String token, Uri fileUri, HashMap<String, String> callbackBody, FileUploaderListener uploaderListener) {
        if (null == fileUri || fileUri.toString().trim().equals("")) {
            uploaderListener.onFailure(new OperationMessage(-1, "fileUri no exists "));
            return;
        }
        upload(context, token, FileUtil.getFile(context, fileUri), callbackBody, uploaderListener);
    }

    /**
     * @param context          当前上下文
     * @param token            上传文件所需要的凭证
     * @param filePath         要上传文件的路径
     * @param callbackBody     自定义参数及callbackBody，没有则传空
     * @param uploaderListener 上传的回调
     */
    public static void upload(Context context, String token, String filePath, HashMap<String, String> callbackBody, FileUploaderListener uploaderListener) {
        if (null == filePath || filePath.trim().equals("")) {
            uploaderListener.onFailure(new OperationMessage(-1, "file no exists : " + filePath));
            return;
        }
        upload(context, token, new File(filePath), callbackBody, uploaderListener);
    }

    /**
     * 上传文件到网宿云存储
     *
     * @param context          当前上下文
     * @param token            上传文件所需要的凭证
     * @param file             要上传的文件
     * @param callbackBody     自定义参数及callbackBody，没有则传空
     * @param uploaderListener 上传的回调
     */
    public static void upload(Context context, String token, File file, HashMap<String, String> callbackBody, FileUploaderListener uploaderListener) {
        upload(context, token, file, callbackBody, (FileUploaderStringListener) uploaderListener);
    }

    /**
     * 上传文件到网宿云存储
     *
     * @param context          当前上下文
     * @param token            上传文件所需要的凭证
     * @param file             要上传的文件
     * @param callbackBody     自定义参数及callbackBody，没有则传空
     * @param uploaderListener 上传的回调
     */
    public static void upload(final Context context, final String token,
                              final File file, final HashMap<String, String> callbackBody,
                              final FileUploaderStringListener uploaderListener) {
        final ParamsConf conf = sParamsConf;
        if (null == token || token.trim().equals("")) {
            uploaderListener.onFailure(new OperationMessage(-1, "token invalidate : " + token));
            return;
        }
        if (file == null || !file.exists()) {
            uploaderListener.onFailure(new OperationMessage(-1, "file no exists"));
            return;
        }
        UploadFileRequest request = new UploadFileRequest();
        Map<String, String> params = new HashMap<>();
        if (callbackBody != null) {
            request.setCallbackVars(callbackBody);
            params.putAll(callbackBody);
        }
        //设置进度条
        request.setProgressCallback(new WcsProgressCallback<UploadFileRequest>() {
            @Override
            public void onProgress(UploadFileRequest request, long currentSize, long totalSize) {
                uploaderListener.onProgress(request, currentSize, totalSize);
            }
        });

        params.put(FORM_TOKEN, token);
        params.put(FORM_FILE_DESC, file.getName());
        params.put(FORM_FILE, file.getName());
        if (conf != null) {
            if (!TextUtils.isEmpty(conf.mimeType)) {
                params.put("mimeType", conf.mimeType);
            }
            //分片上传key需要做URL安全的Base64编码；普通上传不需要
            if (!TextUtils.isEmpty(conf.keyName))
                params.put("key", conf.keyName);
        }
        request.setParameters(params);
        request.setMethod(HttpMethod.POST);
        if (conf != null && !TextUtils.isEmpty(conf.fileName)) {
            request.setName(conf.fileName);
        } else {
            request.setName(file.getName());
        }

        //增加头部请求
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "multipart/form-data");
        request.setHeaders(headers);

        String uploadUrlString = baseUrl + "/file/upload";

        request.setUrl(uploadUrlString);

        request.setCallbackParam(params);
        request.setFile(file);

        getInternalRequest(context, sClientConfig).upload(request, uploaderListener, context);

        dump(context, token, uploadUrlString, file.length(), file.getName());
    }

    /**
     * 异步实现的分片上传
     *
     * @param tag                   用于标记分片上传，可取任意值(比如说文件名),只对取消产生影响，不想使用可置为null
     * @param context               上下文
     * @param uploadToken           上传token
     * @param file                  待上传文件
     * @param callbackBody          定制返回给客户端的信息格式
     *                              详见https://wcs.chinanetcenter.com/document/API/Token/PutPolicy/callback
     * @param sliceUploaderListener 分片上传回调
     */
    public static void sliceUpload(final String tag, final Context context, final String uploadToken,
                                   final File file, final HashMap<String, String> callbackBody,
                                   final SliceUploaderListener sliceUploaderListener) {
        final ParamsConf conf = sParamsConf;
        if (null == file || !file.exists()) {
            if (null != sliceUploaderListener) {
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.add(String.format(SLICE_UPLOAD_MESSAGE_FORMAT, -1, "file no exists"));
                sliceUploaderListener.onSliceUploadFailured(hashSet);
            }
            return;
        }
//        if (!file.canRead()) {
//            if (null != sliceUploaderListener) {
//                HashSet<String> hashSet = new HashSet<String>();
//                hashSet.add(String.format(SLICE_UPLOAD_MESSAGE_FORMAT, -1, "access file denied."));
//                sliceUploaderListener.onSliceUploadFailured(hashSet);
//            }
//            return;
//        }
        if (null == context || TextUtils.isEmpty(uploadToken) || TextUtils.isEmpty(getUploadScope(uploadToken))) {
            if (null != sliceUploaderListener) {
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.add(String.format(SLICE_UPLOAD_MESSAGE_FORMAT, -1, "param invalidate"));
                sliceUploaderListener.onSliceUploadFailured(hashSet);
            }
            return;
        }
        final Block[] blocks = Block.blocks(file);
        if (null == blocks || blocks.length <= 0) {
            if (null != sliceUploaderListener) {
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.add(String.format(SLICE_UPLOAD_MESSAGE_FORMAT, -1, "read file failured."));
                sliceUploaderListener.onSliceUploadFailured(hashSet);
            }
            return;
        }
        String fileHash = file.getName() + ":" + getUploadScope(uploadToken);

        //初始化索引
        final SliceCache sliceCache = getSliceCache(fileHash, blocks);
        WCSLogUtil.i("get slice cache " + sliceCache);

        //已上传部分
        long allPersistentSize = getUploadedSize(blocks, sliceCache);
        WCSLogUtil.d(fileHash + "" + " persistent size from cache " + allPersistentSize);

        final int blockCount = blocks.length;

        final long fileSize = blocks[0].getOriginalFileSize();

        final ProgressNotifier progressNotifier = new ProgressNotifier(fileSize, sliceUploaderListener);
        if (allPersistentSize >= fileSize) {
            WCSLogUtil.d("all file uploaded, merge directly");
            mergeBlock(tag, context, uploadToken, fileSize, sliceCache,
                    convertListToString(sliceCache.getBlockContext()), callbackBody,
                    sliceUploaderListener, conf);
            return;
        }

        if (allPersistentSize > 0) {
            progressNotifier.increaseProgressAndNotify(allPersistentSize);
        }
        final int[] success = new int[]{0};
        final int[] failed = new int[]{0};
        final HashSet<String> failedMessages = new HashSet<String>();

        int concurrentLimit = sClientConfig == null ? ClientConfig.DEFAULT_CONCURRENT_REQUEST : sClientConfig.getMaxConcurrentRequest();
        final Semaphore sem = new Semaphore(concurrentLimit);
        final Stack<ByteArray> arrayBuffersList = new Stack<>();
        //初始化缓存
        for (int i = 0; i < concurrentLimit; i++) {
            arrayBuffersList.add(new ByteArray(blocks[0].getSliceSize()));
        }

        for (int i = 0; i < blocks.length; ++i) {
            final int finalI = i;
            if (mExecutorService.isShutdown() || mExecutorService == null) {
                mExecutorService = Executors.newSingleThreadExecutor();
            }
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    ByteArray tmp = null;
                    try {
                        sem.acquire();
                        if (!arrayBuffersList.isEmpty()) {
                            tmp = arrayBuffersList.pop();
                        }
                        final ByteArray finalTmp = tmp;
                        uploadBlock(context, uploadToken, blocks[finalI], finalI, sliceCache, tag,
                                progressNotifier, new UploadBlockListener() {
                                    @Override
                                    public void onBlockUploaded(int blockIndex, String blockContext) {
                                        WCSLogUtil.d("block upload success block index: " + blockIndex);
                                        success[0]++;
//                                    blocks[finalI].releaseBuffer();

                                        if (success[0] == blockCount) {
                                            mergeBlock(tag, context, uploadToken, fileSize, sliceCache,
                                                    convertListToString(sliceCache.getBlockContext()),
                                                    callbackBody,
                                                    sliceUploaderListener, conf);
                                        } else if (success[0] + failed[0] == blockCount) {
                                            if (null != sliceUploaderListener) {
                                                sliceUploaderListener.onSliceUploadFailured(failedMessages);
                                            }

                                        }
                                        if (finalTmp != null) {
                                            arrayBuffersList.add(finalTmp);
                                        }
                                        sem.release();
                                    }

                                    @Override
                                    public void onBlockUploadFailured(int blockIndex,
                                                                      OperationMessage operationMessage) {
                                        String failedMessage = String.format(SLICE_UPLOAD_MESSAGE_FORMAT,
                                                blockIndex, operationMessage.getMessage());
                                        failedMessages.add(failedMessage);
                                        WCSLogUtil.d("block upload failure block index: " + blockIndex);
                                        failed[0]++;
//                                    blocks[finalI].releaseBuffer();

                                        if (success[0] + failed[0] == blockCount || mExecutorService.isShutdown()) {
                                            if (null != sliceUploaderListener) {
                                                sliceUploaderListener.onSliceUploadFailured(failedMessages);
                                            }
                                        }
                                        if (finalTmp != null) {
                                            arrayBuffersList.add(finalTmp);
                                        }
                                        sem.release();
                                    }
                                }, tmp, conf);

                    } catch (InterruptedException e) {
                        WCSLogUtil.e(e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * @param blocks     块数组
     * @param sliceCache
     * @return 已上传文件大小
     */
    private static long getUploadedSize(Block[] blocks, SliceCache sliceCache) {
        long allUploadedSize = 0;
        for (int i = 0; i < sliceCache.getBlockUploadedIndex().size(); i++) {
            Integer persistentIndex = sliceCache.getBlockUploadedIndex().get(i);//每个块的最后片索引
            int uploadingIndexValue = persistentIndex == null ? 0 : persistentIndex.intValue();
            blocks[i].setIndex(uploadingIndexValue);// slice index
            // 持久化的是nextIndex，计算当前需要减一
            WCSLogUtil.d("uploaded index " + uploadingIndexValue + " from " + i);
            allUploadedSize += uploadingIndexValue * blocks[i].getSliceSize();
        }
        return allUploadedSize;
    }

    /**
     * @param fileHash
     * @param blocks
     * @return 缓存的块和片的信息
     */
    private static SliceCache getSliceCache(String fileHash, Block[] blocks) {
        SliceCache sliceCache = SliceCacheManager.getInstance().getSliceCache(fileHash);
        long sliceSize = blocks[0].getSliceSize();
        long blockSize = blocks[0].size();

        //如果块和片的设置变了，则重新上传
        boolean isConfigChanged = false;
        if (null != sliceCache) {
            isConfigChanged = (sliceCache.getBlockUploadedIndex().size() != blocks.length ||
                    sliceSize != sliceCache.getSliceSize() ||
                    blockSize != sliceCache.getBlockSize());
        }
        WCSLogUtil.d("is config changed " + isConfigChanged + ", slice cache: " + sliceCache);

        if (null == sliceCache || isConfigChanged) {
            sliceCache = new SliceCache();
            sliceCache.setUploadBatch(UUID.randomUUID().toString());
            sliceCache.setFileHash(fileHash);
            sliceCache.setBlockContext(new ArrayList<String>());
            sliceCache.setBlockUploadedIndex(new ArrayList<Integer>());
            sliceCache.setSliceSize(sliceSize);
            sliceCache.setBlockSize(blockSize);
            //初始化
            for (int i = 0; i < blocks.length; i++) {
                sliceCache.getBlockUploadedIndex().add(0);
                sliceCache.getBlockContext().add("");
            }
            SliceCacheManager.getInstance().addSliceCache(sliceCache);
        }
        return sliceCache;
    }

    /**
     * 将上传好的所有数据块按指定顺序合并成一个资源文件
     *
     * @param tag                   用于标记分片上传，可取任意值(比如说文件名),只对取消产生影响，不想使用可置为null
     * @param context               上下文
     * @param uploadToken           上传token
     * @param fileSize              文件大小
     * @param sliceCache            缓存的块和片的信息
     * @param contextList           块级上传控制信息列表
     * @param customParams          定制返回给客户端的信息格式
     * @param sliceUploaderListener 分片上传回调
     * @param conf                  一些自定义参数
     */
    private static void mergeBlock(final Object tag, final Context context,
                                   final String uploadToken, final long fileSize,
                                   final SliceCache sliceCache, String contextList,
                                   final HashMap<String, String> customParams,
                                   final SliceUploaderListener sliceUploaderListener,
                                   final ParamsConf conf) {
        WCSLogUtil.d("context list : " + contextList);
        WcsCompletedCallback<WcsRequest, WcsResult> wcsCompletedCallback
                = new WcsCompletedCallback<WcsRequest, WcsResult>() {
            @Override
            public void onSuccess(WcsRequest request, WcsResult result) {
                SliceCacheManager.getInstance().removeSliceCache(sliceCache);

                if (null != sliceUploaderListener) {
                    try {
                        sliceUploaderListener.onSliceUploadSucceed(BaseApi.parseWCSUploadResponse(result));
                    } catch (JSONException e) {
                        onFailure(request, new OperationMessage(e));
                    }
                }
            }

            @Override
            public void onFailure(WcsRequest request, OperationMessage operationMessage) {
                WCSLogUtil.d("merge block failured : " + operationMessage.getMessage());
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.add(String.format(SLICE_UPLOAD_MESSAGE_FORMAT, -1, operationMessage.getMessage()));
                if (null != sliceUploaderListener) {
                    sliceUploaderListener.onSliceUploadFailured(hashSet);
                }
            }


        };

        String mergeUrlString = baseUrl + "/mkfile/" + fileSize;
        StringBuilder mergeUrlStringBuffer = new StringBuilder();
        if (null != customParams && customParams.size() > 0)

        {
            for (String key : customParams.keySet()) {
                String value = customParams.get(key);
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    mergeUrlStringBuffer.append("/");
                    mergeUrlStringBuffer.append(key);
                    mergeUrlStringBuffer.append("/");
                    mergeUrlStringBuffer.append(EncodeUtils.urlsafeEncode(value));
                }
            }
            mergeUrlString += mergeUrlStringBuffer.toString();
        }


        WcsRequest request = new WcsRequest();
        request.setMethod(HttpMethod.POST);
        request.setUrl(mergeUrlString);
        request.setUploadData(contextList.getBytes());
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", uploadToken);
        headers.put("UploadBatch", sliceCache.getUploadBatch());
        headers.put("Content-Type", "text/plain");
        if (conf != null) {
            if (!TextUtils.isEmpty(conf.mimeType)) {
                headers.put("MimeType", conf.mimeType);
            }
            //key需要URL安全的Base64编码
            if (!TextUtils.isEmpty(conf.keyName))
                headers.put("Key", EncodeUtils.urlsafeEncode(conf.keyName));
        }
        request.setHeaders(headers);

        getInternalRequest(context, sClientConfig).mergeBlock(tag, request, wcsCompletedCallback, context);

        dump(context, uploadToken, mergeUrlString, fileSize, "unknown");
    }

    /**
     * 上传块
     *
     * @param context             上下文
     * @param uploadToken         上传token
     * @param block               块
     * @param blockIndex          块的索引
     * @param sliceCache          缓存的块和片的信息
     * @param tag                 用于标记分片上传，可取任意值(比如说文件名),只对取消产生影响，不想使用可置为null
     * @param progressNotifier    进度提示
     * @param uploadBlockListener 块上传回调
     * @param byteArray           存放当前片
     * @param conf                一些自定义参数
     */
    private static void uploadBlock(final Context context,
                                    final String uploadToken, final Block block, final int blockIndex,
                                    final SliceCache sliceCache,
                                    final Object tag, final ProgressNotifier progressNotifier,
                                    final UploadBlockListener uploadBlockListener,
                                    final ByteArray byteArray,
                                    final ParamsConf conf) {


        //设置缓存
        block.setByteArray(byteArray);
        final int currentIndex = block.getIndex();
        Slice slice = block.moveToNext();
        if (null != slice && currentIndex == 0) { //  第一片
            makeBlock(tag, context, uploadToken, block, blockIndex, slice, sliceCache, progressNotifier, uploadBlockListener, conf);
        } else if (null != slice) {
            uploadSlice(tag, context, uploadToken, block, blockIndex, slice,
                    sliceCache, sliceCache.getBlockContext().get(blockIndex), progressNotifier,
                    uploadBlockListener, conf);
        } else {//分片已经完成
//            block.releaseBuffer();//释放缓存
            uploadBlockListener.onBlockUploaded(blockIndex, sliceCache.getBlockContext().get(blockIndex));
        }
    }

    /**
     * 生成一个block，同时上传第一片
     *
     * @param tag                 用于标记分片上传，可取任意值(比如说文件名),只对取消产生影响，不想使用可置为null
     * @param context             上下文
     * @param uploadToken         上传token
     * @param block               块
     * @param blockIndex          块的索引
     * @param slice               当前片
     * @param sliceCache          缓存的块和片的信息
     * @param progressNotifier    进度提示
     * @param uploadBlockListener 块上传回调
     * @param conf                一些自定义参数
     */
    private static void makeBlock(final Object tag, final Context context, final String uploadToken, final Block block, final int blockIndex, final Slice slice, final SliceCache sliceCache, final ProgressNotifier progressNotifier, final UploadBlockListener uploadBlockListener, final ParamsConf conf) {
        WcsCompletedCallback<WcsRequest, SliceResponse> wcsCompletedCallback =
                new WcsCompletedCallback<WcsRequest, SliceResponse>() {
                    @Override
                    public void onSuccess(WcsRequest request, SliceResponse result) {
                        uploadNextSlice(tag, result, blockIndex, block, slice, context, uploadToken,
                                sliceCache, progressNotifier, uploadBlockListener, conf);
                    }

                    @Override
                    public void onFailure(WcsRequest request, OperationMessage operationMessage) {
                        WCSLogUtil.d("block index failured : " + blockIndex + ", onFailure : " +
                                operationMessage.getMessage());
                        uploadBlockListener.onBlockUploadFailured(blockIndex, operationMessage);
                    }
                };
        String initBlockUrl = baseUrl + "/mkblk/" + block.size() + "/" + blockIndex;

        SliceUploadRequest request = new SliceUploadRequest();
        request.setMethod(HttpMethod.POST);
        request.setUrl(initBlockUrl);
        request.setUploadData(slice.toByteArray());
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", uploadToken);
        headers.put("UploadBatch", sliceCache.getUploadBatch());
        headers.put("Content-Type", "application/octet-stream");
        if (conf != null) {
            if (!TextUtils.isEmpty(conf.mimeType)) {
                headers.put("MimeType", conf.mimeType);
            }
            if (!TextUtils.isEmpty(conf.keyName))
                headers.put("Key", EncodeUtils.urlsafeEncode(conf.keyName));
        }
        request.setHeaders(headers);

        //设置进度
        request.setProgressCallback(new WcsProgressCallback<WcsRequest>() {
            @Override
            public void onProgress(WcsRequest request, long currentSize, long totalSize) {
                progressNotifier.increaseProgressAndNotify(currentSize);
            }
        });

        getInternalRequest(context, sClientConfig).uploadBlock(tag, request, wcsCompletedCallback, context);

        dump(context, uploadToken, initBlockUrl, slice.size(), block.getOriginalFileName());
    }

    /**
     * 上传指定块的一片数据，具体数据量可根据现场环境调整。同一块的每片数据必须串行上传。
     *
     * @param tag                 用于标记分片上传，可取任意值(比如说文件名),只对取消产生影响，不想使用可置为null
     * @param context             上下文
     * @param uploadToken         上传token
     * @param block               块
     * @param blockIndex          块的索引
     * @param slice               片
     * @param sliceCache          缓存的块和片的信息
     * @param blockContext        块级上传控制信息
     * @param progressNotifier    进度提示
     * @param uploadBlockListener 块上传回调
     * @param conf                一些自定义参数
     */
    private static void uploadSlice(final Object tag, final Context context,
                                    final String uploadToken, final Block block, final int blockIndex,
                                    final Slice slice, final SliceCache sliceCache, String blockContext,
                                    final ProgressNotifier progressNotifier,
                                    final UploadBlockListener uploadBlockListener,
                                    final ParamsConf conf) {
        WcsCompletedCallback<WcsRequest, SliceResponse> wcsCompletedCallback = new WcsCompletedCallback<WcsRequest, SliceResponse>() {
            @Override
            public void onSuccess(WcsRequest request, SliceResponse result) {
                uploadNextSlice(tag, result, blockIndex, block, slice, context, uploadToken,
                        sliceCache, progressNotifier, uploadBlockListener, conf);
            }

            @Override
            public void onFailure(WcsRequest request, OperationMessage operationMessage) {
//                block.releaseBuffer();
                WCSLogUtil.d("block index failured : " + blockIndex + ", onFailure : " + operationMessage.getMessage());
                uploadBlockListener.onBlockUploadFailured(blockIndex, operationMessage);
            }
        };


        String uploadSliceUrl = baseUrl + "/bput/" + blockContext + "/" + slice.getOffset();

        SliceUploadRequest request = new SliceUploadRequest();
        request.setMethod(HttpMethod.POST);
        request.setUrl(uploadSliceUrl);
        request.setUploadData(slice.toByteArray());
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", uploadToken);
        headers.put("UploadBatch", sliceCache.getUploadBatch());
        headers.put("Content-Type", "application/octet-stream");

        if (conf != null) {
            if (!TextUtils.isEmpty(conf.mimeType)) {
                headers.put("MimeType", conf.mimeType);
            }
            if (!TextUtils.isEmpty(conf.keyName))
                headers.put("Key", EncodeUtils.urlsafeEncode(conf.keyName));
        }
        request.setHeaders(headers);

        //设置进度
        request.setProgressCallback(new WcsProgressCallback<WcsRequest>() {
            @Override
            public void onProgress(WcsRequest request, long currentSize, long totalSize) {
                progressNotifier.increaseProgressAndNotify(currentSize);
            }
        });

        getInternalRequest(context, sClientConfig).uploadBlock(tag, request, wcsCompletedCallback, context);

        dump(context, uploadToken, uploadSliceUrl, slice.size(), block.getOriginalFileName());
    }

    /**
     * 根据返回的片信息，验证上传的片是否完整，是则继续下一片，否则重传
     *
     * @param tag                 用于标记分片上传，可取任意值(比如说文件名),只对取消产生影响，不想使用可置为null
     * @param sliceResponse       上传完片的响应信息：偏移量等
     * @param blockIndex          块的序列号
     * @param block               块
     * @param lastSlice           刚上传的片
     * @param context             上下文
     * @param uploadToken         上传token
     * @param sliceCache          缓存的块和片的信息
     * @param progressNotifier    进度提示
     * @param uploadBlockListener 块上传回调
     * @param conf                一些自定义参数
     */
    private static void uploadNextSlice(Object tag, SliceResponse sliceResponse,
                                        int blockIndex,
                                        Block block, Slice lastSlice, Context context, String uploadToken,
                                        SliceCache sliceCache, ProgressNotifier progressNotifier,
                                        UploadBlockListener uploadBlockListener,
                                        ParamsConf conf) {
        WCSLogUtil.d("block index : " + blockIndex + ";Thread : " + Thread.currentThread().getName() + ";slice index: " + block.getIndex() + "; uploadSlice slice response : " + sliceResponse);
        if (sliceResponse.crc32 == 0) {
            uploadBlockListener.onBlockUploadFailured(blockIndex, new OperationMessage(0, "sliceResponse incorrect, " + sliceResponse.getHeaders()));
        } else if (Crc32.calc(lastSlice.toByteArray()) == sliceResponse.crc32) {
            //校验成功的情况，继续上传下一片
            sliceCache.getBlockContext().set(blockIndex, sliceResponse.context);//ctx
            sliceCache.getBlockUploadedIndex().set(blockIndex, block.getIndex());
            WCSLogUtil.d("uploadSlice correctly. save sliceCache" + " .block index : " + blockIndex + ";slice index: " + block.getIndex());
            Slice nextSlice = block.moveToNext();
            if (null != nextSlice) {
                uploadSlice(tag, context, uploadToken, block, blockIndex, nextSlice, sliceCache,
                        sliceResponse.context, progressNotifier, uploadBlockListener, conf);
            } else {
                WCSLogUtil.d("get empty slice while upload next slice");
                uploadBlockListener.onBlockUploaded(blockIndex, sliceResponse.context);
            }
        } else {
            //重传
            //crc32不一致，重新上传，使用之前的offset和context
            int retry = lastSlice.getRetry();
            if (retry >= Slice.SLICE_MAX_RETRY) {
                uploadBlockListener.onBlockUploadFailured(blockIndex, new OperationMessage(0, "crc32 incorrect, " + sliceResponse));
                return;
            }
            WCSLogUtil.d("crc32 incorrect,retry");
            lastSlice.setRetry(++retry);
            progressNotifier.decreaseProgress(lastSlice.toByteArray().length);
            if (lastSlice.getOffset() == 0) {
                //第一片
                makeBlock(tag, context, uploadToken, block, blockIndex, lastSlice,
                        sliceCache, progressNotifier, uploadBlockListener, conf);
            } else {
                uploadSlice(tag, context, uploadToken, block, blockIndex, lastSlice,
                        sliceCache, sliceCache.getBlockContext().get(blockIndex), progressNotifier, uploadBlockListener, conf);
            }
        }
        SliceCacheManager.getInstance().dumpAll();
    }


    private static String convertListToString(ArrayList<String> contexts) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < contexts.size(); i++) {
            sb.append(contexts.get(i));
            if (i + 1 < contexts.size()) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 格式：uploadToken = AccessKey:encodedSign:encodePutPolicy
     * putpolicy
     * {
     * "scope": "<bucket string>",
     * "deadline": "<deadline string>",
     * "returnBody": "<returnBody string>",
     * "overwrite": "<overwrite int>",
     * "fsizeLimit": "<fsizeLimit long>",
     * "returnUrl": "<returnUrl string>"
     * }
     *
     * @param uploadToken
     * @return
     */
    private static String getUploadScope(String uploadToken) {
        String[] uploadTokenArray = uploadToken.split(":");
        if (uploadTokenArray.length != 3) {
            return "";
        }
        String policyJsonString = EncodeUtils.urlsafeDecodeString(uploadTokenArray[2]);
        String scope = " ";
        try {
            JSONObject jsonObject = new JSONObject(policyJsonString);
            scope = jsonObject.optString("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return scope;
    }

    /**
     * 打印日志
     *
     * @param context
     * @param token
     * @param urlString
     * @param length
     * @param fileName
     */
    private static void dump(Context context, String token, String urlString,
                             long length, String fileName) {
//        String userAgent = HttpProtocolParams.getUserAgent(getAsyncClient(context).getHttpClient().getParams());
        String userAgent = "underfined";
        long timestamp = System.currentTimeMillis();
        String string2dump = String.format(
                "### url : %s,\r\n ### time : %s,\r\n ### token : %s,\r\n ### fileName : %s,\r\n ### length : %s,\r\n ### userAgent : %s\r\n", urlString,
                timestamp, token, fileName, length, userAgent);
        LogRecorder.getInstance().dumpLog(string2dump);
    }

    private interface UploadBlockListener {

        public void onBlockUploaded(int blockIndex, String context);

        public void onBlockUploadFailured(int blockIndex, OperationMessage operationMessage);

    }

}

