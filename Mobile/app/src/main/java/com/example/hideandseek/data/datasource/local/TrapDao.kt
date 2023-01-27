package com.example.hideandseek.data.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TrapDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(trap: TrapData)

    @Update
    fun update(trap: TrapData)

    @Delete
    fun delete(trap: TrapData)

    @Query("SELECT * FROM trap_table")
    fun getAll(): Flow<List<TrapData>>

    @Query("DELETE FROM trap_table")
    suspend fun deleteAll()
}
