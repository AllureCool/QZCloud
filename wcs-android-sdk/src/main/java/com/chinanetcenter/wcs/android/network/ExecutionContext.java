package com.chinanetcenter.wcs.android.network;

import com.chinanetcenter.wcs.android.internal.WcsCompletedCallback;
import com.chinanetcenter.wcs.android.internal.WcsProgressCallback;

import okhttp3.OkHttpClient;

/**
 * @author :wangjm1
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.network
 * @class : ${CLASS_NAME}
 * @time : 2017/5/10 ${ITME}
 * @description :TODO
 */
public class ExecutionContext<T> {
    private T request;
    private OkHttpClient client;
    private CancellationHandler cancellationHandler;

    private WcsCompletedCallback completedCallback;
    private WcsProgressCallback progressCallback;

    public ExecutionContext(OkHttpClient client, T request) {
        this.client = client;
        this.request = request;
    }

    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public void setClient(OkHttpClient client) {
        this.client = client;
    }

    public CancellationHandler getCancellationHandler() {
        return cancellationHandler;
    }

    public void setCancellationHandler(CancellationHandler cancellationHandler) {
        this.cancellationHandler = cancellationHandler;
    }

    public WcsCompletedCallback getCompletedCallback() {
        return completedCallback;
    }

    public void setCompletedCallback(WcsCompletedCallback completedCallback) {
        this.completedCallback = completedCallback;
    }

    public WcsProgressCallback getProgressCallback() {
        return progressCallback;
    }

    public void setProgressCallback(WcsProgressCallback progressCallback) {
        this.progressCallback = progressCallback;
    }

}
