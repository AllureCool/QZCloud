package com.smile.qzclould.repository

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by wangzhg on 2018/8/25
 * Describe:
 */
data class Respone<T>(
        val success: Boolean,
        val message: String? = null,
        val code: String,
        val status: Int,
        @SerializedName("result")
        val data: T? = null,
        val token: String? = null
): Serializable