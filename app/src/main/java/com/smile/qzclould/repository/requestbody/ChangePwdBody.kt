package com.smile.qzclould.repository.requestbody

data class ChangePwdBody(
        val phoneInfo: String,
        val code: String,
        val newPassword: String
)