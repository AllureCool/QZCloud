package com.chinanetcenter.wcs.android.entity;

import android.text.TextUtils;

import com.chinanetcenter.wcs.android.network.WcsResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author :wangjm1
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.entity
 * @class : ${CLASS_NAME}
 * @time : 2017/5/15 ${ITME}
 * @description :TODO
 */
public class MergeBlockResult extends WcsResult {

    public String message;//错误信息
    //成功返回后的信息
    public String hash;
    public String key;

    public static void fromJsonString(MergeBlockResult mergeBlockResult, String jsonString) {
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject.has("code")) {
                    mergeBlockResult.setStatusCode(jsonObject.optInt("code", 0));
                }
                if (jsonObject.has("message")) {
                    mergeBlockResult.message = jsonObject.optString("message", "");
                }
                if (jsonObject.has("hash")) {
                    mergeBlockResult.hash = jsonObject.optString("hash", "");
                }
                if (jsonObject.has("key")) {
                    mergeBlockResult.key = jsonObject.optString("key", "");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static MergeBlockResult fromJsonString(String jsonString) {
        MergeBlockResult mergeBlockResult = new MergeBlockResult();
        if (!TextUtils.isEmpty(jsonString)) {
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                if (jsonObject.has("code")) {
                    mergeBlockResult.setStatusCode(jsonObject.optInt("code", 0));
                }
                if (jsonObject.has("message")) {
                    mergeBlockResult.message = jsonObject.optString("message", "");
                }
                if (jsonObject.has("hash")) {
                    mergeBlockResult.hash = jsonObject.optString("hash", "");
                }
                if (jsonObject.has("key")) {
                    mergeBlockResult.key = jsonObject.optString("key", "");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mergeBlockResult;
    }

}
