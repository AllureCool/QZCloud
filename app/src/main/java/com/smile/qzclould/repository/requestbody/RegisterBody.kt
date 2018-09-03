package com.smile.qzclould.repository.requestbody

data class RegisterBody(
        val phoneInfo: String,
        val code: String,
        val name: String,
        val password: String
)