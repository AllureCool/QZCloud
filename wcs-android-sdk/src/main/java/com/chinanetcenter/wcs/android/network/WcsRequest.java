package com.chinanetcenter.wcs.android.network;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 构造基本的请求参数
 */
public class WcsRequest {

    //base url
    private String url;
    private HttpMethod method;
    //请求头
    private Map<String, String> headers = new HashMap<>();
    //请求参数
    private Map<String, String> parameters = new LinkedHashMap<>();

    private byte[] uploadData;
    //上传文件的路径
    private String uploadFilePath;
    private InputStream uploadInputStream;

    private File file;
    //    private String key;

    //文件名

    private String name;

    private long readStreamLength;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getReadStreamLength() {
        return readStreamLength;
    }

    public void setReadStreamLength(long readStreamLength) {
        this.readStreamLength = readStreamLength;
    }

    public byte[] getUploadData() {
        return uploadData;
    }

    public void setUploadData(byte[] uploadData) {
        this.uploadData = uploadData;
    }

    public String getUploadFilePath() {
        return uploadFilePath;
    }

    public void setUploadFilePath(String uploadFilePath) {
        this.uploadFilePath = uploadFilePath;
    }

    public InputStream getUploadInputStream() {
        return uploadInputStream;
    }

    public void setUploadInputStream(InputStream uploadInputStream) {
        this.uploadInputStream = uploadInputStream;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }


    public void releaseData() {
        if (uploadData != null && uploadData.length > 0) {
            uploadData = null;
        }
    }
}
