package com.smile.qzclould.ui.preview.picture

import java.io.Serializable

data class PictureBean(
        val width: Int,
        val height: Int,
        val address: String,
        val url: String
): Serializable