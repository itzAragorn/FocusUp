package com.example.focusup.data.repository

import com.example.focusup.data.db.UserDao
import com.example.focusup.data.model.UserEntity

class UserRepository(private val userDao: UserDao){

    suspend fun registerUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: UserEntity) {
        userDao.deleteUser(user)
    }

    suspend fun loginUser(correo: String, password: String): UserEntity? {
        return userDao.login(correo, password)
    }

    suspend fun getUserByEmail(correo: String): UserEntity? {
        return userDao.getUserbyEmail(correo)
    }

    suspend fun getAllUsers(): List<UserEntity> {
        return userDao.getAllUsers()
    }
}