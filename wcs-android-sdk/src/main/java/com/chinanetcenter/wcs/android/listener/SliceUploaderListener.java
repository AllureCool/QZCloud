package com.chinanetcenter.wcs.android.listener;

import org.json.JSONObject;

import java.util.HashSet;

public abstract class SliceUploaderListener {

    public abstract void onSliceUploadSucceed(JSONObject reponseJSON);

    public abstract void onSliceUploadFailured(HashSet<String> errorMessages);

    public void onProgress(long uploaded, long total) {

    }

}
