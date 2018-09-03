package com.smile.qzclould.ui.user.loign.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserInfoBean(
        val uuid: String,
        @SerializedName("name")
        val nickName: String,
        val phone: String,
        val icon: String
): Serializable