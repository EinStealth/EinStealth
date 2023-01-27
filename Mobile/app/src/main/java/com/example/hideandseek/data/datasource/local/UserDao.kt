package com.example.hideandseek.data.datasource.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserData)

    @Update
    fun update(user: UserData)

    @Delete
    fun delete(user: UserData)

    @Query("SELECT * FROM user_table ORDER BY id DESC LIMIT 1")
    suspend fun getLatest(): UserData

    @Query("SELECT * FROM user_table")
    fun getAll(): Flow<List<UserData>>

    @Query("DELETE FROM user_table")
    suspend fun deleteAll()
}
