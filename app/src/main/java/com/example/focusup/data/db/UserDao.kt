package com.example.focusup.data.db

import androidx.room.*
import com.example.focusup.data.model.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE correo = :correo LIMIT 1")
    suspend fun getUserbyEmail(correo: String): UserEntity?

    @Query("SELECT * FROM users WHERE correo = :correo AND password = :password")
    suspend fun login(correo: String, password: String): UserEntity?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}