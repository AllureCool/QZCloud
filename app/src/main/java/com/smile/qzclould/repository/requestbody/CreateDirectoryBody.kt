package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class CreateDirectoryBody(
        val name: String,
        val path: String
): Serializable