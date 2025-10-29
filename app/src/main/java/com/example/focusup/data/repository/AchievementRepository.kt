package com.example.focusup.data.repository

import com.example.focusup.data.database.dao.AchievementDao
import com.example.focusup.data.database.entities.Achievement
import com.example.focusup.data.database.entities.AchievementType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class AchievementRepository(
    private val achievementDao: AchievementDao
) {
    
    fun getAchievementsForUser(userId: Long): Flow<List<Achievement>> {
        return achievementDao.getAchievementsForUser(userId)
    }
    
    fun getUnlockedAchievements(userId: Long): Flow<List<Achievement>> {
        return achievementDao.getUnlockedAchievements(userId)
    }
    
    fun getLockedAchievements(userId: Long): Flow<List<Achievement>> {
        return achievementDao.getLockedAchievements(userId)
    }
    
    fun getRecentlyUnlocked(userId: Long, hoursAgo: Int = 24): Flow<List<Achievement>> {
        val since = System.currentTimeMillis() - (hoursAgo * 60 * 60 * 1000)
        return achievementDao.getRecentlyUnlocked(userId, since)
    }
    
    suspend fun initializeAchievementsForUser(userId: Long) {
        // Crear todos los achievements para el usuario si no existen
        val existingAchievements = achievementDao.getAchievementsForUser(userId).first()
        
        if (existingAchievements.isEmpty()) {
            val achievements = AchievementType.values().map { type ->
                Achievement(
                    userId = userId,
                    achievementType = type.id,
                    title = type.title,
                    description = type.description,
                    badgeIcon = type.badgeIcon,
                    xpReward = type.xpReward,
                    targetValue = type.targetValue,
                    category = type.category,
                    rarity = type.rarity
                )
            }
            achievementDao.insertAchievements(achievements)
        }
    }
    
    suspend fun checkAndUnlockAchievements(
        userId: Long,
        totalPomodoros: Int,
        totalTasks: Int,
        totalFocusTimeMinutes: Int,
        currentStreak: Int,
        dailyPomodoros: Int,
        dailyTasks: Int
    ): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()
        
        // Verificar achievements de pomodoros
        checkPomodoroAchievements(userId, totalPomodoros, newlyUnlocked)
        
        // Verificar achievements de tareas
        checkTaskAchievements(userId, totalTasks, newlyUnlocked)
        
        // Verificar achievements de tiempo
        checkTimeAchievements(userId, totalFocusTimeMinutes, newlyUnlocked)
        
        // Verificar achievements de streaks
        checkStreakAchievements(userId, currentStreak, newlyUnlocked)
        
        // Verificar achievements diarios
        checkDailyAchievements(userId, dailyPomodoros, dailyTasks, newlyUnlocked)
        
        return newlyUnlocked
    }
    
    private suspend fun checkPomodoroAchievements(
        userId: Long,
        totalPomodoros: Int,
        newlyUnlocked: MutableList<Achievement>
    ) {
        val pomodoroAchievements = listOf(
            AchievementType.FIRST_POMODORO to 1,
            AchievementType.POMODORO_10 to 10,
            AchievementType.POMODORO_50 to 50,
            AchievementType.POMODORO_100 to 100,
            AchievementType.POMODORO_500 to 500
        )
        
        pomodoroAchievements.forEach { (type, threshold) ->
            if (totalPomodoros >= threshold) {
                unlockAchievementIfNotUnlocked(userId, type.id, newlyUnlocked)
            } else {
                updateAchievementProgress(userId, type.id, totalPomodoros)
            }
        }
    }
    
    private suspend fun checkTaskAchievements(
        userId: Long,
        totalTasks: Int,
        newlyUnlocked: MutableList<Achievement>
    ) {
        val taskAchievements = listOf(
            AchievementType.FIRST_TASK to 1,
            AchievementType.TASK_MASTER_25 to 25,
            AchievementType.TASK_MASTER_100 to 100,
            AchievementType.TASK_MASTER_500 to 500
        )
        
        taskAchievements.forEach { (type, threshold) ->
            if (totalTasks >= threshold) {
                unlockAchievementIfNotUnlocked(userId, type.id, newlyUnlocked)
            } else {
                updateAchievementProgress(userId, type.id, totalTasks)
            }
        }
    }
    
    private suspend fun checkTimeAchievements(
        userId: Long,
        totalMinutes: Int,
        newlyUnlocked: MutableList<Achievement>
    ) {
        val timeAchievements = listOf(
            AchievementType.TIME_WARRIOR_10H to 600,  // 10 horas
            AchievementType.TIME_WARRIOR_50H to 3000, // 50 horas
            AchievementType.TIME_WARRIOR_100H to 6000 // 100 horas
        )
        
        timeAchievements.forEach { (type, threshold) ->
            if (totalMinutes >= threshold) {
                unlockAchievementIfNotUnlocked(userId, type.id, newlyUnlocked)
            } else {
                updateAchievementProgress(userId, type.id, totalMinutes)
            }
        }
    }
    
    private suspend fun checkStreakAchievements(
        userId: Long,
        currentStreak: Int,
        newlyUnlocked: MutableList<Achievement>
    ) {
        val streakAchievements = listOf(
            AchievementType.STREAK_3 to 3,
            AchievementType.STREAK_7 to 7,
            AchievementType.STREAK_30 to 30,
            AchievementType.STREAK_100 to 100
        )
        
        streakAchievements.forEach { (type, threshold) ->
            if (currentStreak >= threshold) {
                unlockAchievementIfNotUnlocked(userId, type.id, newlyUnlocked)
            } else {
                updateAchievementProgress(userId, type.id, currentStreak)
            }
        }
    }
    
    private suspend fun checkDailyAchievements(
        userId: Long,
        dailyPomodoros: Int,
        dailyTasks: Int,
        newlyUnlocked: MutableList<Achievement>
    ) {
        // First Day achievement (3+ pomodoros en un día)
        if (dailyPomodoros >= 3) {
            unlockAchievementIfNotUnlocked(userId, AchievementType.FIRST_DAY.id, newlyUnlocked)
        }
        
        // Speed Demon achievement (10+ tareas en un día)
        if (dailyTasks >= 10) {
            unlockAchievementIfNotUnlocked(userId, AchievementType.SPEED_DEMON.id, newlyUnlocked)
        }
        
        // Early Bird y Night Owl se verificarán en el PomodoroViewModel cuando se complete una sesión
    }
    
    private suspend fun unlockAchievementIfNotUnlocked(
        userId: Long,
        achievementType: String,
        newlyUnlocked: MutableList<Achievement>
    ) {
        val achievement = achievementDao.getAchievementByType(userId, achievementType)
        if (achievement != null && !achievement.isUnlocked) {
            val now = System.currentTimeMillis()
            achievementDao.unlockAchievement(achievement.id, now)
            
            // Añadir a la lista de nuevos desbloqueados
            newlyUnlocked.add(achievement.copy(isUnlocked = true, unlockedAt = now))
        }
    }
    
    private suspend fun updateAchievementProgress(
        userId: Long,
        achievementType: String,
        progress: Int
    ) {
        achievementDao.updateProgress(userId, achievementType, progress)
    }
    
    suspend fun unlockSpecialAchievement(userId: Long, achievementType: AchievementType) {
        val achievement = achievementDao.getAchievementByType(userId, achievementType.id)
        if (achievement != null && !achievement.isUnlocked) {
            achievementDao.unlockAchievement(achievement.id, System.currentTimeMillis())
        }
    }
    
    suspend fun getUnlockedCount(userId: Long): Int {
        return achievementDao.getUnlockedCount(userId)
    }
    
    suspend fun getAchievementStats(userId: Long): AchievementStats {
        val total = AchievementType.values().size
        val unlocked = achievementDao.getUnlockedCount(userId)
        val productivity = achievementDao.getUnlockedCountByCategory(userId, "PRODUCTIVITY")
        val consistency = achievementDao.getUnlockedCountByCategory(userId, "CONSISTENCY")
        val milestone = achievementDao.getUnlockedCountByCategory(userId, "MILESTONE")
        
        return AchievementStats(
            totalAchievements = total,
            unlockedAchievements = unlocked,
            productivityAchievements = productivity,
            consistencyAchievements = consistency,
            milestoneAchievements = milestone,
            completionPercentage = (unlocked.toFloat() / total * 100).toInt()
        )
    }
}

data class AchievementStats(
    val totalAchievements: Int,
    val unlockedAchievements: Int,
    val productivityAchievements: Int,
    val consistencyAchievements: Int,
    val milestoneAchievements: Int,
    val completionPercentage: Int
)