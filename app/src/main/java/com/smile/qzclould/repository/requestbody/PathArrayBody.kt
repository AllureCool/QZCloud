package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class PathArrayBody(
        val path: List<String>
): Serializable