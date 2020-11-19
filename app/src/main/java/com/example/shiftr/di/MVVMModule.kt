package com.example.shiftr.di

import android.content.Context
import com.example.shiftr.model.AppDataSource
import com.example.shiftr.model.AppRepository
import com.example.shiftr.model.DataStoreUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object MVVMModule {

    @Provides
    @Singleton
    fun providerRepository(@ApplicationContext application: Context): AppDataSource {
        return AppRepository(application)
    }

    @Provides
    @Singleton
    fun providerDataStoreUtils(@ApplicationContext application: Context): DataStoreUtils {
        return DataStoreUtils(application)
    }
}