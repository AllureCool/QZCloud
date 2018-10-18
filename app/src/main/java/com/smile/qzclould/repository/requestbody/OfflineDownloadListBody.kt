package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class OfflineDownloadListBody(
    val page: Int,
    val pageSize: Int,
    val order: Int
): Serializable