package com.smile.qzclould.common.base

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.smile.qzclould.db.*

@Database(entities = [User::class, Direcotory::class], version = 4, exportSchema = false)
@TypeConverters(NormalConverter::class)
abstract class CloudDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun DirecotoryDao(): DirecotoryDao
}
