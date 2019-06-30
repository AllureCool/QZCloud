package com.smile.qzclould.ui.user.loign.adapter

import android.support.constraint.ConstraintLayout
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.smile.qzclould.R
import com.smile.qzclould.ui.user.loign.bean.UserOtherOnlineBean
import com.smile.qzclould.utils.DateUtils

/**
 * Created by wangzhg on 2019/6/30
 * Describe:
 */
class UserOnlineAdapter: BaseQuickAdapter<UserOtherOnlineBean, BaseViewHolder>(R.layout.item_user_online) {

    val deviceList = mutableListOf<String>()

    override fun convert(helper: BaseViewHolder?, item: UserOtherOnlineBean?) {
        with(helper?.getView<TextView>(R.id.tvDevice)) {
            this?.text = item?.ssid
        }
        with(helper?.getView<TextView>(R.id.tvDate)) {
            this?.text = DateUtils.dateFormatHMS(item?.refreshTime!!)
        }
        with(helper?.getView<ConstraintLayout>(R.id.clItem)) {
            var isSelected = false
            this?.setOnClickListener {
                isSelected = !isSelected
                it.tag = isSelected
                if(isSelected) {
                    deviceList.add(item?.ssid!!)
                    helper?.getView<TextView>(R.id.tvDevice)?.setTextColor(mContext.resources.getColor(R.color.color_green_2EC17C))
                } else {
                    deviceList.remove(item?.ssid!!)
                    helper?.getView<TextView>(R.id.tvDevice)?.setTextColor(mContext.resources.getColor(R.color.color_black_404040))
                }
            }
        }
    }
}