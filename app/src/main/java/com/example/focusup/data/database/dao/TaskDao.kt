package com.example.focusup.data.database.dao

import androidx.room.*
import com.example.focusup.data.database.entities.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    
    @Query("SELECT * FROM tasks WHERE userId = :userId ORDER BY date, time")
    fun getTasksByUser(userId: Long): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE userId = :userId AND date = :date ORDER BY time")
    fun getTasksByUserAndDate(userId: Long, date: String): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE userId = :userId AND isCompleted = :isCompleted ORDER BY date, time")
    fun getTasksByUserAndStatus(userId: Long, isCompleted: Boolean): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): Task?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Delete
    suspend fun deleteTask(task: Task)
    
    @Query("DELETE FROM tasks WHERE userId = :userId")
    suspend fun deleteAllTasksByUser(userId: Long)
    
    @Query("SELECT * FROM tasks WHERE userId = :userId AND isNotificationEnabled = 1 AND isCompleted = 0")
    fun getTasksWithNotificationsEnabled(userId: Long): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE parentTaskId = :parentTaskId ORDER BY date, time")
    fun getChildTasks(parentTaskId: Long): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE recurrenceType != 'NONE' AND parentTaskId IS NULL")
    fun getRecurringParentTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE userId = :userId AND date = :date ORDER BY time")
    suspend fun getTasksForUserAndDateSync(userId: Long, date: String): List<Task>
}