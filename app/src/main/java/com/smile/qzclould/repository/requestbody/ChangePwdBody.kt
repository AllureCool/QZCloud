package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class ChangePwdBody(
        val phoneInfo: String,
        val code: String,
        val newPassword: String
): Serializable