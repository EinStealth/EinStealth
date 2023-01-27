package com.example.hideandseek.di

import android.content.Context
import androidx.room.Room
import com.example.hideandseek.data.datasource.local.LocationDao
import com.example.hideandseek.data.datasource.local.LocationRoomDatabase
import com.example.hideandseek.data.datasource.local.UserDao
import com.example.hideandseek.data.datasource.local.UserRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Singleton
    @Provides
    fun provideUserRoomDatabase(
        @ApplicationContext context: Context
    ): UserRoomDatabase {
        return Room.databaseBuilder(
            context,
            UserRoomDatabase::class.java,
            "user_db4"
        ).build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: UserRoomDatabase): UserDao {
        return db.userDao()
    }
}