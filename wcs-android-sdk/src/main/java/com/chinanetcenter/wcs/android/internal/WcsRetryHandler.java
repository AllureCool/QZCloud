package com.chinanetcenter.wcs.android.internal;

import com.chinanetcenter.wcs.android.exception.ClientException;
import com.chinanetcenter.wcs.android.exception.ServiceException;
import com.chinanetcenter.wcs.android.utils.WCSLogUtil;

import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLException;

/**
 * @author :yanghuan
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.internal
 * @class : WcsRetryHandler
 * @time : 2017/5/11 16:38
 * @description :重试
 */

public class WcsRetryHandler {
    // retry-this, since it may happens as part of a Wi-Fi to 3G failover
    //UnknownHostException
    // retry-this, since it may happens as part of a Wi-Fi to 3G failover
    //SocketException
    // retry-this, may be connect timeout
    //InterruptedIOException

    // never retry SSL handshake failures
    //SSLException

    private int maxRetryCount = 2;

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public WcsRetryHandler() {
    }

    public WcsRetryHandler(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public boolean shouldRetry(Exception e, int currentRetryCount) {
        if (currentRetryCount >= maxRetryCount) {
            return false;
        }
        if (e instanceof ClientException) {
            if (((ClientException) e).isCanceledException()) {
                return false;
            }

            Exception localException = (Exception) e.getCause();
            if (localException instanceof InterruptedIOException
                && !(localException instanceof SocketTimeoutException)) {
                WCSLogUtil.e("[shouldRetry] - is interrupted!");
                return false;
            } else if (localException instanceof IllegalArgumentException) {
                return false;
            } else if (localException instanceof SSLException) {
                return false;
            }
            WCSLogUtil.d("shouldRetry - " + e.toString());
            e.getCause().printStackTrace();
            return true;
        } else if (e instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) e;
            if (serviceException.getStatusCode() == 408 || (serviceException.getStatusCode() >= 500 &&
                serviceException.getStatusCode() != 579)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

}
