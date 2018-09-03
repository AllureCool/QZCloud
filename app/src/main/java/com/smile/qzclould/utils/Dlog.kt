package com.smile.qzclould.utils

import android.util.Log
import com.google.gson.Gson
import com.smile.qzclould.BuildConfig

/**
 * Created by wangzhg on 2018/7/13
 * Describe:用户debug模式下的log信息
 */
object DLog {

    private val callerName: String
        get() {
            val elements = Throwable().stackTrace
            return elements[2].className
        }

    private val isDebug: Boolean?
        get() = BuildConfig.DEBUG

    @JvmStatic
    fun i(msg: String) {
        if (isDebug!!) {
            Log.i(callerName, msg)
        }
    }
    @JvmStatic
    fun d(msg: String) {
        if (isDebug!!) {
            Log.d(callerName, msg)
        }
    }
    @JvmStatic
    fun v(msg: String) {
        if (isDebug!!) {
            Log.v(callerName, msg)
        }
    }
    @JvmStatic
    fun e(msg: String) {
        if (isDebug!!) {
            Log.e(callerName, msg)
        }
    }
    @JvmStatic
    fun e(e: Throwable) {
        if (isDebug!!) {
            Log.e(callerName, "error", e)
        }
    }
    @JvmStatic
    fun e(msg: String, e: Throwable) {
        if (isDebug!!) {
            Log.e(callerName, msg, e)
        }
    }
    @JvmStatic
    fun w(msg: String) {
        if (isDebug!!) {
            Log.w(callerName, msg)
        }
    }
    @JvmStatic
    fun toJson(obj: Any): String {
        return Gson().toJson(obj)
    }
}