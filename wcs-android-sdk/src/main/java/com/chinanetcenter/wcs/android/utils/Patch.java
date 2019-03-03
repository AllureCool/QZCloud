package com.chinanetcenter.wcs.android.utils;

public class Patch {

    public static native int patch(String apkPath, String destPath, String diffPath);

}
