package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class SendLoginMsgBody(
        val countryCode: String,
        val phone: String
): Serializable