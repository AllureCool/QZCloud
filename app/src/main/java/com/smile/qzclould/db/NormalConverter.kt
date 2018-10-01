package com.smile.qzclould.db

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.smile.qzclould.ui.transfer.bean.FileDetailBean

class NormalConverter {
    @TypeConverter
    fun storedFileDetail(obj: FileDetailBean?): String {
        try {
            return Gson().toJson(obj)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    @TypeConverter
    fun convertFileDetailBean(value: String): FileDetailBean? {
        try {
            return Gson().fromJson(value, FileDetailBean::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}