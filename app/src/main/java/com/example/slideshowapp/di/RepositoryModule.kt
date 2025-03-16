package com.example.slideshowapp.di

import com.example.slideshowapp.data.api.HomePlanApi
import com.example.slideshowapp.data.dao.HomePlanDao
import com.example.slideshowapp.data.repository.HomePlanRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideHomePlanRepository(
        api: HomePlanApi,
        dao: HomePlanDao
    ): HomePlanRepository {
        return HomePlanRepository(api, dao)
    }
} 