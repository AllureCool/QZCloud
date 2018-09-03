package com.smile.qzclould.utils

import android.content.Context
import com.smile.qzclould.common.App

/**
 * Created by wangzhg on 2018/7/13
 * Describe:
 */
class ViewUtils {
    companion object {
        fun getScreenWidth(): Int {
            return getContext().resources.displayMetrics.widthPixels
        }
        fun getScreenHeight(): Int {
            return getContext().resources.displayMetrics.heightPixels
        }

        fun dip2px(dpValue: Float): Float {
            val scale = getContext().resources?.displayMetrics!!.density
            return dpValue * scale + 0.5f
        }
        private inline fun getContext(): Context = App.instance.applicationContext
    }
}