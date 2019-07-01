package com.smile.qzclould.ui.transfer.adapter

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants
import com.smile.qzclould.ui.transfer.bean.DownloadTaskBean
import com.smile.qzclould.ui.transfer.bean.OfflineListBean
import com.smile.qzclould.utils.DateUtils

class DownloadTaskAdapter: BaseQuickAdapter<OfflineListBean.OfflineInfo, BaseViewHolder>(R.layout.item_offline) {

    private var mOnItemRemoveListener: OnItemRemoveListener? = null

    fun setItemRemoveListener(onItemRemoveListener: OnItemRemoveListener) {
        mOnItemRemoveListener = onItemRemoveListener
    }

    override fun convert(helper: BaseViewHolder?, item: OfflineListBean.OfflineInfo?) {
//        with(helper?.getView<ImageView>(R.id.mIcon)) {
//            when {
//                item?.mime!!.contains(Constants.MIME_FOLDER) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_directory))
//                item.mime.contains(Constants.MIME_IMG) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_image))
//                item.mime.contains(Constants.MIME_AUDIO) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_mp3))
//                item.mime.contains(Constants.MIME_TEXT) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_txt))
//                item.mime.contains(Constants.MIME_VIDEO) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_video))
//                item.mime.contains(Constants.MIME_DOC) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_doc))
//                item.mime.contains(Constants.MIME_EXCEL) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_excel))
//                item.mime.contains(Constants.MIME_PDF) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_pdf))
//                item.mime.contains(Constants.MIME_ZIP) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_zip))
//                else -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_file_unkonw))
//            }
//        }
        helper?.setText(R.id.mTvFileName, item?.name)
        if(item?.progress == 100) {
            helper?.getView<TextView>(R.id.mTvDate)?.setTextColor(mContext.resources.getColor(R.color.color_green_2EC17C))
            helper?.setText(R.id.mTvDownloadStatus, "已完成")
            helper?.getView<TextView>(R.id.mTvDownloadTip)?.visibility = View.GONE
        } else {
            helper?.getView<TextView>(R.id.mTvDate)?.setTextColor(mContext.resources.getColor(R.color.color_gray_919191))
            helper?.setText(R.id.mTvDownloadStatus, "已完成${item?.progress}%")
            helper?.getView<TextView>(R.id.mTvDownloadTip)?.visibility = View.VISIBLE
        }
        helper?.getView<ImageView>(R.id.mIvSelect)?.visibility = View.GONE

    }

    interface OnItemRemoveListener {
        fun onRemoved(file: DownloadTaskBean.Task?)
    }
}