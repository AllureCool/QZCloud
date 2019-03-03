package com.chinanetcenter.wcs.android;

public class ClientConfig {

    public static final int DEFAULT_RETRIES = 1;
    public static final int DEFAULT_CONCURRENT_REQUEST = 5;
    public static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;
    public static final int DEFAULT_CONNECTION_TIMEOUT = 15 * 1000;

    /**
     * 分片上传并发数5~10
     */
    private int maxConcurrentRequest = DEFAULT_CONCURRENT_REQUEST;

    /**
     * response超时的时间，单位为ms
     */
    private int socketTimeout = DEFAULT_SOCKET_TIMEOUT;

    /**
     * 连接超时的时间，单位为ms
     */
    private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;

    /**
     * 重试的次数
     */
    private int maxErrorRetry = DEFAULT_RETRIES;

    private String proxyHost;
    private int proxyPort;

    /**
     *
     * @param maxConcurrentRequest 分片上传并发数5~10
     */
    public ClientConfig(int maxConcurrentRequest) {
        this.maxConcurrentRequest = maxConcurrentRequest;
    }


    /**
     *
     * @param socketTimeout response超时的时间，单位为ms
     * @param connectionTimeout 连接超时的时间，单位为ms
     */
    public ClientConfig(int socketTimeout, int connectionTimeout) {
        this.socketTimeout = socketTimeout;
        this.connectionTimeout = connectionTimeout;
    }

    /**
     *
     * @param maxConcurrentRequest 分片上传并发数5~10
     * @param socketTimeout response超时的时间，单位为ms
     * @param connectionTimeout 连接超时的时间，单位为ms
     * @param maxErrorRetry 重试的次数
     */
    public ClientConfig(int maxConcurrentRequest, int socketTimeout, int connectionTimeout, int maxErrorRetry) {
        this.maxConcurrentRequest = maxConcurrentRequest;
        this.socketTimeout = socketTimeout;
        this.connectionTimeout = connectionTimeout;
        this.maxErrorRetry = maxErrorRetry;
    }

    /**
     * 构造新实例。
     */
    public ClientConfig() {
    }

    /**
     * 获取一个默认实例
     */
    public static ClientConfig getDefaultConf() {
        return new ClientConfig();
    }

    /**
     * 返回最大的并发HTTP请求数
     *
     * @return
     */
    public int getMaxConcurrentRequest() {
        return maxConcurrentRequest;
    }

    /**
     * 设置允许并发的最大HTTP请求数
     *
     * @param maxConcurrentRequest 最大HTTP并发请求数
     */
    public void setMaxConcurrentRequest(int maxConcurrentRequest) {
        this.maxConcurrentRequest = maxConcurrentRequest;
    }

    /**
     * 返回通过打开的连接传输数据的超时时间（单位：毫秒）。
     * 0表示无限等待（但不推荐使用）。
     *
     * @return 通过打开的连接传输数据的超时时间（单位：毫秒）。
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * 设置通过打开的连接传输数据的超时时间（单位：毫秒）。
     * 0表示无限等待（但不推荐使用）。
     *
     * @param socketTimeout 通过打开的连接传输数据的超时时间（单位：毫秒）。
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    /**
     * 返回建立连接的超时时间（单位：毫秒）。
     *
     * @return 建立连接的超时时间（单位：毫秒）。
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * 设置建立连接的超时时间（单位：毫秒）。
     *
     * @param connectionTimeout 建立连接的超时时间（单位：毫秒）。
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * 返回一个值表示当可重试的请求失败后最大的重试次数。（默认值为2）
     *
     * @return 当可重试的请求失败后最大的重试次数。
     */
    public int getMaxErrorRetry() {
        return maxErrorRetry;
    }

    /**
     * 设置一个值表示当可重试的请求失败后最大的重试次数。（默认值为2）
     *
     * @param maxErrorRetry 当可重试的请求失败后最大的重试次数。
     */
    public void setMaxErrorRetry(int maxErrorRetry) {
        this.maxErrorRetry = maxErrorRetry;
    }

//    public String getProxyHost() {
//        return proxyHost;
//    }
//
//    public void setProxyHost(String proxyHost) {
//        this.proxyHost = proxyHost;
//    }
//
//    public int getProxyPort() {
//        return proxyPort;
//    }
//
//    public void setProxyPort(int proxyPort) {
//        this.proxyPort = proxyPort;
//    }
}
