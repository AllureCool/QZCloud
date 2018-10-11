package com.smile.qzclould.ui.transfer.adapter

import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.common.Constants
import com.smile.qzclould.db.Direcotory
import com.smile.qzclould.utils.DateUtils

class DownloadPathSelectAdapter : BaseQuickAdapter<Direcotory, BaseViewHolder> {
    private var mCheckListener: OnCheckListener? = null

    constructor() : super(R.layout.item_file)

    fun setOnCheckListener(listener: OnCheckListener) {
        mCheckListener = listener
    }

    override fun convert(helper: BaseViewHolder?, item: Direcotory?) {
        helper?.setText(R.id.mTvFileName, item?.name)
        helper?.setText(R.id.mTvDate, DateUtils.dateFormat(item?.ctime!!))

        with(helper?.getView<ImageView>(R.id.mIvSelect)) {
            this?.isSelected = item!!.isSelected
        }
        helper?.getView<ImageView>(R.id.mIvSelect)?.setOnClickListener {
            for (file in data) {
                file.isSelected = false
            }
            item?.isSelected = true
            notifyDataSetChanged()
            mCheckListener?.onChecked(helper.adapterPosition, item)
        }

        helper?.getView<ConstraintLayout>(R.id.mClItem)?.setOnClickListener {
            mCheckListener?.onItemClick(helper.adapterPosition, item)
        }
    }

    interface OnCheckListener {
        fun onChecked(position: Int, item: Direcotory?)

        fun onItemClick(position: Int, item: Direcotory?)

        fun onItemLongClick(position: Int, item: Direcotory?)
    }
}