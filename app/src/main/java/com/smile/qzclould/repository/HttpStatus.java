package com.smile.qzclould.repository;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

public class HttpStatus {
    @SerializedName("token")
    private String mToken;

    public String getToken() {
        return mToken;
    }

    /**
     * API是否请求失败
     *
     * @return 失败返回true, 成功返回false
     */
    public boolean hasToken() {
        return !TextUtils.isEmpty(mToken);
    }
}
