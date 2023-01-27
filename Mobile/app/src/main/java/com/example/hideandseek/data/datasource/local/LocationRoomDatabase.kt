package com.example.hideandseek.data.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [LocationData::class], version = 1, exportSchema = false)
abstract class LocationRoomDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
}
