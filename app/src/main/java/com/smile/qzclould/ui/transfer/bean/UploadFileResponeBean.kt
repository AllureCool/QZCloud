package com.smile.qzclould.ui.transfer.bean

import java.io.Serializable

/**
 * Created by wangzhg on 2019/3/2
 * Describe:
 */
data class UploadFileResponeBean(
        val uploadUrl: String,
        val type: Int,
        val token: String,
        val version: Int
): Serializable