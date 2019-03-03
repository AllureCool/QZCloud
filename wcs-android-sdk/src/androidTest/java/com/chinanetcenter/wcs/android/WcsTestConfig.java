package com.chinanetcenter.wcs.android;

import android.util.Log;

import com.chinanetcenter.wcs.android.utils.DateUtil;
import com.chinanetcenter.wcs.android.utils.WetagUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import static android.support.test.InstrumentationRegistry.getContext;

/**
 * @author :wangjm1
 * @version :1.0
 * @package : com.chinanetcenter.wcs.android
 * @class : ${CLASS_NAME}
 * @time : 2017/5/12 ${ITME}
 * @description :TODO
 */
public class WcsTestConfig {

    public static final String UPLOAD_URL = "http://apitestuser.up0.v1.wcsapi.com";
    protected static final int WAIT_TIME = 30000;
    private static final String CALLBACK_URL = "http://callback-test.wcs.biz.matocloud.com:8088/callbackUrl";
    private static final long TEST_EXPIRED = DateUtil.parseDate("2099-01-01 00:00:00", DateUtil.COMMON_PATTERN).getTime();
    private static final String TAG = "CNCLog";

    public final static String TOKEN = "db17ab5d18c137f786b67c490187317a0738f94a:NzU1ZGJlNGJlMWY0MTVhZjNmNzZhYzY4ZDExMjIwYTJkMjA1MWNjZg==:eyJzY29wZSI6ImltYWdlczpmZHNmamtkcz09MzIxamtsPSIsImRlYWRsaW5lIjoiNDA3MDg4MDAwMDAwMCIsIm92ZXJ3cml0ZSI6MSwiZnNpemVMaW1pdCI6MCwiaW5zdGFudCI6MCwic2VwYXJhdGUiOjB9";
    public static String[] sFileNameArray = {"100k", "200k", "500k", "1m", "4m", "10m", "50m", "100m", "500m"};
    public static long[] sFileSizeArray = {102400, 204800, 512000, 1024 * 1024, 1024 * 1024 * 4, 1024 * 1024 * 10, 1024 * 1024 * 50, 1024 * 1024 * 100, 1024 * 1024 * 500};


    public static void generateFilesAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    generateFiles();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    public static void generateFiles() throws IOException {
        for (int i = 0; i < sFileNameArray.length; i++) {
            String filePath = getContext().getFilesDir() + File.separator + sFileNameArray[i];
            File file = new File(filePath);
            if (file.exists()) {
                Log.d(TAG, filePath + " exists, hash : " + WetagUtil.getEtagHash(file));
                continue;
            }
            Log.d(TAG, "Generating File " + filePath);
            byte[] buffer = new byte[1024];
            new Random().nextBytes(buffer);
            long fileSize = sFileSizeArray[i];
            FileOutputStream fos = new FileOutputStream(file);
            for (int k = 0; k < fileSize / 1024; k++) {
                fos.write(buffer);
            }
            fos.close();
            Log.d(TAG, "Generated File " + filePath + " succeeded.");
        }
    }


}
