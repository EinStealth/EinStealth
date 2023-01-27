package com.example.hideandseek.di

import android.content.Context
import androidx.room.Room
import com.example.hideandseek.data.datasource.local.LocationDao
import com.example.hideandseek.data.datasource.local.LocationRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Singleton
    @Provides
    fun provideLocationRoomDatabase(
        @ApplicationContext context: Context
    ): LocationRoomDatabase {
        return Room.databaseBuilder(
            context,
            LocationRoomDatabase::class.java,
            "location_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideLocationDao(db: LocationRoomDatabase): LocationDao {
        return db.locationDao()
    }
}