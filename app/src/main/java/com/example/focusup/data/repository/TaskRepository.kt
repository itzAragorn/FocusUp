package com.example.focusup.data.repository

import com.example.focusup.data.database.dao.TaskDao
import com.example.focusup.data.database.entities.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao
) {
    
    fun getTasksByUser(userId: Long): Flow<List<Task>> {
        return taskDao.getTasksByUser(userId)
    }
    
    fun getTasksByUserAndDate(userId: Long, date: String): Flow<List<Task>> {
        return taskDao.getTasksByUserAndDate(userId, date)
    }
    
    fun getTasksByUserAndStatus(userId: Long, isCompleted: Boolean): Flow<List<Task>> {
        return taskDao.getTasksByUserAndStatus(userId, isCompleted)
    }
    
    suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)
    }
    
    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }
    
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }
    
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }
    
    suspend fun deleteAllTasksByUser(userId: Long) {
        taskDao.deleteAllTasksByUser(userId)
    }
    
    fun getTasksWithNotificationsEnabled(userId: Long): Flow<List<Task>> {
        return taskDao.getTasksWithNotificationsEnabled(userId)
    }
    
    fun getChildTasks(parentTaskId: Long): Flow<List<Task>> {
        return taskDao.getChildTasks(parentTaskId)
    }
    
    fun getRecurringParentTasks(): Flow<List<Task>> {
        return taskDao.getRecurringParentTasks()
    }
}