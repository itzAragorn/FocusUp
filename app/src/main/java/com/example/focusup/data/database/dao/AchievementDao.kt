package com.example.focusup.data.database.dao

import androidx.room.*
import com.example.focusup.data.database.entities.Achievement
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    
    @Query("SELECT * FROM achievements WHERE userId = :userId ORDER BY unlockedAt DESC, rarity ASC")
    fun getAchievementsForUser(userId: Long): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE userId = :userId AND isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(userId: Long): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE userId = :userId AND isUnlocked = 0 ORDER BY targetValue ASC")
    fun getLockedAchievements(userId: Long): Flow<List<Achievement>>
    
    @Query("SELECT * FROM achievements WHERE userId = :userId AND achievementType = :type")
    suspend fun getAchievementByType(userId: Long, type: String): Achievement?
    
    @Query("SELECT COUNT(*) FROM achievements WHERE userId = :userId AND isUnlocked = 1")
    suspend fun getUnlockedCount(userId: Long): Int
    
    @Query("SELECT * FROM achievements WHERE userId = :userId AND isUnlocked = 1 AND unlockedAt > :since ORDER BY unlockedAt DESC")
    fun getRecentlyUnlocked(userId: Long, since: Long): Flow<List<Achievement>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)
    
    @Update
    suspend fun updateAchievement(achievement: Achievement)
    
    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :achievementId")
    suspend fun unlockAchievement(achievementId: Long, unlockedAt: Long)
    
    @Query("UPDATE achievements SET currentProgress = :progress WHERE userId = :userId AND achievementType = :type")
    suspend fun updateProgress(userId: Long, type: String, progress: Int)
    
    @Query("DELETE FROM achievements WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Long)
    
    // Queries para estadísticas
    @Query("SELECT COUNT(*) FROM achievements WHERE userId = :userId AND category = :category AND isUnlocked = 1")
    suspend fun getUnlockedCountByCategory(userId: Long, category: String): Int
    
    @Query("SELECT COUNT(*) FROM achievements WHERE userId = :userId AND rarity = :rarity AND isUnlocked = 1")
    suspend fun getUnlockedCountByRarity(userId: Long, rarity: String): Int
}