package com.chinanetcenter.wcs.android.listener;

import com.chinanetcenter.wcs.android.entity.MergeBlockResult;
import com.chinanetcenter.wcs.android.entity.OperationMessage;
import com.chinanetcenter.wcs.android.internal.UploadFileRequest;
import com.chinanetcenter.wcs.android.internal.WcsCompletedCallback;

/**
 * @author :wangjm1
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.listener
 * @class : ${CLASS_NAME}
 * @time : 2017/5/15 ${ITME}
 * @description :TODO
 */
public abstract class NewMultiUploadListener implements WcsCompletedCallback<UploadFileRequest, MergeBlockResult> {

    @Override
    public void onSuccess(UploadFileRequest request, MergeBlockResult result) {
        if (result.getStatusCode() == 200) {
            onSuccess(result.key, result.hash);
        } else {
            onFailure(result.getStatusCode(), result.message);
        }

    }

    @Override
    public void onFailure(UploadFileRequest request, OperationMessage operationMessage) {

    }

    public abstract void onSuccess(String key, String hash);

    public abstract void onFailure(int status, String message);
}
