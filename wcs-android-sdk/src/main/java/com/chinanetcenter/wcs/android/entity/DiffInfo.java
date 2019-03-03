package com.chinanetcenter.wcs.android.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class DiffInfo {

    private int status;
    private String diffHash;
    private String finalHash;
    private long diffSize;
    private long finalSize;
    private String diffUrl;

    // TODO: parse json
    public static DiffInfo fromJSONString(String jsonString) {
        DiffInfo diffInfo = new DiffInfo();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return diffInfo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDiffHash() {
        return diffHash;
    }

    public void setDiffHash(String diffHash) {
        this.diffHash = diffHash;
    }

    public String getFinalHash() {
        return finalHash;
    }

    public void setFinalHash(String finalHash) {
        this.finalHash = finalHash;
    }

    public long getDiffSize() {
        return diffSize;
    }

    public void setDiffSize(long diffSize) {
        this.diffSize = diffSize;
    }

    public long getFinalSize() {
        return finalSize;
    }

    public void setFinalSize(long finalSize) {
        this.finalSize = finalSize;
    }

    public String getDiffUrl() {
        return diffUrl;
    }

    public void setDiffUrl(String diffUrl) {
        this.diffUrl = diffUrl;
    }
}
