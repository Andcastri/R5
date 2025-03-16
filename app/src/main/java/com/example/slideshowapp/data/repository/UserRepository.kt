package com.example.slideshowapp.data.repository

import com.example.slideshowapp.data.dao.UserDao
import com.example.slideshowapp.data.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun createUser(user: User) {
        userDao.insertUser(user)
    }

    fun getAdminUsers(): Flow<List<User>> {
        return userDao.getAdminUsers()
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }
} 