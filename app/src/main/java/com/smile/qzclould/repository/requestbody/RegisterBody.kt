package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class RegisterBody(
        val phoneInfo: String,
        val code: String,
        val name: String,
        val password: String
): Serializable