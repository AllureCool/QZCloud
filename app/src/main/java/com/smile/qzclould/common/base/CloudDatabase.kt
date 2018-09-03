package com.smile.qzclould.common.base

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.smile.qzclould.db.User
import com.smile.qzclould.db.UserDao

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class CloudDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
}
