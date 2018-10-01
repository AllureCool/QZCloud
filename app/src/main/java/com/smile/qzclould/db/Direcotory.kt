package com.smile.qzclould.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.smile.qzclould.ui.transfer.bean.FileDetailBean
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity(tableName = "TBDownloadFile")
data class Direcotory(
        @PrimaryKey
        @NotNull
        val uuid: String, //该文件/文件夹的唯一ID
        val name: String, //文件名
        val mime: String, //文件mime，即文件的类型
        val type: Int, //0:为文件,1:目录
        val parent: String, //父目录id
        val ctime: Long, //文件创建时间
        val mtime: Long, //文件修改时间
        val atime: Long, //文件访问时间
        val userId: Int, //用户id
        val path: String, //该文件或文件夹的访问路径
        val size: Long, //文件大小(字节)
        val flag: Int, //总是 0
        val preview: Int, //预览状态
        val recycle: Int, //0:正常文件，1:回收站

        var isSelected: Boolean, //是否选中
        var isDownloading: Boolean = false, //是否处于下载状态
        var downloadStatus: Int = 0, //0: 未下载 1：正在下载 2：暂停 3：下载失败 4：下载完成
        var downloadSize: Int = 0, //已经下载的大小
        var downProgress: Int =  0, //下载进度
        var totalSize: Int = 0, //文件总大小
        var taskId: Int = 0, //下载任务id
        var fileDetail: FileDetailBean? = null,

        val storeId: String,
        val lft: Int,
        val rgt: Int,
        val version: Int,
        val locking: Boolean
): Serializable