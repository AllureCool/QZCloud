package com.smile.qzclould.ui.cloud.bean

import java.io.Serializable

data class DirecotoryBean(
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
        val alias: String, //文件别名
        val flag: Int, //总是 0
        val preview: Int, //预览状态
        val recycle: Int, //0:正常文件，1:回收站

        val storeId: String,
        val pathId: String,
        val lft: Int,
        val rgt: Int,
        val exit: String,
        val version: Int,
        val locking: Boolean
): Serializable