package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class MoveFileBody(
        val path: List<String>,
        val destPath: String
): Serializable