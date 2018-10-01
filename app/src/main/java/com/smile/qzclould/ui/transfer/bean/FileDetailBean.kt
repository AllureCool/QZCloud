package com.smile.qzclould.ui.transfer.bean

import com.chad.library.adapter.base.BaseViewHolder

data class FileDetailBean(
        val uuid: String,
        val downloadAddress: String,
        val mime: String,
        val name: String
) {
    var position: Int = 0
//    var holder: BaseViewHolder? = null
}