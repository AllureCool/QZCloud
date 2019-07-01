package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class PathArrayBodyV2(
        val source: List<Source>
): Serializable {
    data class Source(
            val path: String
    ): Serializable
}