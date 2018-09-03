package com.smile.qzclould.manager

import android.content.Context.MODE_PRIVATE
import android.text.TextUtils
import com.smile.qzclould.common.App
import com.smile.qzclould.common.Constants
import com.smile.qzclould.ui.user.loign.bean.UserInfoBean
import java.io.*

class UserInfoManager private constructor(){

    private val sp by lazy { App.instance.getSharedPreferences(Constants.KEY_CLOUD_SP, MODE_PRIVATE) }

    companion object {
        private var instance: UserInfoManager? = null
        get() {
            if (field == null) {
                field = UserInfoManager()
            }
            return field
        }

        @Synchronized
        fun get(): UserInfoManager {
            return instance!!
        }
    }

    fun saveUserInfo(user: UserInfoBean?) {
        val editor = sp.edit()
        try {
            val bos = ByteArrayOutputStream()
            val os = ObjectOutputStream(bos)
            os.writeObject(user)
            //将序列化的数据转为16进制保存
            val bytesToHexString = bytesToHexString(bos.toByteArray())
            //保存该16进制数组
            editor.putString(Constants.USER_INFO, bytesToHexString)
            editor.apply()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun getUserInfo(): UserInfoBean? {
        try {
            if (sp.contains(Constants.USER_INFO)) {
                val string = sp.getString(Constants.USER_INFO, "")
                if (TextUtils.isEmpty(string)) {
                    return null
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    val stringToBytes = StringToBytes(string!!)
                    val bis = ByteArrayInputStream(stringToBytes!!)
                    val `is` = ObjectInputStream(bis)
                    //返回反序列化得到的对象
                    return `is`.readObject() as UserInfoBean
                }
            }
        } catch (e: StreamCorruptedException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        //所有异常返回null
        return null
    }

    fun hasLogin(): Boolean {
        return !"".equals(sp.getString(Constants.USER_TOKEN, ""))
    }

    fun logout() {
        val editor = sp.edit()
        editor.remove(Constants.USER_INFO).apply()
        editor.remove(Constants.USER_TOKEN).apply()
    }

    fun saveUserToken(token: String?) {
        val editor = sp.edit()
        editor.putString(Constants.USER_TOKEN, token).apply()
    }

    fun getUserToken(): String {
        return sp.getString(Constants.USER_TOKEN, "")
    }

    private fun bytesToHexString(bArray: ByteArray?): String? {
        if (bArray == null) {
            return null
        }
        if (bArray.isEmpty()) {
            return ""
        }
        val sb = StringBuffer(bArray.size)
        var sTemp: String
        for (i in bArray.indices) {
            sTemp = Integer.toHexString(0xFF and bArray[i].toInt())
            if (sTemp.length < 2)
                sb.append(0)
            sb.append(sTemp.toUpperCase())
        }
        return sb.toString()
    }

    fun StringToBytes(data: String): ByteArray? {
        val hexString = data.toUpperCase().trim { it <= ' ' }
        if (hexString.length % 2 != 0) {
            return null
        }
        val retData = ByteArray(hexString.length / 2)
        var i = 0
        while (i < hexString.length) {
            val int_ch: Int  // 两位16进制数转化后的10进制数
            val hex_char1 = hexString[i] ////两位16进制数中的第一位(高位*16)
            val int_ch1: Int
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1.toInt() - 48) * 16   //// 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1.toInt() - 55) * 16 //// A 的Ascll - 65
            else
                return null
            i++
            val hex_char2 = hexString[i] ///两位16进制数中的第二位(低位)
            val int_ch2: Int
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = hex_char2.toInt() - 48 //// 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2.toInt() - 55 //// A 的Ascll - 65
            else
                return null
            int_ch = int_ch1 + int_ch2
            retData[i / 2] = int_ch.toByte()//将转化后的数放入Byte里
            i++
        }
        return retData
    }
}