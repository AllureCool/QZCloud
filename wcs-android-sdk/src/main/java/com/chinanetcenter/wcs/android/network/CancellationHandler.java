package com.chinanetcenter.wcs.android.network;

import okhttp3.Call;

/**
 * @author :wangjm1
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.network
 * @class : ${CLASS_NAME}
 * @time : 2017/5/10 ${ITME}
 * @description :TODO
 */
public class CancellationHandler {
    private volatile boolean isCancelled;
    private volatile boolean isFinished;

    private volatile Call call;
    private Object tag;

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
        isCancelled = true;
    }

    public boolean isCancelled() {
        boolean cancelledByOther = false;
        if (call != null) {
            cancelledByOther = call.isCanceled();
        }
        return isCancelled || cancelledByOther;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public boolean shouldBeGarbageCollected() {
        return isCancelled() || isFinished();
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }
}
