package com.smile.qzclould.common

/**
 * Created by wangzhg on 2018/8/31
 * Describe:
 */

object Constants {

    var pathList = mutableListOf<String>()

    const val MIME_IMG = "image/"
    const val MIME_FOLDER = "application/x-directory"
    const val MIME_TEXT = "text/"
    const val MIME_AUDIO = "audio/"
    const val MIME_VIDEO = "video/"
    const val MIME_PDF = "application/pdf"
    const val MIME_EXCEL = "-excel"
    const val MIME_DOC = "application/msword"
    const val MIME_ZIP = "application/zip"

    const val KEY_CLOUD_SP = "key_cloud_sp"
    const val USER_INFO = "user_info"
    const val USER_TOKEN = "user_token"
    const val TOAST_SUCCESS = 0
    const val TOAST_ERROR = 1
    const val TOAST_NORMAL = 2
}
