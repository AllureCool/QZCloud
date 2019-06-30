package com.smile.qzclould.ui.transfer.bean

import java.io.Serializable

/**
 * Created by wangzhg on 2019/3/2
 * Describe:
 */
data class UploadFileResponeBeanV2(
        val uploadInfo: UploadInfo
): Serializable {
    data class UploadInfo(
            val uploadUrl: String,
            val type: String,
            val uploadToken: String,
            val filePath: String
    ): Serializable
}