package com.example.slideshowapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.slideshowapp.data.dao.HomePlanDao
import com.example.slideshowapp.data.model.HomePlan
import com.example.slideshowapp.data.util.Converters

@Database(
    entities = [HomePlan::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homePlanDao(): HomePlanDao
} 