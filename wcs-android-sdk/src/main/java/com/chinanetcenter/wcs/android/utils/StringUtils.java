package com.chinanetcenter.wcs.android.utils;

import java.io.UnsupportedEncodingException;

public class StringUtils {

    private static final String CHARSET = "utf-8";

    public static String stringFrom(byte[] stringBytes) {
        try {
            return stringBytes == null ? null : new String(stringBytes, CHARSET);
        } catch (UnsupportedEncodingException e) {
            WCSLogUtil.e("Encoding response into string failed");
            return null;
        }
    }

}
