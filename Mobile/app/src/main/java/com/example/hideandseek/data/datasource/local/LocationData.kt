package com.example.hideandseek.data.datasource.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location_table")
data class LocationData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "relative_time") val relativeTime: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "altitude") val altitude: Double,
    @ColumnInfo(name = "obj_id") val objId: Int,
)
