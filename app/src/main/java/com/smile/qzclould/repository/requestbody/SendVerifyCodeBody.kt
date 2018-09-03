package com.smile.qzclould.repository.requestbody

data class SendVerifyCodeBody(
        val countryCode: String,
        val phone: String
)