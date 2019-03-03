package com.smile.qzclould.common

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.support.multidex.MultiDex
import com.facebook.drawee.backends.pipeline.Fresco
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection
import com.liulishuo.filedownloader.util.FileDownloadUtils
import com.smile.qzclould.BuildConfig
import com.smile.qzclould.common.base.CloudDatabase
import com.tencent.bugly.Bugly
import com.tencent.bugly.beta.Beta
import com.tspoon.traceur.Traceur
import org.jetbrains.anko.doAsync
import java.io.File


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
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Traceur.enableLogging()
        }
        Bugly.init(this, "2f2dbb867a", BuildConfig.DEBUG)
        Beta.checkUpgrade()
        Fresco.initialize(this)
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(FileDownloadUrlConnection.Creator(FileDownloadUrlConnection.Configuration()
                        .connectTimeout(15000) // set connection timeout.
                        .readTimeout(15000) // set read timeout.
                ))
                .maxNetworkThreadCount(3)
                .commit()
        clearCache()
    }

    private fun clearCache() {
        doAsync {
            val file = File(FileDownloadUtils.getDefaultSaveRootPath() + File.separator)
            if(file.listFiles().isNotEmpty()) {
                for (item in file.listFiles()) {
                    item.deleteRecursively()
                }
            }
        }
    }
}