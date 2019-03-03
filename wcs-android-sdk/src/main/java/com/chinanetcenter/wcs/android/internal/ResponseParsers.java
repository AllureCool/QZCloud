package com.chinanetcenter.wcs.android.internal;

import android.support.annotation.NonNull;

import com.chinanetcenter.wcs.android.entity.SliceResponse;
import com.chinanetcenter.wcs.android.network.ResponseParser;
import com.chinanetcenter.wcs.android.network.WcsResult;
import com.chinanetcenter.wcs.android.utils.WCSLogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Response;

/**
 * @author :wangjm1
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android.internal
 * @class : ${CLASS_NAME}
 * @time : 2017/5/11 ${ITME}
 * @description :TODO
 */
public class ResponseParsers {


    public static class BaseResponseParser implements ResponseParser<WcsResult> {

        @Override
        public WcsResult parse(Response response) throws IOException {
            WcsResult result = new WcsResult();
            setWcsResult(response, result);
            return result;
        }
    }

    @NonNull
    private static void setWcsResult(Response response, WcsResult result) throws IOException {
        result.setStatusCode(response.code());
        Map<String, String> responseHeader = parseResponseHeader(response);
        result.setResponseHeader(responseHeader);
        if (!response.isSuccessful()) {
            result.setRequestId(responseHeader.get(WcsResult.REQUEST_ID));
        }
        String data;
        if (response.body() == null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("code", 500);
                jsonObject.put("message", "服务器内部错误");
                data = jsonObject.toString();
            } catch (JSONException e) {
                WCSLogUtil.e(e.getMessage());
                data = "Service error";
            }
        } else {
            data = response.body().string();
        }
        result.setResponse(data);

    }

    public static class UploadResponseParser implements ResponseParser<UploadFileResult> {
        @Override
        public UploadFileResult parse(Response response) throws IOException {
            UploadFileResult result = new UploadFileResult();
            setWcsResult(response, result);
            return result;
        }
    }

    public static class UploadBlockResponseParser implements ResponseParser<SliceResponse> {
        @Override
        public SliceResponse parse(Response response) throws IOException, JSONException {
            SliceResponse result = new SliceResponse();
            setWcsResult(response, result);
            if (response.isSuccessful()) {
                SliceResponse.fromJsonString(result, result.getResponse());
            }
            return result;
        }
    }

    private static String parseResponse(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;
        while ((count = in.read(data, 0, 1024)) != -1) {
            outputStream.write(data, 0, count);
        }
        data = null;
        String jsonStr = new String(outputStream.toByteArray(), "utf-8");

        outputStream.close();
        return jsonStr;
    }


    public static void safeCloseResponse(Response response) {
        try {
            response.body().close();
        } catch (Exception e) {
        }
    }

    public static Map<String, String> parseResponseHeader(Response response) {
        Map<String, String> result = new HashMap<String, String>();
        Headers headers = response.headers();
        for (int i = 0; i < headers.size(); i++) {
            result.put(headers.name(i), headers.value(i));
        }
        return result;
    }
}
