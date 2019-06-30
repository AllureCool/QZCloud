package com.smile.qzclould.ui.preview.picture

import java.io.Serializable

data class PictureBeanV2(
        val width: Int,
        val height: Int,
        val previewHlsAddress: String,
        val sourcePath: String
): Serializable