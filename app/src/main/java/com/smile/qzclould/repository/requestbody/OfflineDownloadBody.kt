package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class OfflineDownloadBody(
        val taskHash: String,
        val copyFile: Array<Int>,
        val savePath: String
): Serializable