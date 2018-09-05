package com.smile.qzclould.repository.requestbody

data class FileListBody(
        val parent: String, //该文件夹的id(可选)
        val path: String, //路径(可选)
        val start: Int, //从第几条开始
        val size: Int, //列表大小
        val recycle: Int, //默认不显示已经回收的文件。使用-1 显示所有文件，使用1只显示回收站
        val mime: String, //列出文件夹下面某一类型的文件，支持通配符，比如image/*
        val orderBy: Int, //排序 0按 文件名 1 按时间
        val type: Int //文件类型 0 显示文件 1 显示文件夹 -1 显示文件和文件夹
)