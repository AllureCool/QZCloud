package com.chinanetcenter.wcs.android;

import android.util.Log;

import com.chinanetcenter.wcs.android.utils.EncodeUtils;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author : yanghuan
 * @version : 1.0
 * @package : com.example.wcssdktest
 * @class : EncodeTest
 * @time : 2017/6/6 10:58
 * @description : 测试base64加密
 */
public class EncodeTest {
    @Test
    public void encode_isCorrect() throws Exception {
        Log.i("encode_isCorrect", EncodeUtils.urlsafeEncode("p  m  l"));
        assertArrayEquals(EncodeUtils.urlsafeEncode("p  m  l").getBytes(), "cCAgbSAgbA==".getBytes());
    }
}
