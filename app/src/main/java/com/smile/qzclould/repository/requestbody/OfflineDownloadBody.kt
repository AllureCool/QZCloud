package com.smile.qzclould.repository.requestbody

data class OfflineDownloadBody(
        val taskHash: String,
        val copyFile: Array<Int>,
        val savePath: String
)