package com.smile.qzclould.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import java.io.Serializable

/**
 * Created by wangzhg on 2019/3/3
 * Describe:
 */
@Entity(tableName = "TBUploadFile")
class UploadFileEntity(
        @PrimaryKey
        @NotNull
        val fileName: String,
        val filePath: String,
        var status: Int, //0:等待上传 1:正在上传 2：上传成功 3：上传失败
        var uploadPercent: Int
): Serializable