package com.chinanetcenter.wcs.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class MultiOperationMessage {

    private static final String UNKNOWN = "unknown";

    private String bucketName = UNKNOWN;
    private String fileName = UNKNOWN;
    private String originalFileName = UNKNOWN;
    private String message = UNKNOWN;
    private int code;

    public static MultiOperationMessage fromJsonString(JSONObject jsonObject) {
        MultiOperationMessage operationMessage = new MultiOperationMessage();
        operationMessage.code = jsonObject.optInt("code", -1);
        operationMessage.message = jsonObject.optString("message", UNKNOWN);
        operationMessage.fileName = jsonObject.optString("fileName", UNKNOWN);
        operationMessage.bucketName = jsonObject.optString("bucketName", UNKNOWN);
        operationMessage.originalFileName = jsonObject.optString("originFileName", UNKNOWN);
        return operationMessage;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("code", code);
            jsonObject.putOpt("message", message);
            jsonObject.putOpt("originalFileName", originalFileName);
            jsonObject.putOpt("fileName", fileName);
            jsonObject.putOpt("bucketName", bucketName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
