package com.smile.qzclould.ui.transfer.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.db.UploadFileEntity
import io.netopen.hotbitmapgg.library.view.RingProgressBar

/**
 * Created by wangzhg on 2019/3/2
 * Describe:
 */
class UploadAdapter: BaseQuickAdapter<UploadFileEntity, BaseViewHolder>(R.layout.item_upload) {

    override fun convert(helper: BaseViewHolder?, item: UploadFileEntity?) {
        with(helper?.getView(R.id.mIcon) as ImageView) {
            Glide.with(mContext).load(item?.filePath).into(this)
        }
        helper?.setText(R.id.mTvFileName, item?.fileName)
        when(item?.status) {
            0 -> {
                helper.setText(R.id.mTvUploadStatus, "等待上传...")
                helper.getView<TextView>(R.id.mTvProgress).visibility = View.GONE
                helper?.getView<RingProgressBar>(R.id.mDlProgress).visibility = View.GONE
            }
            1 -> {
                helper.setText(R.id.mTvUploadStatus, "正在上传...")
                helper.getView<TextView>(R.id.mTvProgress).visibility = View.VISIBLE
                helper?.getView<RingProgressBar>(R.id.mDlProgress).visibility = View.VISIBLE
                helper.getView<TextView>(R.id.mTvProgress).text = item.uploadPercent.toString()
                helper?.getView<RingProgressBar>(R.id.mDlProgress).progress = item.uploadPercent
            }
            2 -> {
                helper.setText(R.id.mTvUploadStatus, "上传成功")
                helper.getView<TextView>(R.id.mTvProgress).visibility = View.GONE
                helper?.getView<RingProgressBar>(R.id.mDlProgress).visibility = View.GONE
            }
            3 -> {
                helper.setText(R.id.mTvUploadStatus, "上传失败")
                helper.getView<TextView>(R.id.mTvProgress).visibility = View.GONE
                helper?.getView<RingProgressBar>(R.id.mDlProgress).visibility = View.GONE
            }
        }
    }
}