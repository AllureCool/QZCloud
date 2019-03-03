package com.chinanetcenter.wcs.android.internal;

import com.chinanetcenter.wcs.android.network.WcsRequest;

/**
 * @author :yanghuan
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.internal
 * @class : SliceUploadRequest
 * @time : 2017/5/23 15:08
 * @description :分片上传请求参数
 */

public class SliceUploadRequest extends WcsRequest {

    //分片上传进度回调（每片回调一次）
    private WcsProgressCallback<WcsRequest> progressCallback;

    public WcsProgressCallback<WcsRequest> getProgressCallback() {
        return progressCallback;
    }

    public void setProgressCallback(WcsProgressCallback<WcsRequest> progressCallback) {
        this.progressCallback = progressCallback;
    }
}
