package com.chinanetcenter.wcs.android.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.List;

public class ApkUtls {

    public static String getApkPath(Context context, String packageName) {
        List<ApplicationInfo> apps = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        for (final ApplicationInfo applicationInfo : apps) {
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                if (applicationInfo.publicSourceDir.contains(packageName)) {
                    return applicationInfo.publicSourceDir;
                }
            }
        }
        return null;
    }

}
