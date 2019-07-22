package com.smile.qzclould.ui.preview.video

import java.io.Serializable

data class VideoBeanV2(
        val name: String,
        val duration: Long,
        val width: Int,
        val height: Int,
        val previewHlsAddress: String,
        val sourcePath: String
): Serializable