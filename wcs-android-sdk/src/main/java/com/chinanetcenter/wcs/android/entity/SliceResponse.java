package com.chinanetcenter.wcs.android.entity;

import android.text.TextUtils;

import com.chinanetcenter.wcs.android.network.WcsResult;

import org.json.JSONException;
import org.json.JSONObject;

public class SliceResponse extends WcsResult {

    /**
     * 下一个上传片在切割块中的偏移。若片大小与块大小相等，则该返回值为该块的大小。
     */
    public long offset;

    /**
     * 本次上传成功后的块级上传控制信息，用于后续上传片及生成文件。本字段是只能被WCS服务器解读使用的不透明字段，上传端不应修改其内容。
     * 每次返回的<ctx>都只对应紧随其后的下一个上传数据片，上传非对应数据片会返回401状态码。
     */
    public String context;

    /**
     * 上传块Crc32，客户可通过此字段对上传块的完整性进行较验。
     */
    public long crc32;

    /**
     * 上传块校验码。
     */
    public String md5;

    public static void fromJsonString(SliceResponse sliceResponse, String jsonString) throws JSONException {
        if (!TextUtils.isEmpty(jsonString)) {
            JSONObject jsonObject = new JSONObject(jsonString);
            sliceResponse.offset = jsonObject.optLong("offset", 0);
            sliceResponse.context = jsonObject.optString("ctx", "0");
            sliceResponse.crc32 = jsonObject.optLong("crc32", 0);
            sliceResponse.md5 = jsonObject.optString("checksum", "0");
            if (sliceResponse.crc32 == 0 || "0".equals(sliceResponse.context)) {
                throw new JSONException("crc32 or context not found: " + jsonString);
            }
        }
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.putOpt("offset", offset);
            jsonObject.putOpt("context", context);
            jsonObject.putOpt("crc32", crc32);
            jsonObject.putOpt("md5", md5);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
