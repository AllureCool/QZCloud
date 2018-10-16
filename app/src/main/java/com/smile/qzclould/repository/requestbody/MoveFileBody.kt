package com.smile.qzclould.repository.requestbody

data class MoveFileBody(
        val path: List<String>,
        val destPath: String
)