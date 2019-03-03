package com.chinanetcenter.wcs.android.exception;

/**
 * @author :yanghuan
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.exception
 * @class : ClientException
 * @time : 2017/5/11 17:08
 * @description :
 */

public class ClientException extends Exception {
    private Boolean canceled = false;

    /**
     * 构造新实例。
     */
    public ClientException() {
        super();
    }

    /**
     * 用给定的异常信息构造新实例。
     *
     * @param message 异常信息。
     */
    public ClientException(String message) {
//        super("[ErrorMessage]: " + message);
        super(message);
    }

    /**
     * 用表示异常原因的对象构造新实例。
     *
     * @param cause 异常原因。
     */
    public ClientException(Throwable cause) {
        super(cause);
    }

    /**
     * 用异常消息和表示异常原因的对象构造新实例。
     *
     * @param message 异常信息。
     * @param cause   异常原因。
     */
    public ClientException(String message, Throwable cause) {
        this(message, cause, false);
    }

    /**
     * 构造取消导致的异常
     */
    public ClientException(String message, Throwable cause, Boolean isCancelled) {
        super(message, cause);
//        super("[ErrorMessage]: " + message, cause);
        this.canceled = isCancelled;
    }

    /**
     * 检查异常是否是因为取消而产生
     *
     * @return
     */
    public Boolean isCanceledException() {
        return canceled;
    }

}
