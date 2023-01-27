package com.example.hideandseek.di

import android.content.Context
import androidx.room.Room
import com.example.hideandseek.data.datasource.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TrapModule {
    @Singleton
    @Provides
    fun provideTrapRoomDatabase(
        @ApplicationContext context: Context
    ): TrapRoomDatabase {
        return Room.databaseBuilder(
            context,
            TrapRoomDatabase::class.java,
            "trap_db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideTrapDao(db: TrapRoomDatabase): TrapDao {
        return db.trapDao()
    }
}