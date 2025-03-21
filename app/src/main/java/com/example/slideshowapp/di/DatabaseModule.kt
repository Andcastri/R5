package com.example.slideshowapp.di

import android.content.Context
import androidx.room.Room
import com.example.slideshowapp.data.dao.HomePlanDao
import com.example.slideshowapp.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "homeplans_database"
        ).build()
    }

    @Provides
    fun provideHomePlanDao(database: AppDatabase): HomePlanDao {
        return database.homePlanDao()
    }
} 