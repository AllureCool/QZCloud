package com.chinanetcenter.wcs.android.api;

enum LocalResultCode {

    FILE_NOT_FOUND(1001, "文件不存在。"),

    UNKOWN(4000, "未知异常。");

    int code;

    String errorMsg;

    LocalResultCode(int code, String msg) {
        this.code = code;
        this.errorMsg = msg;
    }

    static LocalResultCode getInstance(int code) {
        for (LocalResultCode resultCode : values()) {
            if (resultCode.code == code) {
                return resultCode;
            }
        }
        return UNKOWN;
    }
}
