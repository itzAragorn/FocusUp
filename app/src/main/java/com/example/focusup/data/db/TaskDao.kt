package com.example.focusup.data.db

import androidx.room.*
import com.example.focusup.data.model.TaskEntity

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY fecha ASC, hora ASC")
    suspend fun getTasksByUser(userId: Int): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE userId = :userId AND fecha = :fecha")
    suspend fun getTasksByDate(userId: Int, fecha: String): List<TaskEntity>

}