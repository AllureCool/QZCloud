package com.smile.qzclould.repository.upload

/**
 * Created by wangzhg on 2019/3/2
 * Describe:
 */
data class UploadFileBean(
        val fileName: String,
        val filePath: String,
        var status: Int, //0:等待上传 1:正在上传 2：上传成功 3：上传失败
        var uploadPercent: Int
)