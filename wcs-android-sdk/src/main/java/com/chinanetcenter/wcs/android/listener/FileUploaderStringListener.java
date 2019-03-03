package com.chinanetcenter.wcs.android.listener;

import com.chinanetcenter.wcs.android.entity.OperationMessage;
import com.chinanetcenter.wcs.android.internal.UploadFileRequest;
import com.chinanetcenter.wcs.android.internal.UploadFileResult;
import com.chinanetcenter.wcs.android.internal.WcsCompletedCallback;
import com.chinanetcenter.wcs.android.internal.WcsProgressCallback;


public abstract class FileUploaderStringListener implements
        WcsCompletedCallback<UploadFileRequest, UploadFileResult>,
        WcsProgressCallback<UploadFileRequest> {
    UploadFileResult result;

    @Override
    public void onProgress(UploadFileRequest request, long currentSize, long totalSize) {


    }

    @Override
    public void onSuccess(UploadFileRequest request, UploadFileResult result) {
        this.result = result;
        onSuccess(result.getStatusCode(), result.getResponse());
    }

    @Override
    public void onFailure(UploadFileRequest request, OperationMessage message) {

        onFailure(message);
    }

    /**
     * 文件上传成功之后回调
     *
     * @param status
     * @param responseString
     */
    public abstract void onSuccess(int status, String responseString);

    /**
     * 文件上传失败之后的回调
     *
     * @param operationMessage 操作对应的信息
     */
    public abstract void onFailure(OperationMessage operationMessage);


}
