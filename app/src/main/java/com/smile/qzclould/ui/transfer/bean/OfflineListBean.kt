package com.smile.qzclould.ui.transfer.bean

import java.io.Serializable

data class OfflineListBean(
        val list: List<OfflineInfo>
): Serializable {
    data class OfflineInfo(
            val identity: String,
            val userIdentity: Int,
            val createTime: Long,
            val name: String,
            val type: Int,
            val status: Int,
            val size: Long,
            val downloadSize: Long,
            val progress: Int,
            val cip: String,
            val data: String
    ): Serializable {
        data class OfflineData(
                val identity: String,
                val user: Int,
                val path: String
        ): Serializable
    }
}