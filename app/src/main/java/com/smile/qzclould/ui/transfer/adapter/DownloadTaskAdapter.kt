package com.smile.qzclould.ui.transfer.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.ui.cloud.bean.DirecotoryBean
import com.smile.qzclould.ui.transfer.bean.DownloadTaskBean
import com.smile.qzclould.utils.DateUtils

class DownloadTaskAdapter: BaseQuickAdapter<DownloadTaskBean.Task, BaseViewHolder>(R.layout.item_file) {

    override fun convert(helper: BaseViewHolder?, item: DownloadTaskBean.Task?) {
        helper?.setText(R.id.mTvFileName, item?.name)
        helper?.setText(R.id.mTvDate, DateUtils.dateFormat(item?.createTime!!))
    }
}