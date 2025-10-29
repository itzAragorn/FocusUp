package com.example.focusup.data.repository

import com.example.focusup.data.database.dao.UserProgressDao
import com.example.focusup.data.database.entities.LevelSystem
import com.example.focusup.data.database.entities.UserProgress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class UserProgressRepository(
    private val userProgressDao: UserProgressDao
) {
    
    fun getUserProgress(userId: Long): Flow<UserProgress?> {
        return userProgressDao.getUserProgress(userId)
    }
    
    suspend fun getUserProgressSync(userId: Long): UserProgress? {
        return userProgressDao.getUserProgressSync(userId)
    }
    
    suspend fun initializeUserProgress(userId: Long) {
        val existingProgress = userProgressDao.getUserProgressSync(userId)
        if (existingProgress == null) {
            val initialProgress = UserProgress(
                userId = userId,
                totalXp = 0,
                currentLevel = 1,
                xpToNextLevel = LevelSystem.getXpRequiredForLevel(1),
                totalAchievements = 0,
                lastActiveDate = getCurrentDate(),
                title = LevelSystem.getTitleForLevel(1)
            )
            userProgressDao.insertUserProgress(initialProgress)
        }
    }
    
    suspend fun addXp(userId: Long, xp: Int): LevelUpResult {
        val currentProgress = userProgressDao.getUserProgressSync(userId) ?: return LevelUpResult()
        
        val newTotalXp = currentProgress.totalXp + xp
        val newLevel = LevelSystem.getLevelFromXp(newTotalXp)
        val xpToNext = LevelSystem.getXpToNextLevel(newTotalXp)
        val newTitle = LevelSystem.getTitleForLevel(newLevel)
        
        val leveledUp = newLevel > currentProgress.currentLevel
        
        val updatedProgress = currentProgress.copy(
            totalXp = newTotalXp,
            currentLevel = newLevel,
            xpToNextLevel = xpToNext,
            title = newTitle,
            updatedAt = System.currentTimeMillis()
        )
        
        userProgressDao.updateUserProgress(updatedProgress)
        
        return LevelUpResult(
            leveledUp = leveledUp,
            newLevel = newLevel,
            previousLevel = currentProgress.currentLevel,
            xpGained = xp,
            newTitle = if (leveledUp && newTitle != currentProgress.title) newTitle else null
        )
    }
    
    suspend fun updateAchievementCount(userId: Long, count: Int) {
        userProgressDao.updateAchievementCount(userId, count)
    }
    
    suspend fun updateLastActiveDate(userId: Long) {
        val today = getCurrentDate()
        userProgressDao.updateLastActiveDate(userId, today)
    }
    
    fun getUserProgressWithCalculations(userId: Long): Flow<UserProgressDisplay?> {
        return userProgressDao.getUserProgress(userId).map { progress ->
            progress?.let {
                val levelProgress = calculateLevelProgress(it.totalXp, it.currentLevel)
                val xpForCurrentLevel = getLevelStartXp(it.currentLevel)
                val xpForNextLevel = getLevelStartXp(it.currentLevel + 1)
                
                UserProgressDisplay(
                    userId = it.userId,
                    totalXp = it.totalXp,
                    currentLevel = it.currentLevel,
                    xpToNextLevel = it.xpToNextLevel,
                    totalAchievements = it.totalAchievements,
                    title = it.title,
                    levelProgressPercentage = levelProgress,
                    xpInCurrentLevel = it.totalXp - getLevelStartXp(it.currentLevel),
                    xpRequiredForCurrentLevel = LevelSystem.getXpRequiredForLevel(it.currentLevel),
                    lastActiveDate = it.lastActiveDate,
                    badges = it.badges,
                    currentXp = it.totalXp,
                    xpForCurrentLevel = xpForCurrentLevel,
                    xpForNextLevel = xpForNextLevel
                )
            }
        }
    }
    
    private fun calculateLevelProgress(totalXp: Int, currentLevel: Int): Int {
        val levelStartXp = getLevelStartXp(currentLevel)
        val xpInCurrentLevel = totalXp - levelStartXp
        val xpRequiredForLevel = LevelSystem.getXpRequiredForLevel(currentLevel)
        
        return if (xpRequiredForLevel > 0) {
            ((xpInCurrentLevel.toFloat() / xpRequiredForLevel) * 100).toInt().coerceIn(0, 100)
        } else {
            100
        }
    }
    
    private fun getLevelStartXp(level: Int): Int {
        return (1 until level).sumOf { LevelSystem.getXpRequiredForLevel(it) }
    }
    
    suspend fun getTopUsersByXp(limit: Int = 10): List<UserProgress> {
        return userProgressDao.getTopUsersByXp(limit)
    }
    
    suspend fun addBadge(userId: Long, badgeId: String) {
        val progress = userProgressDao.getUserProgressSync(userId)
        if (progress != null && !progress.badges.contains(badgeId)) {
            val updatedBadges = progress.badges + badgeId
            val updatedProgress = progress.copy(
                badges = updatedBadges,
                updatedAt = System.currentTimeMillis()
            )
            userProgressDao.updateUserProgress(updatedProgress)
        }
    }
    
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}

data class LevelUpResult(
    val leveledUp: Boolean = false,
    val newLevel: Int = 1,
    val previousLevel: Int = 1,
    val xpGained: Int = 0,
    val newTitle: String? = null
)

data class UserProgressDisplay(
    val userId: Long,
    val totalXp: Int,
    val currentLevel: Int,
    val xpToNextLevel: Int,
    val totalAchievements: Int,
    val title: String,
    val levelProgressPercentage: Int,
    val xpInCurrentLevel: Int,
    val xpRequiredForCurrentLevel: Int,
    val lastActiveDate: String?,
    val badges: List<String>,
    // Propiedades adicionales para XP progress bar
    val currentXp: Int,
    val xpForCurrentLevel: Int,
    val xpForNextLevel: Int
)