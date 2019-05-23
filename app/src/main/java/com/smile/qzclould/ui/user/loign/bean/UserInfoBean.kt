package com.smile.qzclould.ui.user.loign.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserInfoBean(
        val identity: String,
        val uuid: String,
        @SerializedName("name")
        var nickName: String,
        val phone: String,
        val icon: String
): Serializable