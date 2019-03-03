package com.chinanetcenter.wcs.android;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogRecorder {

    private static final String FILE_NAME = "wcs-dump.log";
    private String mFilePathString = "";
    private volatile Boolean mLock = false;

    private LogRecorder() {
    }

    public static LogRecorder getInstance() {
        return SingletonHolder.sInstance;
    }

    public void setup(Context context) {
        mFilePathString = context.getFilesDir() + File.separator + FILE_NAME;
    }

    public void enableLog() {
        mLock = true;
    }

    public void disableLog() {
        mLock = false;
    }

    public Boolean getLock() {
        return mLock;
    }

    public synchronized void dumpLog(String str) {
        synchronized (mLock) {
            if (!mLock) {
                return;
            }
            if (!TextUtils.isEmpty(mFilePathString)) {
                File logFile = new File(mFilePathString);
                if (!logFile.exists()) {
                    try {
                        logFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                    buf.append(str);
                    buf.newLine();
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("CNCLog", "Log recorder hava not setted up.");
            }
        }
    }

    private static class SingletonHolder {
        private static LogRecorder sInstance = new LogRecorder();
    }

}
