package com.smile.qzclould.common

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.facebook.drawee.backends.pipeline.Fresco
import com.smile.qzclould.BuildConfig
import com.smile.qzclould.common.base.CloudDatabase
import com.tspoon.traceur.Traceur

/**
 * Created by wangzhg on 2018/7/12
 * Describe:
 */
class App: Application() {
    companion object {
        @JvmStatic
        lateinit var instance: App

        private var cloudDb: CloudDatabase? = null

        fun getCloudDatabase(): CloudDatabase? {
            if(cloudDb == null) {
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
        if(BuildConfig.DEBUG) {
            Traceur.enableLogging()
        }
        Fresco.initialize(this)
    }
}