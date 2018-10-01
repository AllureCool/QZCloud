package com.smile.qzclould.ui.cloud.bean

data class ParseUrlResultBean(
        val taskHash: String,
        val name: String,
        val server: Boolean,
        val files: List<File>
) {
    data class File(
            val path: String,
            val size: Long,
            val order: Int
    )
}