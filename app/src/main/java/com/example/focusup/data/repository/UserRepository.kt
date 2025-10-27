package com.example.focusup.data.repository

import com.example.focusup.data.database.dao.UserDao
import com.example.focusup.data.database.entities.User

class UserRepository(
    private val userDao: UserDao
) {
    
    suspend fun loginUser(email: String, password: String): User? {
        return userDao.getUserByCredentials(email, password)
    }
    
    suspend fun registerUser(user: User): Result<Long> {
        return try {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                Result.failure(Exception("Email already exists"))
            } else {
                val userId = userDao.insertUser(user)
                Result.success(userId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserById(id: Long): User? {
        return userDao.getUserById(id)
    }
    
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
    
    suspend fun isEmailExists(email: String): Boolean {
        return userDao.isEmailExists(email) > 0
    }
}