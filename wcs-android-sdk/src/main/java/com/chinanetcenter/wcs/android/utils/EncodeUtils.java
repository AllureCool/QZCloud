package com.chinanetcenter.wcs.android.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * URLEncoding is the alternate base64 encoding defined in RFC 4648. It is
 * typically used in URLs and file names.
 */

/**
 * Base64编码工具类
 */
public class EncodeUtils {

    public static byte[] urlsafeEncodeBytes(byte[] src) {
        if (src.length % 3 == 0) {
            return encodeBase64Ex(src);
        }

        byte[] b = encodeBase64Ex(src);
        if (b.length % 4 == 0) {
            return b;
        }

        int pad = 4 - b.length % 4;
        byte[] b2 = new byte[b.length + pad];
        System.arraycopy(b, 0, b2, 0, b.length);
        b2[b.length] = '=';
        if (pad > 1) {
            b2[b.length + 1] = '=';
        }
        return b2;
    }

    public static byte[] urlsafeBase64Decode(String encoded) {
        byte[] rawbs = encoded.getBytes();
        for (int i = 0; i < rawbs.length; i++) {
            if (rawbs[i] == '_') {
                rawbs[i] = '/';
            } else if (rawbs[i] == '-') {
                rawbs[i] = '+';
            }
        }
        return Base64.decode(rawbs, Base64.NO_WRAP);
    }

    public static String urlsafeDecodeString(String encoded) {
        return new String(urlsafeBase64Decode(encoded));
    }

    public static String urlsafeEncodeString(byte[] src) {
        return new String(urlsafeEncodeBytes(src));
    }

    public static String urlsafeEncode(String text) {
        try {
            return new String(urlsafeEncodeBytes(text.getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    // replace '/' with '_', '+" with '-'
    private static byte[] encodeBase64Ex(byte[] src) {
        // urlsafe version is not supported in version 1.4 or lower.
        byte[] b64 = Base64.encode(src, Base64.NO_WRAP);

        for (int i = 0; i < b64.length; i++) {
            if (b64[i] == '/') {
                b64[i] = '_';
            } else if (b64[i] == '+') {
                b64[i] = '-';
            }
        }
        return b64;
    }

    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString().toLowerCase();
    }

}
