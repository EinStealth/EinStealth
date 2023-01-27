package com.example.hideandseek.data.repository

import androidx.annotation.WorkerThread
import com.example.hideandseek.data.datasource.local.UserDao
import com.example.hideandseek.data.datasource.local.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserRepository {
    val allUsers: Flow<List<UserData>>

    suspend fun getLatest(): UserData

    suspend fun insert(user: UserData)

    suspend fun deleteAll()
}

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
) : UserRepository {

    override val allUsers: Flow<List<UserData>>
        get() = userDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    override suspend fun getLatest(): UserData {
        return userDao.getLatest()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    override suspend fun insert(user: UserData) {
        userDao.insert(user)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    override suspend fun deleteAll() {
        userDao.deleteAll()
    }
}
