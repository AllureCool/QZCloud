package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class RegisterBodyV2(
        val phoneInfo: String,
        val code: String,
        val password: String
): Serializable