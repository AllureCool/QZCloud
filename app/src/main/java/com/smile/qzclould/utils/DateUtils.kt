package com.smile.qzclould.utils

import java.text.SimpleDateFormat

object DateUtils {

    fun dateFormat(time: Long): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(time)
    }

    fun dateFormatHMS(time: Long): String {
        val format = SimpleDateFormat("HH:mm:ss")
        return format.format(time)
    }
}