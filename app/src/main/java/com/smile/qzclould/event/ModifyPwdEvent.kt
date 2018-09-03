package com.smile.qzclould.event

data class ModifyPwdEvent(
        val phoneInfo: String,
        val toobarTitle: String,
        val jumpType: Int
)