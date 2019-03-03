package com.chinanetcenter.wcs.android.entity;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class FileInfo {

    private boolean result;

    private int code;

    private String message;

    private String mimeType;

    private String name;

    private long fileSize;

    private long createDate;

    private String hash;

    public static FileInfo fromJsonString(String jsonString) {
        FileInfo fileInfo = new FileInfo();
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                fileInfo.code = jsonObject.optInt("code", 500);
                fileInfo.message = jsonObject.optString("message", "服务器内部错误");
                fileInfo.mimeType = jsonObject.optString("mimeType", "unknown");
                fileInfo.name = jsonObject.optString("name", "fileName");
                fileInfo.fileSize = jsonObject.optLong("fileSize", 0);
                fileInfo.createDate = jsonObject.optLong("createDate", System.currentTimeMillis());
                fileInfo.hash = jsonObject.optString("hash", "-1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return fileInfo;
    }

    /**
     * 操作对应的状态码
     *
     * @return
     */
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 操作对应的信息
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 文件的MIME类型，字符串格式为"image/png"
     *
     * @return
     */
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * 文件名
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 文件大小
     *
     * @return
     */
    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 文件创建的时间戳
     *
     * @return
     */
    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    /**
     * 文件的hash值
     *
     * @return
     */
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("code", code);
            jsonObject.putOpt("message", message);
            jsonObject.putOpt("mimeType", mimeType);
            jsonObject.putOpt("name", name);
            jsonObject.putOpt("fileSize", fileSize);
            jsonObject.putOpt("createDate", createDate);
            jsonObject.putOpt("hash", hash);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
