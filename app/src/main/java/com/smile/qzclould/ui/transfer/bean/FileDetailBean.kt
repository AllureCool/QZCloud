package com.smile.qzclould.ui.transfer.bean

import java.io.Serializable

data class FileDetailBean(
        val uuid: String,
        val downloadAddress: String,
        val mime: String,
        val name: String
): Serializable {
    var position: Int = 0
}