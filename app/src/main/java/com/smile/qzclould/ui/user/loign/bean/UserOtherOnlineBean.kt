package com.smile.qzclould.ui.user.loign.bean

import java.io.Serializable

/**
 * Created by wangzhg on 2019/6/30
 * Describe:
 */
data class UserOtherOnlineBean(
        val device: String,
        val ignoreCase: Boolean,
        val refreshTime: Long,
        val ssid: String,
        val status: Int
): Serializable