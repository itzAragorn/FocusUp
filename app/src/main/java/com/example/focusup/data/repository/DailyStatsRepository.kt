package com.example.focusup.data.repository

import com.example.focusup.data.database.dao.DailyStatsDao
import com.example.focusup.data.database.entities.DailyStats
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class DailyStatsRepository(private val dailyStatsDao: DailyStatsDao) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    fun getAllStatsForUser(userId: Long): Flow<List<DailyStats>> {
        return dailyStatsDao.getAllStatsForUser(userId)
    }
    
    suspend fun getStatsForDate(userId: Long, date: String): DailyStats? {
        return dailyStatsDao.getStatsForDate(userId, date)
    }
    
    suspend fun getStatsForToday(userId: Long): DailyStats? {
        val today = dateFormat.format(Date())
        return dailyStatsDao.getStatsForDate(userId, today)
    }
    
    suspend fun getRecentStats(userId: Long, days: Int = 7): List<DailyStats> {
        return dailyStatsDao.getRecentStats(userId, days)
    }
    
    suspend fun getStatsForWeek(userId: Long): List<DailyStats> {
        val calendar = Calendar.getInstance()
        val endDate = dateFormat.format(calendar.time)
        
        calendar.add(Calendar.DAY_OF_YEAR, -6) // Last 7 days
        val startDate = dateFormat.format(calendar.time)
        
        return dailyStatsDao.getStatsForDateRange(userId, startDate, endDate)
    }
    
    suspend fun getStatsForMonth(userId: Long): List<DailyStats> {
        val calendar = Calendar.getInstance()
        val endDate = dateFormat.format(calendar.time)
        
        calendar.add(Calendar.DAY_OF_YEAR, -29) // Last 30 days
        val startDate = dateFormat.format(calendar.time)
        
        return dailyStatsDao.getStatsForDateRange(userId, startDate, endDate)
    }
    
    suspend fun getCurrentStreak(userId: Long): Int {
        val recentStats = dailyStatsDao.getRecentStats(userId, 30)
        return recentStats.firstOrNull()?.studyStreakDays ?: 0
    }
    
    suspend fun getMaxStreak(userId: Long): Int {
        return dailyStatsDao.getMaxStreak(userId) ?: 0
    }
    
    suspend fun getWeeklyAverageProductivity(userId: Long): Float {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val startDate = dateFormat.format(calendar.time)
        
        return dailyStatsDao.getAverageProductivityScore(userId, startDate) ?: 0f
    }
    
    suspend fun getTotalFocusTimeThisWeek(userId: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val startDate = dateFormat.format(calendar.time)
        
        return dailyStatsDao.getTotalFocusTimeInPeriod(userId, startDate) ?: 0
    }
    
    suspend fun getTotalTasksCompletedThisWeek(userId: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val startDate = dateFormat.format(calendar.time)
        
        return dailyStatsDao.getTotalTasksCompletedInPeriod(userId, startDate) ?: 0
    }
    
    suspend fun updateTodayStats(
        userId: Long,
        pomodoroCompleted: Boolean = false,
        focusTimeMinutes: Int = 0,
        taskCompleted: Boolean = false,
        taskCreated: Boolean = false
    ) {
        val today = dateFormat.format(Date())
        var stats = dailyStatsDao.getStatsForDate(userId, today)
        
        if (stats == null) {
            // Create new stats for today
            val previousDayStats = getYesterdayStats(userId)
            val newStreak = if (previousDayStats?.studyStreakDays ?: 0 > 0) {
                (previousDayStats?.studyStreakDays ?: 0) + 1
            } else {
                1
            }
            
            stats = DailyStats(
                userId = userId,
                date = today,
                studyStreakDays = newStreak
            )
        }
        
        // Update stats
        stats = stats.copy(
            pomodoroSessionsCompleted = if (pomodoroCompleted) stats.pomodoroSessionsCompleted + 1 else stats.pomodoroSessionsCompleted,
            totalFocusTimeMinutes = stats.totalFocusTimeMinutes + focusTimeMinutes,
            tasksCompleted = if (taskCompleted) stats.tasksCompleted + 1 else stats.tasksCompleted,
            tasksCreated = if (taskCreated) stats.tasksCreated + 1 else stats.tasksCreated,
            productivityScore = calculateProductivityScore(stats)
        )
        
        dailyStatsDao.insertOrUpdateStats(stats)
    }
    
    private suspend fun getYesterdayStats(userId: Long): DailyStats? {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = dateFormat.format(calendar.time)
        
        return dailyStatsDao.getStatsForDate(userId, yesterday)
    }
    
    private fun calculateProductivityScore(stats: DailyStats): Float {
        // Simple productivity score calculation (0-100)
        var score = 0f
        
        // Pomodoro sessions (max 40 points for 8 sessions)
        score += minOf(stats.pomodoroSessionsCompleted * 5f, 40f)
        
        // Focus time (max 30 points for 4+ hours)
        score += minOf(stats.totalFocusTimeMinutes / 8f, 30f)
        
        // Tasks completed (max 30 points for 6+ tasks)
        score += minOf(stats.tasksCompleted * 5f, 30f)
        
        return minOf(score, 100f)
    }
    
    suspend fun insertOrUpdateStats(stats: DailyStats) {
        dailyStatsDao.insertOrUpdateStats(stats)
    }
    
    suspend fun deleteAllStatsForUser(userId: Long) {
        dailyStatsDao.deleteAllStatsForUser(userId)
    }
    
    suspend fun getTotalStats(userId: Long): TotalUserStats {
        val allStats = dailyStatsDao.getAllStatsForUserSync(userId)
        
        return TotalUserStats(
            totalPomodoros = allStats.sumOf { it.pomodoroSessionsCompleted },
            totalTasks = allStats.sumOf { it.tasksCompleted },
            totalFocusTime = allStats.sumOf { it.totalFocusTimeMinutes },
            currentStreak = getCurrentStreak(userId),
            maxStreak = getMaxStreakCalculated(userId)
        )
    }
    
    private suspend fun getMaxStreakCalculated(userId: Long): Int {
        val allStats = dailyStatsDao.getAllStatsForUserSync(userId)
            .sortedBy { it.date }
        
        var maxStreak = 0
        var currentStreak = 0
        
        for (i in allStats.indices) {
            if (allStats[i].pomodoroSessionsCompleted > 0) {
                currentStreak++
                maxStreak = maxOf(maxStreak, currentStreak)
            } else {
                currentStreak = 0
            }
        }
        
        return maxStreak
    }
}

data class TotalUserStats(
    val totalPomodoros: Int,
    val totalTasks: Int,
    val totalFocusTime: Int,
    val currentStreak: Int,
    val maxStreak: Int
)