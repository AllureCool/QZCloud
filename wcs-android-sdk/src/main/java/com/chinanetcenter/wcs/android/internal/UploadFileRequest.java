package com.chinanetcenter.wcs.android.internal;

import com.chinanetcenter.wcs.android.network.WcsRequest;

import java.util.Map;

/**
 * @author :wangjm1
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.internal
 * @class : ${CLASS_NAME}
 * @time : 2017/5/11 ${ITME}
 * @description :普通上传请求参数
 */
public class UploadFileRequest extends WcsRequest {
    //token中已包含bucket
//    private String bucketName;
    private String objectKey;

    private Map<String, String> callbackParam;

    private Map<String, String> callbackVars;

    //普通上传进度回调（1/10文件回调一次，可在WcsRequestTask修改）
    private WcsProgressCallback<UploadFileRequest> progressCallback;

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public Map<String, String> getCallbackParam() {
        return callbackParam;
    }

    public void setCallbackParam(Map<String, String> callbackParam) {
        this.callbackParam = callbackParam;
    }

    public Map<String, String> getCallbackVars() {
        return callbackVars;
    }

    public void setCallbackVars(Map<String, String> callbackVars) {
        this.callbackVars = callbackVars;
    }

    public WcsProgressCallback<UploadFileRequest> getProgressCallback() {
        return progressCallback;
    }

    public void setProgressCallback(WcsProgressCallback<UploadFileRequest> progressCallback) {
        this.progressCallback = progressCallback;
    }
}
