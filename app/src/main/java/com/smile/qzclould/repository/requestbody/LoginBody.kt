package com.smile.qzclould.repository.requestbody

import java.io.Serializable

/**
 * Created by wangzhg on 2018/8/25
 * Describe:
 */
data class LoginBody(
    val value: String,
    val password: String
): Serializable