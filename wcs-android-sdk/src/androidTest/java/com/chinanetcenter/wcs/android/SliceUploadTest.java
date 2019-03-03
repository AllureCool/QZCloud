package com.chinanetcenter.wcs.android;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chinanetcenter.wcs.android.api.FileUploader;
import com.chinanetcenter.wcs.android.listener.SliceUploaderListener;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.chinanetcenter.wcs.android.WcsTestConfig.TOKEN;
import static com.chinanetcenter.wcs.android.WcsTestConfig.WAIT_TIME;

/**
 * @author : yanghuan
 * @version : 1.0
 * @package : com.chinanetcenter.wcs.android
 * @class : SliceUploadTest
 * @time : 2017/6/9 16:15
 * @description :
 */
@RunWith(AndroidJUnit4.class)
public class SliceUploadTest {

    private static final String TAG = "CNCLog-SliceUploadTest";


    @BeforeClass
    public static void setUp() throws IOException {
        Config.DEBUGGING = true;
        WcsTestConfig.generateFiles();
        FileUploader.setUploadUrl("http://apitestuser.up0.v1.wcsapi.com");
        ClientConfig config = new ClientConfig(8, WAIT_TIME, WAIT_TIME, 0);
        FileUploader.setClientConfig(config);
    }

    @Test
    public void testSlice() throws Exception {
        FileUploader.setBlockConfigs(8, 256 * 2);
        String filePath = getPath();
        Log.d(TAG, "uploadSlice: " + filePath);
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.sliceUpload(null, InstrumentationRegistry.getTargetContext(),
                WcsTestConfig.TOKEN, new File(filePath), null, new SliceUploaderListener() {

                    @Override
                    public void onProgress(long uploaded, long total) {
                        String percent = (float) uploaded / total * 100 + "%";
                        String progressMsg = "当前: " + uploaded + ", 总: " + total +
                                ", 比例: " + percent + "\r\n";
                        Log.d(TAG, progressMsg);
                    }


                    @Override
                    public void onSliceUploadSucceed(JSONObject reponseJSON) {
                        Log.d(TAG, "onSuccess: " + reponseJSON);
                        signal.countDown();
                        Assert.assertNotNull(reponseJSON);
                    }

                    @Override
                    public void onSliceUploadFailured(HashSet<String> errorMessages) {
                        StringBuilder sb = new StringBuilder();
                        for (String string : errorMessages) {
                            sb.append(string + "\r\n");
                            Log.e(TAG, "errorMessage : " + string);
                        }
                        signal.countDown();
                        Assert.assertNull(errorMessages);
                    }

                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testChinese() throws InterruptedException {
        Log.i(TAG, "testChinese");
        //key 测试@#￥（*……%￥
        final String token = "05b2a7815d8e3c5b46fbafcf5ad88c15e7c8e4a3:Mzc2ZGVlZmQzOTU0ZWRiMTZiMzYzMGRlNGFkNmRjNTdhMzcwOTdlZA==:eyJzY29wZSI6Indjcy1zZGstdGVzdDrmtYvor5VAI--_pe-8iCrigKbigKYl77-lIiwiZGVhZGxpbmUiOiI5Mzk4OTE2ODAwMDAwIiwib3ZlcndyaXRlIjoxLCJmc2l6ZUxpbWl0IjowLCJpbnN0YW50IjowLCJzZXBhcmF0ZSI6MH0=";
        final String filePath = getPath();
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.sliceUpload(null, InstrumentationRegistry.getTargetContext(),
                token, new File(filePath), null, new SliceUploaderListener() {
                    @Override
                    public void onSliceUploadSucceed(JSONObject reponseJSON) {
                        Log.d(TAG, "onSuccess: " + reponseJSON);
                        signal.countDown();
                        Assert.assertNotNull(reponseJSON);
                    }

                    @Override
                    public void onSliceUploadFailured(HashSet<String> errorMessages) {
                        StringBuilder sb = new StringBuilder();
                        for (String string : errorMessages) {
                            sb.append(string + "\r\n");
                            Log.e(TAG, "errorMessage : " + string);
                        }
                        signal.countDown();
                        Assert.assertNull(errorMessages);
                    }

                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testUsingCallbackUrl() throws InterruptedException {
        Log.i(TAG, "testUsingCallbackUrl");
        final String token = "05b2a7815d8e3c5b46fbafcf5ad88c15e7c8e4a3:NGYwOGVhYjg2Y2EwNWU2YjJmNDNmOGI3YjBjYjMxMWNkYTU2OTQ4Nw==:eyJzY29wZSI6Indjcy1zZGstdGVzdCIsImRlYWRsaW5lIjoiOTM5ODkxNjgwMDAwMCIsIm92ZXJ3cml0ZSI6MSwiZnNpemVMaW1pdCI6MCwiY2FsbGJhY2tVcmwiOiJodHRwOi8vY2FsbGJhY2stdGVzdC53Y3MuYml6Lm1hdG9jbG91ZC5jb206ODA4OC9jYWxsYmFja1VybCIsImluc3RhbnQiOjAsInNlcGFyYXRlIjowfQ==";
        final String filePath = getPath();

        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.sliceUpload(null, InstrumentationRegistry.getTargetContext(),
                token, new File(filePath), null, new SliceUploaderListener() {
                    @Override
                    public void onSliceUploadSucceed(JSONObject reponseJSON) {
                        Log.d(TAG, "onSuccess: " + reponseJSON);
                        signal.countDown();
                        Assert.assertNotNull(reponseJSON);
                    }

                    @Override
                    public void onSliceUploadFailured(HashSet<String> errorMessages) {
                        StringBuilder sb = new StringBuilder();
                        for (String string : errorMessages) {
                            sb.append(string + "\r\n");
                            Log.e(TAG, "errorMessage : " + string);
                        }
                        signal.countDown();
                        Assert.assertNull(errorMessages);
                    }

                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    //    @Test
    public void testUsingCallbackBody() throws InterruptedException {
        Log.i(TAG, "testUsingCallbackBody");
        //location=$(x:location)&price=$(x:price)
        final String filePath = getPath();

        final HashMap<String, String> callbackBody = new HashMap<String, String>();
        callbackBody.put("x:location", "123456.001001");
        callbackBody.put("x:price", "12321");

        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.sliceUpload(null, InstrumentationRegistry.getTargetContext(),
                WcsTestConfig.TOKEN, new File(filePath), null, new SliceUploaderListener() {
                    @Override
                    public void onSliceUploadSucceed(JSONObject reponseJSON) {
                        Log.d(TAG, "onSuccess: " + reponseJSON);
                        signal.countDown();
                        Assert.assertNotNull(reponseJSON);
                    }

                    @Override
                    public void onSliceUploadFailured(HashSet<String> errorMessages) {
                        StringBuilder sb = new StringBuilder();
                        for (String string : errorMessages) {
                            sb.append(string + "\r\n");
                            Log.e(TAG, "errorMessage : " + string);
                        }
                        signal.countDown();
                        Assert.assertNull(errorMessages);
                    }

                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testNullFile() throws Exception {
        Log.d(TAG, "testNullFile ");
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.sliceUpload(null, InstrumentationRegistry.getTargetContext(),
                WcsTestConfig.TOKEN, null, null, new SliceUploaderListener() {
                    @Override
                    public void onSliceUploadSucceed(JSONObject reponseJSON) {
                        Log.d(TAG, "onSuccess: " + reponseJSON);
                        signal.countDown();
                        Assert.assertNull(reponseJSON);
                    }

                    @Override
                    public void onSliceUploadFailured(HashSet<String> errorMessages) {
                        StringBuilder sb = new StringBuilder();
                        for (String string : errorMessages) {
                            sb.append(string + "\r\n");
                            Log.e(TAG, "errorMessage : " + string);
                        }
                        signal.countDown();
                        Assert.assertTrue(sb.toString().contains("file no exists"));
                    }

                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testUnReadFile() throws Exception {
        Log.d(TAG, "testUnReadFile ");
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.sliceUpload(null, InstrumentationRegistry.getTargetContext(),
                WcsTestConfig.TOKEN, new File("sfdfo"), null, new SliceUploaderListener() {
                    @Override
                    public void onSliceUploadSucceed(JSONObject reponseJSON) {
                        Log.d(TAG, "onSuccess: " + reponseJSON);
                        signal.countDown();
                        Assert.assertNull(reponseJSON);
                    }

                    @Override
                    public void onSliceUploadFailured(HashSet<String> errorMessages) {
                        StringBuilder sb = new StringBuilder();
                        for (String string : errorMessages) {
                            sb.append(string + "\r\n");
                            Log.e(TAG, "errorMessage : " + string);
                        }
                        signal.countDown();
                        Assert.assertTrue(sb.toString().contains("file no exists"));
                    }

                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testNullToken() throws Exception {
        Log.d(TAG, "testNullToken ");
        String filePath = getPath();
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.sliceUpload(null, InstrumentationRegistry.getTargetContext(),
                null, new File(filePath), null, new SliceUploaderListener() {
                    @Override
                    public void onSliceUploadSucceed(JSONObject reponseJSON) {
                        Log.d(TAG, "onSuccess: " + reponseJSON);
                        signal.countDown();
                        Assert.assertNull(reponseJSON);
                    }

                    @Override
                    public void onSliceUploadFailured(HashSet<String> errorMessages) {
                        StringBuilder sb = new StringBuilder();
                        for (String string : errorMessages) {
                            sb.append(string + "\r\n");
                            Log.e(TAG, "errorMessage : " + string);
                        }
                        signal.countDown();
                        Assert.assertTrue(sb.toString().contains("param invalidate"));
                    }

                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testEmptyToken() throws Exception {
        Log.d(TAG, "testNullToken ");
        String filePath = getPath();
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.sliceUpload(null, InstrumentationRegistry.getTargetContext(),
                "", new File(filePath), null, new SliceUploaderListener() {
                    @Override
                    public void onSliceUploadSucceed(JSONObject reponseJSON) {
                        Log.d(TAG, "onSuccess: " + reponseJSON);
                        signal.countDown();
                        Assert.assertNull(reponseJSON);
                    }

                    @Override
                    public void onSliceUploadFailured(HashSet<String> errorMessages) {
                        StringBuilder sb = new StringBuilder();
                        for (String string : errorMessages) {
                            sb.append(string + "\r\n");
                            Log.e(TAG, "errorMessage : " + string);
                        }
                        signal.countDown();
                        Assert.assertTrue(sb.toString().contains("param invalidate"));
                    }

                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testInvalidToken() throws Exception {
        Log.d(TAG, "testInvalidToken ");
        String filePath = getPath();
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.sliceUpload(null, InstrumentationRegistry.getTargetContext(),
                "wefoegwoeg", new File(filePath), null, new SliceUploaderListener() {
                    @Override
                    public void onSliceUploadSucceed(JSONObject reponseJSON) {
                        Log.d(TAG, "onSuccess: " + reponseJSON);
                        signal.countDown();
                        Assert.assertNull(reponseJSON);
                    }

                    @Override
                    public void onSliceUploadFailured(HashSet<String> errorMessages) {
                        StringBuilder sb = new StringBuilder();
                        for (String string : errorMessages) {
                            sb.append(string + "\r\n");
                            Log.e(TAG, "errorMessage : " + string);
                        }
                        signal.countDown();
                        Assert.assertTrue(sb.toString().contains("param invalidate"));
                    }

                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @NonNull
    private String getPath() {
        return InstrumentationRegistry.getTargetContext().getFilesDir() + File.separator + "500k";
    }

    @Test
    public void testOverride0() throws Exception {
        Log.d(TAG, "testOverride0");
        final String token = "05b2a7815d8e3c5b46fbafcf5ad88c15e7c8e4a3:ZjkzYTlmMTRkMGZmZjgzMzcwNmM1OTkzZjAxZTEwZjVlOTcwMjhiZA==:eyJzY29wZSI6Indjcy1zZGstdGVzdCIsImRlYWRsaW5lIjoiOTM5ODkxNjgwMDAwMCIsIm92ZXJ3cml0ZSI6MCwiZnNpemVMaW1pdCI6MCwiaW5zdGFudCI6MCwic2VwYXJhdGUiOjB9";
        final String filePath = getPath();
        final File file = new File(filePath);
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.sliceUpload(null, InstrumentationRegistry.getTargetContext(),
                token, file, null, new SliceUploaderListener() {

                    @Override
                    public void onSliceUploadFailured(HashSet<String> errorMessages) {
                        StringBuilder sb = new StringBuilder();
                        for (String string : errorMessages) {
                            sb.append(string + "\r\n");
                            Log.e(TAG, "errorMessage : " + string);
                        }
                        signal.countDown();
                        Assert.assertTrue(sb.toString().contains("file already exists"));
                    }

                    @Override
                    public void onSliceUploadSucceed(JSONObject reponseJSON) {
                        Log.d(TAG, "onSuccess: " + reponseJSON);
                        signal.countDown();
                        Assert.assertNull(reponseJSON);
                    }
                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testConcurrent() throws Exception {
        FileUploader.setUploadUrl("http://lmsv-up.ksmobile.net");
        final String token = "5c2a09ff8bada8d9c76c0261117e806bfda785db:YWRmOWMyMzI4NGY4ZDQxMGZkYzRjNmY2YzNiM2E5MzM0MDgzMjNlZg==:eyJzY29wZSI6ImxpdmVtZS1zdjoyMDE3MDYxOVwvNTc3OTE0ODg1MTgwMzc1NTAzOTY1MDA4NjkxMjEwLm1wNCIsImRlYWRsaW5lIjoxNDk3OTU4MTQ2MjAzLCJvdmVyd3JpdGUiOjEsInJldHVybkJvZHkiOiJ1cmw9JCh1cmwpJmZzaXplPSQoZnNpemUpIn0=";
        final CountDownLatch latch1 = new CountDownLatch(1);
        int total=15;
        final CountDownLatch latch2 = new CountDownLatch(total);
        final int[] success = {0};
        final int[] fail = {0};
        for (int i = 0; i < total; i++) {
            final int index = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String filePath = InstrumentationRegistry.getTargetContext().getFilesDir() + File.separator + WcsTestConfig.sFileNameArray[index/2];
//                        String filePath = InstrumentationRegistry.getTargetContext().getFilesDir() + File.separator + WcsTestConfig.sFileNameArray[index];
                        latch1.await();
                        sliceUpload(filePath, token, filePath, null, success,fail,latch2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        latch1.countDown();
        latch2.await();
        Assert.assertEquals(total, success[0]);
    }

    private void sliceUpload(String tag, String token, String filePath, HashMap<String, String> callbackBody, final int[] success,final int[] fail , final CountDownLatch latch2) throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        File file = new File(filePath);

        FileUploader.sliceUpload(tag, InstrumentationRegistry.getTargetContext(),
                token, file, callbackBody, new SliceUploaderListener() {
                    @Override
                    public void onSliceUploadSucceed(JSONObject reponseJSON) {
                        Log.d(TAG, "onSuccess: " + reponseJSON);
                        success[0]++;
                        signal.countDown();
                        if(success[0]+fail[0]==5){
                            latch2.countDown();
                        }
                    }


                    @Override
                    public void onSliceUploadFailured(HashSet<String> errorMessages) {
                        fail[0]++;
                        StringBuilder sb = new StringBuilder();
                        for (String string : errorMessages) {
                            sb.append(string + "\r\n");
                            Log.e(TAG, "errorMessage : " + string);
                        }
                        if(success[0]+fail[0]==5){
                            latch2.countDown();
                        }
                        signal.countDown();
                    }

                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    public void testInitiativeCancel() throws Exception {
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        final String filePath = InstrumentationRegistry.getTargetContext().getFilesDir() + File.separator + "50m";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch1.await();
                    File file = new File(filePath);
                    FileUploader.sliceUpload(null, InstrumentationRegistry.getTargetContext(),
                            WcsTestConfig.TOKEN, file, null, new SliceUploaderListener() {
                                @Override
                                public void onSliceUploadSucceed(JSONObject reponseJSON) {
                                    Log.d(TAG, "onSuccess: " + reponseJSON);
                                    Assert.assertNull(reponseJSON);
                                }


                                @Override
                                public void onSliceUploadFailured(HashSet<String> errorMessages) {
                                    StringBuilder sb = new StringBuilder();
                                    for (String string : errorMessages) {
                                        sb.append(string + "\r\n");
                                        Log.e(TAG, "errorMessage : " + string);
                                    }
                                    Assert.assertTrue(sb.toString().contains("cancel"));
                                }

                            });
                    FileUploader.cancelRequests(InstrumentationRegistry.getTargetContext());
                    latch2.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        latch1.countDown();
        latch2.await();
    }

    public void testCancel() throws Exception {
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        String path = InstrumentationRegistry.getTargetContext().getFilesDir() + File.separator + "50m";
        FileUploader.sliceUpload(path, InstrumentationRegistry.getTargetContext(),
                TOKEN, new File(path), null, new SliceUploaderListener() {
                    @Override
                    public void onProgress(long uploaded, long total) {
                        super.onProgress(uploaded, total);
                        latch2.countDown();
                    }

                    @Override
                    public void onSliceUploadFailured(HashSet<String> errorMessages) {
                        StringBuilder sb = new StringBuilder();
                        for (String string : errorMessages) {
                            sb.append(string + "\r\n");
                            Log.e(TAG, "errorMessage : " + string);
                        }
                        latch1.countDown();
                        Assert.assertTrue(sb.toString().contains("cancel"));
                    }

                    @Override
                    public void onSliceUploadSucceed(JSONObject reponseJSON) {
                        Log.d(TAG, "onSuccess: " + reponseJSON);
                        latch1.countDown();
                        Assert.assertNull(reponseJSON);
                    }
                });
        latch2.await();
        FileUploader.cancelRequests(InstrumentationRegistry.getTargetContext(), path);
        latch1.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

}