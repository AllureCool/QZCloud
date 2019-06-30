package com.smile.qzclould.ui.user.loign.bean

import java.io.Serializable

/**
 * Created by wangzhg on 2019/6/30
 * Describe:
 */
class UserSelfOnlineBean(
        val identity: Int,
        val ignoreCase: Boolean,
        val ssid: String,
        val version: Int,
        val iat: Long,
        val ext: Long
): Serializable