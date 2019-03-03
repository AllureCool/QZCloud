package com.chinanetcenter.wcs.android.entity;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class ImageInfo {

    private String message;

    private String width;

    private String height;

    private String size;

    private String colorMode;

    public static ImageInfo fromJsonString(String jsonString) {
        ImageInfo imageInfo = new ImageInfo();
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                imageInfo.message = jsonObject.optString("message", "服务器内部错误");
                imageInfo.width = jsonObject.optString("width", "0 pixel");
                imageInfo.height = jsonObject.optString("height", "0 pixel");
                imageInfo.size = jsonObject.optString("size", "0.0 KB");
                imageInfo.colorMode = jsonObject.optString("colorMode", "unknown");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return imageInfo;
    }

    /**
     * 操作结果对应的信息
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
     * 图片宽度，字符串的格式为"0 pixel"
     *
     * @return
     */
    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * 图片高度，字符串的格式为"0 pixel"
     *
     * @return
     */
    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * 图片大小，字符串格式为"0.0 KB"
     *
     * @return
     */
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    /**
     * 图片的色彩模式，字符串格式为"RGB"
     *
     * @return
     */
    public String getColorMode() {
        return colorMode;
    }

    public void setColorMode(String colorMode) {
        this.colorMode = colorMode;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("message", message);
            jsonObject.putOpt("width", width);
            jsonObject.putOpt("height", height);
            jsonObject.putOpt("size", size);
            jsonObject.putOpt("colorMode", colorMode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
