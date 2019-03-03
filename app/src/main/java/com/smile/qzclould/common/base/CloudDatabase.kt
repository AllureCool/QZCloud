package com.smile.qzclould.common.base

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.smile.qzclould.db.*

@Database(entities = [User::class, Direcotory::class, UploadFileEntity::class], version = 5, exportSchema = false)
@TypeConverters(NormalConverter::class)
abstract class CloudDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun DirecotoryDao(): DirecotoryDao

    abstract fun UploadFileDao(): UploadFileDao
}
