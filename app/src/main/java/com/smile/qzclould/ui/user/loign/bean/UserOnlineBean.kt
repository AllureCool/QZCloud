package com.smile.qzclould.ui.user.loign.bean

import java.io.Serializable

/**
 * Created by wangzhg on 2019/6/30
 * Describe:
 */
data class UserOnlineBean (
        val self: UserSelfOnlineBean,
        val online: List<UserOtherOnlineBean>
): Serializable