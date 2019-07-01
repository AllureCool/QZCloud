package com.smile.qzclould.repository.requestbody

import java.io.Serializable

data class GetDataByPathBodyV2(
        val path: String,
        val start: Int = -1,
        val listSize: Int,
        val orderBy: Int,
        val type: Int = 1
): Serializable