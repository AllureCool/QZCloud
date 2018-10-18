package com.smile.qzclould.ui.cloud.bean

import java.io.Serializable

data class ParseUrlResultBean(
        val taskHash: String,
        val name: String,
        val server: Boolean,
        val files: List<File>
): Serializable {
    data class File(
            val path: String,
            val size: Long,
            val order: Int
    ): Serializable
}