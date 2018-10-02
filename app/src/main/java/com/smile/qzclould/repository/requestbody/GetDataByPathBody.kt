package com.smile.qzclould.repository.requestbody

data class GetDataByPathBody(
        val path: String,
        val page: Int,
        val pageSize: Int,
        val orderBy: Int,
        val type: Int = -1
)