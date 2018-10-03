package com.smile.qzclould.ui.preview.player.adapter

import android.widget.FrameLayout
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.ui.preview.player.bean.VideoDetailBean

class ClarityAdapter: BaseQuickAdapter<VideoDetailBean.VideoInfo, BaseViewHolder>(R.layout.item_select_clarity) {

    override fun convert(helper: BaseViewHolder?, item: VideoDetailBean.VideoInfo?) {
        if(item!!.isPlay) {
            helper?.getView<FrameLayout>(R.id.fl_clarity)?.setBackgroundColor(mContext.resources.getColor(R.color.color_green_2EC17C))
        } else {
            helper?.getView<FrameLayout>(R.id.fl_clarity)?.setBackgroundColor(mContext.resources.getColor(R.color.transparent))
        }
        helper?.setText(R.id.mTvClarity, item?.clearText)
    }
}