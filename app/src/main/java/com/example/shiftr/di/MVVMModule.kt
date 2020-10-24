package com.example.shiftr.di

import com.example.shiftr.model.AppDataSource
import com.example.shiftr.model.AppRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object MVVMModule {

    @Provides
    @Singleton
    fun providerRepository(): AppDataSource {
        return AppRepository()
    }
}