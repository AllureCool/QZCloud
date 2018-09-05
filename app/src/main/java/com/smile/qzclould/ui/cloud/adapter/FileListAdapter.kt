package com.smile.qzclould.ui.cloud.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.ui.cloud.bean.DirecotoryBean
import com.smile.qzclould.utils.DateUtils

class FileListAdapter: BaseQuickAdapter<DirecotoryBean, BaseViewHolder>(R.layout.item_file) {

    override fun convert(helper: BaseViewHolder?, item: DirecotoryBean?) {
        helper?.setText(R.id.mTvFileName, item?.name)
        helper?.setText(R.id.mTvDate, DateUtils.dateFormat(item?.ctime!!))
    }
}