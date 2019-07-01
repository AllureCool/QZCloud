package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class OfflineDownloadListBodyV2(
    val start: Int,
    val listSize: Int
): Serializable