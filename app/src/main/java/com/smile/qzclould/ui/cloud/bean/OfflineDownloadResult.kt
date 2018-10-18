package com.smile.qzclould.ui.cloud.bean

import java.io.Serializable

data class OfflineDownloadResult(
        val userId: Int,
        val taskId: String,
        val copyFile: String,
        val copiedFile: String,
        val createTime: Long,
        val savePath: String,
        val filePath: String,
        val copied: Int
): Serializable