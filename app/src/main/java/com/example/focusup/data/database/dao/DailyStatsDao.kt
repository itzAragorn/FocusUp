package com.example.focusup.data.database.dao

import androidx.room.*
import com.example.focusup.data.database.entities.DailyStats
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {
    
    @Query("SELECT * FROM daily_stats WHERE userId = :userId ORDER BY date DESC")
    fun getAllStatsForUser(userId: Long): Flow<List<DailyStats>>
    
    @Query("SELECT * FROM daily_stats WHERE userId = :userId ORDER BY date DESC")
    suspend fun getAllStatsForUserSync(userId: Long): List<DailyStats>
    
    @Query("SELECT * FROM daily_stats WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getStatsForDate(userId: Long, date: String): DailyStats?
    
    @Query("SELECT * FROM daily_stats WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    suspend fun getRecentStats(userId: Long, limit: Int): List<DailyStats>
    
    @Query("SELECT * FROM daily_stats WHERE userId = :userId AND date >= :startDate AND date <= :endDate ORDER BY date ASC")
    suspend fun getStatsForDateRange(userId: Long, startDate: String, endDate: String): List<DailyStats>
    
    @Query("SELECT MAX(studyStreakDays) FROM daily_stats WHERE userId = :userId")
    suspend fun getMaxStreak(userId: Long): Int?
    
    @Query("SELECT AVG(productivityScore) FROM daily_stats WHERE userId = :userId AND date >= :startDate")
    suspend fun getAverageProductivityScore(userId: Long, startDate: String): Float?
    
    @Query("SELECT SUM(totalFocusTimeMinutes) FROM daily_stats WHERE userId = :userId AND date >= :startDate")
    suspend fun getTotalFocusTimeInPeriod(userId: Long, startDate: String): Int?
    
    @Query("SELECT SUM(tasksCompleted) FROM daily_stats WHERE userId = :userId AND date >= :startDate")
    suspend fun getTotalTasksCompletedInPeriod(userId: Long, startDate: String): Int?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStats(stats: DailyStats)
    
    @Update
    suspend fun updateStats(stats: DailyStats)
    
    @Delete
    suspend fun deleteStats(stats: DailyStats)
    
    @Query("DELETE FROM daily_stats WHERE userId = :userId")
    suspend fun deleteAllStatsForUser(userId: Long)
}