package com.example.hideandseek.data.repository

import androidx.annotation.WorkerThread
import com.example.hideandseek.data.datasource.local.LocationDao
import com.example.hideandseek.data.datasource.local.LocationData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LocationRepository {
    val allLocations: Flow<List<LocationData>>

    suspend fun insert(location: LocationData)

    suspend fun deleteAll()
}

class LocationRepositoryImpl @Inject constructor(
    private val locationDao: LocationDao,
) : LocationRepository {

    override val allLocations: Flow<List<LocationData>>
        get() = locationDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    override suspend fun insert(user: LocationData) {
        locationDao.insert(user)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    override suspend fun deleteAll() {
        locationDao.deleteAll()
    }
}
