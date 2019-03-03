package com.chinanetcenter.wcs.android;

public class Config {

    public static final String PUT_URL = "your upload domain";
    public static final String GET_URL = "your bucket domain";
    public static final String MGR_URL = "your manage domain";


    public static String VERSION = "1.6.4";
    public static boolean DEBUGGING = false;

    //不设置默认url，必须由用户填充
    public static String baseUrl = PUT_URL;

    private Config() {
    }

}
