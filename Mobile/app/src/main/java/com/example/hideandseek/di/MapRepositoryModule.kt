package com.example.hideandseek.di

import com.example.hideandseek.data.repository.MapRepository
import com.example.hideandseek.data.repository.MapRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.selects.SelectInstance
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapRepositoryModule {
    @Binds
    abstract fun bindMapRepository(
        impl: MapRepositoryImpl
    ): MapRepository
}