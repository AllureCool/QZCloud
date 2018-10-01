package com.smile.qzclould.db

import android.arch.persistence.room.*

@Dao
interface DirecotoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveDirecotoryList(direcotoryList: List<Direcotory>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveDirecotory(direcotory: Direcotory)

    @Update
    fun updateDirecotoryInfo(direcotory: Direcotory)

    @Delete
    fun deleteDirecotory(direcotory: Direcotory)

    @Query("SELECT * FROM TBDownloadFile")
    fun loadDirecotory(): List<Direcotory>
}