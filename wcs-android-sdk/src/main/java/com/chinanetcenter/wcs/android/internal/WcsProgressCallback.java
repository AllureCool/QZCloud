package com.chinanetcenter.wcs.android.internal;

/**
 * @author :wangjm1
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.internal
 * @class : ${CLASS_NAME}
 * @time : 2017/5/10 ${ITME}
 * @description :进度回调接口。
 */
public interface WcsProgressCallback<T> {
    public void onProgress(T request, long currentSize, long totalSize);
}
