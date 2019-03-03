package com.chinanetcenter.wcs.android.listener;

import com.chinanetcenter.wcs.android.utils.EncodeUtils;

import org.json.JSONObject;

import java.util.Iterator;

public abstract class SliceUploaderBase64Listener extends SliceUploaderListener {

    @Override
    public void onSliceUploadSucceed(JSONObject reponseJSON) {
        Iterator<String> iterator = reponseJSON.keys();
        StringBuffer sb = new StringBuffer();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = reponseJSON.optString(key, "");
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key);
            sb.append("=");
            sb.append(value);
        }
        onSliceUploadSucceed(EncodeUtils.urlsafeEncode(sb.toString()));
    }

    public abstract void onSliceUploadSucceed(String string);

}
