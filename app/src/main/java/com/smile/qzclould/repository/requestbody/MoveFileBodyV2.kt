package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class MoveFileBodyV2(
        val source: List<Source>,
        val destination: Destination
): Serializable {
    data class Source(
            val path: String
    ): Serializable

    data class Destination(
            val path: String
    ): Serializable
}