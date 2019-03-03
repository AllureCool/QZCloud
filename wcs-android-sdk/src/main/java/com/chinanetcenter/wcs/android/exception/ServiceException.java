package com.chinanetcenter.wcs.android.exception;

/**
 * @author :yanghuan
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.exception
 * @class : ServiceException
 * @time : 2017/5/11 17:08
 * @description : 服务端返回的错误消息。
 */

public class ServiceException extends Exception {
    /**
     * http状态码
     */
    private int statusCode;

    private String requestId;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * 用异常消息和表示异常原因及其他信息的对象构造新实例。
     *
     * @param statusCode      HTTP状态码  579特例
     * @param requestId
     * @param message         {
     *                        "code":     "<code string>",
     *                        "message":  "<message string>"
     *                        }
     */
    public ServiceException(int statusCode, String requestId, String message) {

        super(message);

        this.statusCode = statusCode;
        this.requestId = requestId;
    }
}
