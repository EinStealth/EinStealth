package com.example.hideandseek.di

import com.example.hideandseek.data.repository.ApiRepository
import com.example.hideandseek.data.repository.ApiRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApiRepositoryModule {
    @Binds
    abstract fun bindApiRepository(
        impl: ApiRepositoryImpl
    ): ApiRepository
}