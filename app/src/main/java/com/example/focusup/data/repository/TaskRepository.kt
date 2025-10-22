package com.example.focusup.data.repository

import com.example.focusup.data.db.TaskDao
import com.example.focusup.data.model.TaskEntity

class TaskRepository(private val taskDao: TaskDao) {

    suspend fun addTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }

    suspend fun getTasksByUser(userId: Int): List<TaskEntity> {
        return taskDao.getTasksByUser(userId)
    }

    suspend fun getTasksByDate(userId: Int, fecha: String): List<TaskEntity> {
        return taskDao.getTasksByDate(userId, fecha)
    }
}