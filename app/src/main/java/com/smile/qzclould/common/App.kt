package com.smile.qzclould.common

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import com.facebook.drawee.backends.pipeline.Fresco
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection
import com.smile.qzclould.BuildConfig
import com.smile.qzclould.common.base.CloudDatabase
import com.tencent.bugly.crashreport.CrashReport
import com.tspoon.traceur.Traceur

/**
 * Created by wangzhg on 2018/7/12
 * Describe:
 */
class App : Application() {
    companion object {
        @JvmStatic
        lateinit var instance: App

        private var cloudDb: CloudDatabase? = null

        fun getCloudDatabase(): CloudDatabase? {
            if (cloudDb == null) {
                try {
                    cloudDb = Room.databaseBuilder(instance, CloudDatabase::class.java, "cloud.db").fallbackToDestructiveMigration().build()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return cloudDb
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Traceur.enableLogging()
        }
        CrashReport.initCrashReport(this, "2f2dbb867a", BuildConfig.DEBUG)
        Fresco.initialize(this)
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(FileDownloadUrlConnection.Creator(FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15000) // set connection timeout.
                        .readTimeout(15000) // set read timeout.
                ))
                .maxNetworkThreadCount(3)
                .commit()
    }


}