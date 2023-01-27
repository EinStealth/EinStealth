package com.example.hideandseek.data.datasource.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: LocationData)

    @Update
    fun update(user: LocationData)

    @Delete
    fun delete(user: LocationData)

    @Query("SELECT * FROM location_table")
    fun getAll(): Flow<List<LocationData>>

    @Query("DELETE FROM location_table")
    suspend fun deleteAll()
}
