package com.smile.qzclould.ui.cloud.bean

import java.io.Serializable

data class OfflineParseBean(
       val name: String,
       val identity: String,
       val size: Long,
       val files: List<OfflineFile>
): Serializable {
    data class OfflineFile(
            val downloadIdentity: String,
            val pathIdentity: String,
            val createTime: Long,
            val name: String,
            val path: String,
            val hash: String,
            val size: Long,
            val downloadSize: Int,
            val status: Int,
            val flag: Int,
            val fileIndex: Int,
            val finish: Boolean
    ): Serializable
}