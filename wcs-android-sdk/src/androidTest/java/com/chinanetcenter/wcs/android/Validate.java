package com.chinanetcenter.wcs.android;

import android.support.test.runner.AndroidJUnit4;

import com.chinanetcenter.wcs.android.utils.EncodeUtils;
import com.chinanetcenter.wcs.android.utils.WCSLogUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;

/**
 * @author : yanghuan
 * @version : 1.0
 * @package : com.chinanetcenter.wcs.android
 * @class : Validate
 * @time : 2017/6/12 9:48
 * @description :
 */
@RunWith(AndroidJUnit4.class)
public class Validate  {

    private static String getUploadScope(String uploadToken) {
        String[] uploadTokenArray = uploadToken.split(":");
        if (uploadTokenArray.length != 3) {
            return "";
        }
        String policyJsonString = EncodeUtils.urlsafeDecodeString(uploadTokenArray[2]);
        String scope = "";
        try {
            JSONObject jsonObject = new JSONObject(policyJsonString);
            scope = jsonObject.optString("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return scope;
    }

    @Test
    public void xtestEnvironment() throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        Field debuggingField = WCSLogUtil.class.getDeclaredField("DEBUGGING");
        debuggingField.setAccessible(true);
        boolean debugging = debuggingField.getBoolean(null);
        Assert.assertFalse(debugging);
    }

}
