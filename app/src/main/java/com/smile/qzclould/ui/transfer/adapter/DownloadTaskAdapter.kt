package com.smile.qzclould.ui.transfer.adapter

import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants
import com.smile.qzclould.ui.transfer.bean.DownloadTaskBean
import com.smile.qzclould.utils.DateUtils

class DownloadTaskAdapter: BaseQuickAdapter<DownloadTaskBean.Task, BaseViewHolder>(R.layout.item_file) {

    override fun convert(helper: BaseViewHolder?, item: DownloadTaskBean.Task?) {
        with(helper?.getView<ImageView>(R.id.mIcon)) {
            when {
                item?.mime == Constants.MIME_FOLDER -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_directory))
                item?.mime == Constants.MIME_IMG -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_image))
                else -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_directory))
            }
        }
        helper?.setText(R.id.mTvFileName, item?.name)
        helper?.setText(R.id.mTvDate, DateUtils.dateFormat(item?.createTime!!))
        helper?.getView<ImageView>(R.id.mIvSelect)?.visibility = View.GONE
    }
}