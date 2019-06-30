package com.smile.qzclould.db

import android.arch.persistence.room.*
import android.database.Cursor

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

    @Delete
    fun deleteDirecotory(direcotoryList: List<Direcotory>)

    @Query("SELECT * FROM TableDownloadFile")
    fun loadDirecotory(): List<Direcotory>

}