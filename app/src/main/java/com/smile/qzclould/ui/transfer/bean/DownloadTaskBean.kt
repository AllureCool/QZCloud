package com.smile.qzclould.ui.transfer.bean

import java.io.Serializable

data class DownloadTaskBean(
    val page: Int,
    val pageSize: Int,
    val totalCount: Int,
    val totalPage: Int,
    val list: List<Task>
): Serializable {
    data class Task(
            val userId: Int,
            val taskId: String,
            val copyFile: String,
            val copiedFile: String,
            val createTime: Long,
            val savePath: String,
            val filePath: String,
            val copied: Long,
            val statue: Int,
            val name: String,
            val mime: String,
            val progress: Int,
            val finishedSize: Long,
            val size: Long,
            val errorCode: Int,
            val serverId: String
    ): Serializable
}