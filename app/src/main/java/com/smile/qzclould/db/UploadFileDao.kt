package com.smile.qzclould.db

import android.arch.persistence.room.*

/**
 * Created by wangzhg on 2019/3/3
 * Describe:
 */
@Dao
interface UploadFileDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveUploadFiles(uploadFiles: List<UploadFileEntity>)

    @Delete
    fun deleteFiles(uploadFile: UploadFileEntity)

    @Update
    fun updateFiles(uploadFile: UploadFileEntity)

    @Query("SELECT * FROM TBUploadFile")
    fun loadDirecotory(): List<UploadFileEntity>

    @Query("SELECT * FROM TBUploadFile WHERE fileName IN (:filename)")
    fun findFiles(filename: String): List<UploadFileEntity>
}