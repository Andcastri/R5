package com.example.slideshowapp.data.dao

import androidx.room.*
import com.example.slideshowapp.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE isAdmin = 1")
    fun getAdminUsers(): Flow<List<User>>

    @Delete
    suspend fun deleteUser(user: User)
} 