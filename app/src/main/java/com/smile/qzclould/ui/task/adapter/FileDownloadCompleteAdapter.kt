package com.smile.qzclould.ui.task.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.utils.DLog
import com.smile.qzclould.utils.DateUtils
import com.smile.qzclould.utils.FileUtils
import java.io.File

class FileDownloadCompleteAdapter: BaseQuickAdapter<File, BaseViewHolder>(R.layout.item_file) {

    override fun convert(helper: BaseViewHolder?, item: File?) {
        helper?.setText(R.id.mTvFileName, item?.name)

        helper?.setText(R.id.mTvDate, DateUtils.dateFormat(item?.lastModified()!!))

        DLog.i(FileUtils.getMIMEType(item) + "------------------------------")
    }
}