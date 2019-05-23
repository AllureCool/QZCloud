package com.smile.qzclould.repository.requestbody

import java.io.Serializable

/**
 * Created by wangzhg on 2018/8/25
 * Describe:
 */
data class LoginByMessageBody(
    val phoneInfo: String,
    val code: String
): Serializable