package com.example.slideshowapp.di

import android.content.Context
import com.example.slideshowapp.data.AppDatabase
import com.example.slideshowapp.data.dao.SlideDao
import com.example.slideshowapp.data.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideSlideDao(database: AppDatabase): SlideDao {
        return database.slideDao()
    }
} 