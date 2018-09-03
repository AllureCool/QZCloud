package com.smile.qzclould.db

import android.arch.persistence.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUserInfo(user: User)

    @Delete
    fun deleteUser(user: User)

    @Update
    fun updateUserInfo(user: User)

    @Query("SELECT * FROM User")
    fun loadUserInfo(): List<User>
}