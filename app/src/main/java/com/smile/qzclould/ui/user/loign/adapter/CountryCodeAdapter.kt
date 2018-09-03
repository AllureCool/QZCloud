package com.smile.qzclould.ui.user.loign.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.ui.user.loign.bean.CountryCodeBean

class CountryCodeAdapter: BaseQuickAdapter<CountryCodeBean, BaseViewHolder>(R.layout.item_country_code) {

    var mSelectPos: Int = 0

    override fun setOnItemClick(v: View?, position: Int) {
        super.setOnItemClick(v, position)
        mSelectPos = position
        notifyDataSetChanged()
    }

    override fun convert(helper: BaseViewHolder?, item: CountryCodeBean?) {
        with(helper?.getView<TextView>(R.id.mTvCode)) {
            this?.text = "+${item?.countryCode}"
            if(mSelectPos == helper?.layoutPosition) {
                this?.setTextColor(mContext.resources.getColor(R.color.color_green_2EC17C))
            } else {
                this?.setTextColor(mContext.resources.getColor(R.color.color_black_404040))
            }
        }

        with(helper?.getView<TextView>(R.id.mTvCountry)) {
            this?.text = item?.countryName
            if(mSelectPos == helper?.layoutPosition) {
                this?.setTextColor(mContext.resources.getColor(R.color.color_green_2EC17C))
            } else {
                this?.setTextColor(mContext.resources.getColor(R.color.color_black_404040))
            }
        }

        if(helper?.layoutPosition == mSelectPos) {
            helper?.getView<ImageView>(R.id.mIvCheck)?.visibility = View.VISIBLE
        } else {
            helper?.getView<ImageView>(R.id.mIvCheck)?.visibility = View.GONE
        }
    }
}