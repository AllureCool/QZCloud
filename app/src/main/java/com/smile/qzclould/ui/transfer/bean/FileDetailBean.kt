package com.smile.qzclould.ui.transfer.bean

import java.io.Serializable

data class FileDetailBean(
        val downloadAddress: String,
        val identity: String,
        val hash: String,
        val mime: String,
        val name: String
): Serializable {
    var position: Int = 0
}