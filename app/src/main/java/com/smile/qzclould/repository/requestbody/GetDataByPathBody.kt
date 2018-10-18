package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class GetDataByPathBody(
        val path: String,
        val page: Int,
        val pageSize: Int,
        val orderBy: Int,
        val type: Int = -1
): Serializable