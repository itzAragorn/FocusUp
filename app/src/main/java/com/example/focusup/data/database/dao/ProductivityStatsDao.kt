package com.example.focusup.data.database.dao

import androidx.room.*
import com.example.focusup.data.database.entities.ProductivityStats
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductivityStatsDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stats: ProductivityStats): Long
    
    @Update
    suspend fun update(stats: ProductivityStats)
    
    @Delete
    suspend fun delete(stats: ProductivityStats)
    
    @Query("SELECT * FROM productivity_stats WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getStatsForDate(userId: Long, date: String): ProductivityStats?
    
    @Query("SELECT * FROM productivity_stats WHERE userId = :userId AND date = :date LIMIT 1")
    fun getStatsForDateFlow(userId: Long, date: String): Flow<ProductivityStats?>
    
    @Query("SELECT * FROM productivity_stats WHERE userId = :userId ORDER BY date DESC LIMIT :limit")
    fun getRecentStats(userId: Long, limit: Int = 30): Flow<List<ProductivityStats>>
    
    @Query("SELECT * FROM productivity_stats WHERE userId = :userId ORDER BY date DESC")
    fun getAllStats(userId: Long): Flow<List<ProductivityStats>>
    
    @Query("SELECT SUM(tasksCompleted) FROM productivity_stats WHERE userId = :userId")
    fun getTotalTasksCompleted(userId: Long): Flow<Int?>
    
    @Query("SELECT SUM(pomodorosCompleted) FROM productivity_stats WHERE userId = :userId")
    fun getTotalPomodoros(userId: Long): Flow<Int?>
    
    @Query("SELECT SUM(studyTimeMinutes) FROM productivity_stats WHERE userId = :userId")
    fun getTotalStudyTime(userId: Long): Flow<Int?>
    
    @Query("SELECT MAX(streak) FROM productivity_stats WHERE userId = :userId")
    fun getLongestStreak(userId: Long): Flow<Int?>
    
    @Query("SELECT * FROM productivity_stats WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    fun getCurrentStreak(userId: Long): Flow<ProductivityStats?>
    
    // Estadísticas de la semana actual
    @Query("""
        SELECT * FROM productivity_stats 
        WHERE userId = :userId 
        AND date >= :startDate 
        AND date <= :endDate
        ORDER BY date ASC
    """)
    fun getWeeklyStats(userId: Long, startDate: String, endDate: String): Flow<List<ProductivityStats>>
    
    // Estadísticas del mes actual
    @Query("""
        SELECT * FROM productivity_stats 
        WHERE userId = :userId 
        AND date >= :startDate 
        AND date <= :endDate
        ORDER BY date ASC
    """)
    fun getMonthlyStats(userId: Long, startDate: String, endDate: String): Flow<List<ProductivityStats>>
}
