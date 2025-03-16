package com.example.slideshowapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.slideshowapp.data.dao.SlideDao
import com.example.slideshowapp.data.dao.UserDao
import com.example.slideshowapp.data.model.Slide
import com.example.slideshowapp.data.model.User

@Database(entities = [User::class, Slide::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun slideDao(): SlideDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "slideshow_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
} 