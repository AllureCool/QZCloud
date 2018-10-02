package com.smile.qzclould.ui.task.adapter

import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants
import com.smile.qzclould.utils.DLog
import com.smile.qzclould.utils.DateUtils
import com.smile.qzclould.utils.FileUtils
import java.io.File

class FileDownloadCompleteAdapter: BaseQuickAdapter<File, BaseViewHolder>(R.layout.item_file) {

    private var mOnFilRemoveListener: OnFileRemoveListener? = null

    fun setOnFileRemoveListener(onFileRemoveListener: OnFileRemoveListener) {
        mOnFilRemoveListener = onFileRemoveListener
    }

    override fun convert(helper: BaseViewHolder?, item: File?) {

        with(helper?.getView<ImageView>(R.id.mIcon)) {
            val mimeType = FileUtils.getMIMEType(item)
            when {
                mimeType.contains(Constants.MIME_FOLDER) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_directory))
                mimeType.contains(Constants.MIME_IMG) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_image))
                mimeType.contains(Constants.MIME_AUDIO) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_mp3))
                mimeType.contains(Constants.MIME_TEXT) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_txt))
                mimeType.contains(Constants.MIME_VIDEO) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_video))
                mimeType.contains(Constants.MIME_DOC) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_doc))
                mimeType.contains(Constants.MIME_EXCEL) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_excel))
                mimeType.contains(Constants.MIME_PDF) -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_mime_pdf))
                else -> this?.setImageDrawable(mContext.resources.getDrawable(R.mipmap.img_file_unkonw))
            }
        }

        helper?.setText(R.id.mTvFileName, item?.name)

        helper?.setText(R.id.mTvDate, DateUtils.dateFormat(item?.lastModified()!!))

        helper?.getView<ImageView>(R.id.mIvSelect)?.visibility = View.GONE

        helper?.getView<ConstraintLayout>(R.id.mClItem)?.setOnLongClickListener {
            mOnFilRemoveListener?.onRemove(item!!)
            return@setOnLongClickListener true
        }
    }

    interface OnFileRemoveListener {
        fun onRemove(file: File)
    }
}