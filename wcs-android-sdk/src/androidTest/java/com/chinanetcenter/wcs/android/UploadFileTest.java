package com.chinanetcenter.wcs.android;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.chinanetcenter.wcs.android.api.FileUploader;
import com.chinanetcenter.wcs.android.entity.OperationMessage;
import com.chinanetcenter.wcs.android.internal.UploadFileRequest;
import com.chinanetcenter.wcs.android.listener.FileUploaderListener;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.chinanetcenter.wcs.android.WcsTestConfig.WAIT_TIME;


/**
 * @author : yanghuan
 * @version : 1.6.1
 * @package : com.chinanetcenter.wcs.android
 * @class : SliceUploadTest
 * @time : 2017/6/9 16:15
 * @description :
 */
@RunWith(AndroidJUnit4.class)
public class UploadFileTest{

    private static final String TAG = "CNCLog-UploadFileTest";

    @BeforeClass
    public static void setUp() throws Exception {
        Config.DEBUGGING = true;
        WcsTestConfig.generateFiles();
        FileUploader.setUploadUrl(WcsTestConfig.UPLOAD_URL);
        ClientConfig config = new ClientConfig(WAIT_TIME, WAIT_TIME);
        config.setMaxErrorRetry(0);
        FileUploader.setClientConfig(config);
    }

    @Test
    public void testInvalidateToken() throws Exception {
        Log.d(TAG, "testInvalidateToken");
        final String token = "fdsafdsa";
        final String filePath = getPath();
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.upload(InstrumentationRegistry.getTargetContext(),
                token, filePath, null, new FileUploaderListener() {

                    @Override
                    public void onFailure(OperationMessage operationMessage) {
                        Log.d(TAG, "onFailure: " + operationMessage.getMessage());
                        boolean contains = operationMessage.getMessage().contains("param invalidate");
                        Log.d(TAG, "onFailure: " + contains);
                        Assert.assertNull(operationMessage);
                        Assert.assertTrue(contains);
                        signal.countDown();
                    }

                    @Override
                    public void onSuccess(int status, JSONObject responseJson) {
                        Log.d(TAG, "onSuccess: " + responseJson);
                        Assert.assertNull(responseJson);
                        signal.countDown();
                    }
                });
        signal.await(WAIT_TIME*3, TimeUnit.MILLISECONDS);
    }
    @NonNull
    private String getPath() {
        return InstrumentationRegistry.getTargetContext().getFilesDir() + File.separator + "500k";
    }

    @Test
    public void testNormal() throws Exception {
        final String filePath = getPath();
        Log.d(TAG, "uploadNormal: " + filePath);
        final File file = new File(filePath);
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.upload(InstrumentationRegistry.getTargetContext(),
                WcsTestConfig.TOKEN, file, null, new FileUploaderListener() {
                    @Override
                    public void onProgress(UploadFileRequest request, long currentSize, long totalSize) {
                        String percent = ((float) currentSize / totalSize * 100) + "%";
                        String progressMsg = "当前: " + currentSize + ", 总: " + totalSize +
                                ", 比例: " + percent + "\r\n";
                        Log.d(TAG, progressMsg);
                    }

                    @Override
                    public void onFailure(OperationMessage operationMessage) {
                        Log.d(TAG, "onFailure: " + operationMessage.getMessage());
                        signal.countDown();
                        Assert.assertNull(operationMessage);
                    }

                    @Override
                    public void onSuccess(int status, JSONObject responseJson) {
                        Log.d(TAG, "onSuccess: " + responseJson);
                        signal.countDown();
                        Assert.assertNotNull(responseJson);
                    }
                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }


    @Test
    public void testChinese() throws InterruptedException {
        Log.i(TAG, "testChinese");
        //key 测试@#￥（*……%￥
        final String token = "05b2a7815d8e3c5b46fbafcf5ad88c15e7c8e4a3:NmRmNmY0MDczYTU0NDk0NTgxNjA4NTU1M2JhZjI2ZDc3ZGY0OWUwYQ==:eyJzY29wZSI6Indjcy1zZGstdGVzdDrmtYvor5VAI--_pe-8iCrigKbigKYl77-lIiwiZGVhZGxpbmUiOiI5Mzk4OTE2ODAwMDAwIiwib3ZlcndyaXRlIjoxLCJmc2l6ZUxpbWl0IjowLCJjYWxsYmFja0JvZHkiOiJrZXk9JChrZXkpIiwiaW5zdGFudCI6MCwic2VwYXJhdGUiOjB9";
        final String filePath = getPath();
        final File file = new File(filePath);
        final CountDownLatch signal = new CountDownLatch(1);

        FileUploader.upload(InstrumentationRegistry.getTargetContext(),
                token, file, null, new FileUploaderListener() {

                    @Override
                    public void onFailure(OperationMessage operationMessage) {
                        Log.d(TAG, "onFailure: " + operationMessage.getMessage());
                        signal.countDown();
                        Assert.assertNull(operationMessage);
                    }

                    @Override
                    public void onSuccess(int status, JSONObject responseJson) {
                        Log.d(TAG, "onSuccess: " + responseJson);
                        signal.countDown();
                        Assert.assertNotNull(responseJson);
                    }
                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testUsingCallbackUrl() throws InterruptedException {
        Log.i(TAG, "testUsingCallbackUrl");
        final String token = "05b2a7815d8e3c5b46fbafcf5ad88c15e7c8e4a3:NGYwOGVhYjg2Y2EwNWU2YjJmNDNmOGI3YjBjYjMxMWNkYTU2OTQ4Nw==:eyJzY29wZSI6Indjcy1zZGstdGVzdCIsImRlYWRsaW5lIjoiOTM5ODkxNjgwMDAwMCIsIm92ZXJ3cml0ZSI6MSwiZnNpemVMaW1pdCI6MCwiY2FsbGJhY2tVcmwiOiJodHRwOi8vY2FsbGJhY2stdGVzdC53Y3MuYml6Lm1hdG9jbG91ZC5jb206ODA4OC9jYWxsYmFja1VybCIsImluc3RhbnQiOjAsInNlcGFyYXRlIjowfQ==";
        final String filePath = getPath();
        final File file = new File(filePath);
        final CountDownLatch signal = new CountDownLatch(1);

        FileUploader.upload(InstrumentationRegistry.getTargetContext(),
                token, file, null, new FileUploaderListener() {

                    @Override
                    public void onFailure(OperationMessage operationMessage) {
                        Log.d(TAG, "onFailure: " + operationMessage.getMessage());
                        signal.countDown();
                        Assert.assertNull(operationMessage);
                    }

                    @Override
                    public void onSuccess(int status, JSONObject responseJson) {
                        Log.d(TAG, "onSuccess: " + responseJson);
                        signal.countDown();
                        Assert.assertNotNull(responseJson);
                    }
                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testUsingCallbackBody() throws InterruptedException {
        Log.i(TAG, "testUsingCallbackBody");
        //location=$(x:location)&price=$(x:price)
        final String filePath = getPath();
        final File file = new File(filePath);
        final CountDownLatch signal = new CountDownLatch(1);
    //自定义替换变量
//    final String token = "05b2a7815d8e3c5b46fbafcf5ad88c15e7c8e4a3:YTJlNGMzOWIwNTFkMmZkYjUyMTEzYmMzNTNmY2QyYjU3Y2JjZTRkZA==:eyJzY29wZSI6Indjcy1zZGstdGVzdCIsImRlYWRsaW5lIjoiOTM5ODkxNjgwMDAwMCIsIm92ZXJ3cml0ZSI6MSwiZnNpemVMaW1pdCI6MCwiY2FsbGJhY2tCb2R5IjoibG9jYXRpb249JCh4OmxvY2F0aW9uKSZwcmljZT0kKHg6cHJpY2UpIiwiaW5zdGFudCI6MCwic2VwYXJhdGUiOjB9";
    //常量 username=john&age=21
//    final String token = "05b2a7815d8e3c5b46fbafcf5ad88c15e7c8e4a3:ZmU3ZWFmN2U1Yzg4YjJiZTcxYzE0ZTI2NTRjOTNlZGEzZjk2ZjQxZg==:eyJzY29wZSI6Indjcy1zZGstdGVzdDprZXlpIiwiZGVhZGxpbmUiOiI5Mzk4OTE2ODAwMDAwIiwicmV0dXJuQm9keSI6InVybD0kKHVybCkma2V5PSQoa2V5KSZidWNrZXQ9JChidWNrZXQpIiwib3ZlcndyaXRlIjoxLCJmc2l6ZUxpbWl0IjowLCJpbnN0YW50IjowLCJzZXBhcmF0ZSI6MH0=";
    //特殊替换变量 url=$(url)&key=$(key)&bucket=$(bucket)
    final String token = "05b2a7815d8e3c5b46fbafcf5ad88c15e7c8e4a3:ZmU3ZWFmN2U1Yzg4YjJiZTcxYzE0ZTI2NTRjOTNlZGEzZjk2ZjQxZg==:eyJzY29wZSI6Indjcy1zZGstdGVzdDprZXlpIiwiZGVhZGxpbmUiOiI5Mzk4OTE2ODAwMDAwIiwicmV0dXJuQm9keSI6InVybD0kKHVybCkma2V5PSQoa2V5KSZidWNrZXQ9JChidWNrZXQpIiwib3ZlcndyaXRlIjoxLCJmc2l6ZUxpbWl0IjowLCJpbnN0YW50IjowLCJzZXBhcmF0ZSI6MH0=";
        final HashMap<String, String> callbackBody = new HashMap<String, String>();
        callbackBody.put("x:location", "123456.001001");
        callbackBody.put("x:price", "12321");
        FileUploader.upload(InstrumentationRegistry.getTargetContext(),
                token, file, callbackBody, new FileUploaderListener() {

                    @Override
                    public void onFailure(OperationMessage operationMessage) {
                        Log.d(TAG, "onFailure: " + operationMessage.getMessage());
                        signal.countDown();
                        Assert.assertNull(operationMessage);
                    }

                    @Override
                    public void onSuccess(int status, JSONObject responseJson) {
                        Log.d(TAG, "onSuccess: " + responseJson);
                        signal.countDown();
                        Assert.assertTrue(responseJson.toString().contains("wcs-sdk-test.s.wcsapi.biz.matocloud.com\\/keyi"));
                    }
                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testNullToken() throws Exception {
        Log.d(TAG, "uploadNullToken");
        final String token = null;
        final String filePath = getPath();
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.upload(InstrumentationRegistry.getTargetContext(),
                token, filePath, null, new FileUploaderListener() {

                    @Override
                    public void onFailure(OperationMessage operationMessage) {
                        Log.d(TAG, "onFailure: " + operationMessage.getMessage());
                        signal.countDown();
                        Assert.assertTrue(operationMessage.getMessage().contains("token invalidate"));
                    }

                    @Override
                    public void onSuccess(int status, JSONObject responseJson) {
                        Log.d(TAG, "onSuccess: " + responseJson);
                        signal.countDown();
                        Assert.assertNull(responseJson);
                    }
                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }


    @Test
    public void testNullPath() throws Exception {
        Log.d(TAG, "testNullPath");
        final String filePath = null;
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.upload(InstrumentationRegistry.getTargetContext(),
                WcsTestConfig.TOKEN, filePath, null, new FileUploaderListener() {

                    @Override
                    public void onFailure(OperationMessage operationMessage) {
                        Log.d(TAG, "onFailure: " + operationMessage.getMessage());
                        signal.countDown();
                        Assert.assertTrue(operationMessage.getMessage().contains("file no exists"));
                    }

                    @Override
                    public void onSuccess(int status, JSONObject responseJson) {
                        Log.d(TAG, "onSuccess: " + responseJson);
                        signal.countDown();
                        Assert.assertNull(responseJson);
                    }
                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testNullUri() throws Exception {
        Log.d(TAG, "testNullUri");
        final Uri uri = null;
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.upload(InstrumentationRegistry.getTargetContext(),
                WcsTestConfig.TOKEN, uri, null, new FileUploaderListener() {

                    @Override
                    public void onFailure(OperationMessage operationMessage) {
                        Log.d(TAG, "onFailure: " + operationMessage.getMessage());
                        signal.countDown();
                        Assert.assertTrue(operationMessage.getMessage().contains("fileUri no exists"));
                    }

                    @Override
                    public void onSuccess(int status, JSONObject responseJson) {
                        Log.d(TAG, "onSuccess: " + responseJson);
                        signal.countDown();
                        Assert.assertNull(responseJson);
                    }
                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testConcurrent() throws Exception {
        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(5);
        final int[] count = {0};
        for (int i = 0; i < 5; i++) {
            final int index = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String filePath = InstrumentationRegistry.getTargetContext().getFilesDir() + File.separator + WcsTestConfig.sFileNameArray[index];
                        latch1.await();
                        upload(WcsTestConfig.TOKEN, filePath, null, count);
                        latch2.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        latch1.countDown();
        latch2.await();
        Assert.assertEquals(5, count[0]);
    }

    private void upload(String token, String filePath, HashMap<String, String> callbackBody, final int[] count) throws InterruptedException {
        final CountDownLatch signal = new CountDownLatch(1);
        FileUploader.upload(InstrumentationRegistry.getTargetContext(),
                token, new File(filePath), callbackBody, new FileUploaderListener() {

                    @Override
                    public void onFailure(OperationMessage operationMessage) {
                        Log.d(TAG, "onFailure: " + operationMessage.getMessage());
                        signal.countDown();
                        Assert.assertTrue(operationMessage.getMessage().contains("file already exists"));
                        count[0]++;
                    }

                    @Override
                    public void onSuccess(int status, JSONObject responseJson) {
                        signal.countDown();
                        Log.d(TAG, "onSuccess: " + responseJson);
                        Assert.assertNull(responseJson);
                    }
                });
        signal.await(WAIT_TIME, TimeUnit.MILLISECONDS);
    }
}
