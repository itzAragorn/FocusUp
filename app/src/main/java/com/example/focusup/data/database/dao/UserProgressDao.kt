package com.example.focusup.data.database.dao

import androidx.room.*
import com.example.focusup.data.database.entities.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
    
    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    fun getUserProgress(userId: Long): Flow<UserProgress?>
    
    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    suspend fun getUserProgressSync(userId: Long): UserProgress?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(userProgress: UserProgress)
    
    @Update
    suspend fun updateUserProgress(userProgress: UserProgress)
    
    @Query("UPDATE user_progress SET totalXp = totalXp + :xp WHERE userId = :userId")
    suspend fun addXp(userId: Long, xp: Int)
    
    @Query("UPDATE user_progress SET totalAchievements = :count WHERE userId = :userId")
    suspend fun updateAchievementCount(userId: Long, count: Int)
    
    @Query("UPDATE user_progress SET lastActiveDate = :date WHERE userId = :userId")
    suspend fun updateLastActiveDate(userId: Long, date: String)
    
    @Query("DELETE FROM user_progress WHERE userId = :userId")
    suspend fun deleteUserProgress(userId: Long)
    
    // Query para obtener el ranking de usuarios por XP (opcional para futuras features)
    @Query("SELECT * FROM user_progress ORDER BY totalXp DESC LIMIT :limit")
    suspend fun getTopUsersByXp(limit: Int = 10): List<UserProgress>
}