package com.smile.qzclould.ui.preview.player.bean

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
    ) {
        var isPlay: Boolean = false //当前清晰度的流是否在播放
    }
}