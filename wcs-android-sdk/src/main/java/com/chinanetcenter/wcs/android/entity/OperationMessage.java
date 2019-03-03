//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.chinanetcenter.wcs.android.entity;

import android.text.TextUtils;
import com.chinanetcenter.wcs.android.utils.WCSLogUtil;
import org.json.JSONException;
import org.json.JSONObject;

public class OperationMessage {
    private int status;
    private String message;
    private Throwable error;

    public static OperationMessage fromJsonString(String jsonString, String requestId) throws JSONException {
        OperationMessage errorMessage = new OperationMessage();
        if(!TextUtils.isEmpty(jsonString)) {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject.put("X-Reqid", requestId);
            errorMessage.message = jsonObject.toString();
        }

        return errorMessage;
    }

    public OperationMessage() {
    }

    public OperationMessage(int status, String message) {
        this.status = status;
        this.message = message;
        this.error = null;
    }

    public OperationMessage(Throwable error) {
        this.error = error;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        StringBuilder formatMessage = new StringBuilder();
        if(this.message != null) {
            formatMessage.append(this.message);
        }

        if(this.error != null) {
            formatMessage.append(" { ");
            formatMessage.append("ClientMsg: ");
            formatMessage.append(WCSLogUtil.getStackTraceString(this.error));
            formatMessage.append(" }");
        }

        return formatMessage.toString();
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
