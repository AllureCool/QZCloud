package com.smile.qzclould.repository.requestbody

import java.io.Serializable

/**
 * Created by wangzhg on 2019/3/2
 * Describe:
 */
data class UploadFileBody(
        val name: String,
        val hash: String,
        val parent: String,
        val path: String,
        val originalFilename: String
): Serializable