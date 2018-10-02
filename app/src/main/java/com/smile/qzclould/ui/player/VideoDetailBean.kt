package com.smile.qzclould.ui.player

import java.io.Serializable

data class VideoDetailBean(
        val preview: List<VideoInfo>,
        val clearTexts: List<String>
): Serializable {
    data class VideoInfo(
            val clearText: String,
            val clear: Int,
            val resolution: String,
            val duration: Long,
            val url: String
    )
}