package com.smile.qzclould.repository.requestbody

data class OfflineDownloadListBody(
    val page: Int,
    val pageSize: Int,
    val order: Int
)