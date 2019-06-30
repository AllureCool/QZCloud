package com.smile.qzclould.repository.requestbody

import java.io.Serializable

/**
 * Created by wangzhg on 2019/3/2
 * Describe:
 */
data class UploadFileBodyV2(
        val name: String,
        val hash: String,
        val path: String
): Serializable