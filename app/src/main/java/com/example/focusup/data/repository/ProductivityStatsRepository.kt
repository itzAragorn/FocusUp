package com.example.focusup.data.repository

import com.example.focusup.data.database.dao.ProductivityStatsDao
import com.example.focusup.data.database.entities.ProductivityStats
import kotlinx.coroutines.flow.Flow

class ProductivityStatsRepository(private val statsDao: ProductivityStatsDao) {
    
    fun getStatsForDate(userId: Long, date: String): Flow<ProductivityStats?> {
        return statsDao.getStatsForDateFlow(userId, date)
    }
    
    fun getRecentStats(userId: Long, limit: Int = 30): Flow<List<ProductivityStats>> {
        return statsDao.getRecentStats(userId, limit)
    }
    
    fun getAllStats(userId: Long): Flow<List<ProductivityStats>> {
        return statsDao.getAllStats(userId)
    }
    
    fun getTotalTasksCompleted(userId: Long): Flow<Int?> {
        return statsDao.getTotalTasksCompleted(userId)
    }
    
    fun getTotalPomodoros(userId: Long): Flow<Int?> {
        return statsDao.getTotalPomodoros(userId)
    }
    
    fun getTotalStudyTime(userId: Long): Flow<Int?> {
        return statsDao.getTotalStudyTime(userId)
    }
    
    fun getLongestStreak(userId: Long): Flow<Int?> {
        return statsDao.getLongestStreak(userId)
    }
    
    fun getCurrentStreak(userId: Long): Flow<ProductivityStats?> {
        return statsDao.getCurrentStreak(userId)
    }
    
    fun getWeeklyStats(userId: Long, startDate: String, endDate: String): Flow<List<ProductivityStats>> {
        return statsDao.getWeeklyStats(userId, startDate, endDate)
    }
    
    fun getMonthlyStats(userId: Long, startDate: String, endDate: String): Flow<List<ProductivityStats>> {
        return statsDao.getMonthlyStats(userId, startDate, endDate)
    }
    
    suspend fun updateStats(stats: ProductivityStats) {
        statsDao.update(stats)
    }
    
    suspend fun insertStats(stats: ProductivityStats): Long {
        return statsDao.insert(stats)
    }
    
    suspend fun incrementTaskCompleted(userId: Long, date: String) {
        val stats = statsDao.getStatsForDate(userId, date)
        if (stats != null) {
            statsDao.update(stats.copy(tasksCompleted = stats.tasksCompleted + 1))
        } else {
            statsDao.insert(
                ProductivityStats(
                    userId = userId,
                    date = date,
                    tasksCompleted = 1,
                    tasksCreated = 0
                )
            )
        }
    }
    
    suspend fun incrementTaskCreated(userId: Long, date: String) {
        val stats = statsDao.getStatsForDate(userId, date)
        if (stats != null) {
            statsDao.update(stats.copy(tasksCreated = stats.tasksCreated + 1))
        } else {
            statsDao.insert(
                ProductivityStats(
                    userId = userId,
                    date = date,
                    tasksCompleted = 0,
                    tasksCreated = 1
                )
            )
        }
    }
    
    suspend fun incrementPomodoro(userId: Long, date: String) {
        val stats = statsDao.getStatsForDate(userId, date)
        if (stats != null) {
            statsDao.update(stats.copy(pomodorosCompleted = stats.pomodorosCompleted + 1))
        } else {
            statsDao.insert(
                ProductivityStats(
                    userId = userId,
                    date = date,
                    pomodorosCompleted = 1
                )
            )
        }
    }
    
    suspend fun addStudyTime(userId: Long, date: String, minutes: Int) {
        val stats = statsDao.getStatsForDate(userId, date)
        if (stats != null) {
            statsDao.update(stats.copy(studyTimeMinutes = stats.studyTimeMinutes + minutes))
        } else {
            statsDao.insert(
                ProductivityStats(
                    userId = userId,
                    date = date,
                    studyTimeMinutes = minutes
                )
            )
        }
    }
}
