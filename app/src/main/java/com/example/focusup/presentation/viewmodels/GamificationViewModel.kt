package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.focusup.data.database.entities.Achievement
import com.example.focusup.data.database.entities.AchievementType
import com.example.focusup.data.repository.AchievementRepository
import com.example.focusup.data.repository.AchievementStats
import com.example.focusup.data.repository.LevelUpResult
import com.example.focusup.data.repository.UserProgressDisplay
import com.example.focusup.data.repository.UserProgressRepository
import com.example.focusup.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class GamificationUiState(
    val isLoading: Boolean = false,
    val userProgress: UserProgressDisplay? = null,
    val achievements: List<Achievement> = emptyList(),
    val unlockedAchievements: List<Achievement> = emptyList(),
    val lockedAchievements: List<Achievement> = emptyList(),
    val recentAchievements: List<Achievement> = emptyList(),
    val achievementStats: AchievementStats? = null,
    val pendingLevelUp: LevelUpResult? = null,
    val pendingAchievementUnlocks: List<Achievement> = emptyList(),
    val showLevelUpDialog: Boolean = false,
    val showAchievementDialog: Boolean = false,
    val errorMessage: String? = null
)

class GamificationViewModel(
    private val userId: Long,
    private val achievementRepository: AchievementRepository,
    private val userProgressRepository: UserProgressRepository,
    private val notificationHelper: NotificationHelper? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamificationUiState())
    val uiState: StateFlow<GamificationUiState> = _uiState.asStateFlow()

    init {
        initializeGamification()
        loadGamificationData()
    }

    private fun initializeGamification() {
        viewModelScope.launch {
            try {
                // Inicializar progreso del usuario
                userProgressRepository.initializeUserProgress(userId)
                
                // Inicializar achievements
                achievementRepository.initializeAchievementsForUser(userId)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error inicializando gamificación: ${e.message}"
                )
            }
        }
    }

    private fun loadGamificationData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Combinar flows de progreso y achievements
                combine(
                    userProgressRepository.getUserProgressWithCalculations(userId),
                    achievementRepository.getAchievementsForUser(userId),
                    achievementRepository.getUnlockedAchievements(userId),
                    achievementRepository.getLockedAchievements(userId),
                    achievementRepository.getRecentlyUnlocked(userId, 24)
                ) { progress, allAchievements, unlocked, locked, recent ->
                    
                    val stats = achievementRepository.getAchievementStats(userId)
                    
                    GamificationUiState(
                        isLoading = false,
                        userProgress = progress,
                        achievements = allAchievements,
                        unlockedAchievements = unlocked,
                        lockedAchievements = locked,
                        recentAchievements = recent,
                        achievementStats = stats
                    )
                }.collect { newState ->
                    _uiState.value = newState.copy(
                        pendingLevelUp = _uiState.value.pendingLevelUp,
                        pendingAchievementUnlocks = _uiState.value.pendingAchievementUnlocks,
                        showLevelUpDialog = _uiState.value.showLevelUpDialog,
                        showAchievementDialog = _uiState.value.showAchievementDialog
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error cargando datos: ${e.message}"
                )
            }
        }
    }

    // Función llamada cuando el usuario completa actividades (pomodoros, tareas, etc.)
    suspend fun onActivityCompleted(
        xpToAdd: Int,
        totalPomodoros: Int,
        totalTasks: Int,
        totalFocusTimeMinutes: Int,
        currentStreak: Int,
        dailyPomodoros: Int,
        dailyTasks: Int
    ) {
        viewModelScope.launch {
            try {
                // Añadir XP y verificar level up
                val levelUpResult = userProgressRepository.addXp(userId, xpToAdd)
                
                // Verificar achievements desbloqueados
                val newAchievements = achievementRepository.checkAndUnlockAchievements(
                    userId = userId,
                    totalPomodoros = totalPomodoros,
                    totalTasks = totalTasks,
                    totalFocusTimeMinutes = totalFocusTimeMinutes,
                    currentStreak = currentStreak,
                    dailyPomodoros = dailyPomodoros,
                    dailyTasks = dailyTasks
                )
                
                // Actualizar contador de achievements si hay nuevos
                if (newAchievements.isNotEmpty()) {
                    val newCount = achievementRepository.getUnlockedCount(userId)
                    userProgressRepository.updateAchievementCount(userId, newCount)
                }
                
                // Actualizar fecha de última actividad
                userProgressRepository.updateLastActiveDate(userId)
                
                // Mostrar notificaciones si hay level up o nuevos achievements
                if (levelUpResult.leveledUp) {
                    _uiState.value = _uiState.value.copy(
                        pendingLevelUp = levelUpResult,
                        showLevelUpDialog = true
                    )
                    
                    // Mostrar notificación de level up
                    notificationHelper?.showLevelUpNotification(
                        newLevel = levelUpResult.newLevel,
                        xpGained = levelUpResult.xpGained
                    )
                }
                
                if (newAchievements.isNotEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        pendingAchievementUnlocks = newAchievements,
                        showAchievementDialog = true
                    )
                    
                    // Mostrar notificación del primer achievement desbloqueado
                    newAchievements.firstOrNull()?.let { achievement ->
                        notificationHelper?.showAchievementUnlockedNotification(
                            achievementName = achievement.title,
                            xpReward = achievement.xpReward
                        )
                    }
                } else if (xpToAdd > 0) {
                    // Solo mostrar XP ganado si no hay level up o achievements
                    val activityName = when {
                        dailyPomodoros > 0 -> "completar pomodoro"
                        dailyTasks > 0 -> "completar tarea"
                        else -> "actividad"
                    }
                    
                    notificationHelper?.showXpGainedNotification(
                        xpGained = xpToAdd,
                        activity = activityName
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error actualizando progreso: ${e.message}"
                )
            }
        }
    }

    // Función para desbloquear achievements especiales (como Early Bird, Night Owl, etc.)
    suspend fun unlockSpecialAchievement(achievementType: AchievementType) {
        viewModelScope.launch {
            try {
                achievementRepository.unlockSpecialAchievement(userId, achievementType)
                
                // Actualizar contador
                val newCount = achievementRepository.getUnlockedCount(userId)
                userProgressRepository.updateAchievementCount(userId, newCount)
                
                // Aquí podrías mostrar una notificación específica si quieres
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error desbloqueando achievement: ${e.message}"
                )
            }
        }
    }

    fun dismissLevelUpDialog() {
        _uiState.value = _uiState.value.copy(
            showLevelUpDialog = false,
            pendingLevelUp = null
        )
    }

    fun dismissAchievementDialog() {
        _uiState.value = _uiState.value.copy(
            showAchievementDialog = false,
            pendingAchievementUnlocks = emptyList()
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    // Factory para crear el ViewModel
    class Factory(
        private val userId: Long,
        private val achievementRepository: AchievementRepository,
        private val userProgressRepository: UserProgressRepository,
        private val notificationHelper: NotificationHelper? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GamificationViewModel::class.java)) {
                return GamificationViewModel(userId, achievementRepository, userProgressRepository, notificationHelper) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

// Utilidades para calcular XP basado en actividades
object XpCalculator {
    const val POMODORO_COMPLETED = 25
    const val TASK_COMPLETED = 15
    const val STREAK_BONUS_PER_DAY = 5
    const val PERFECT_DAY_BONUS = 50
    const val EARLY_BIRD_BONUS = 20
    const val NIGHT_OWL_BONUS = 20
    
    fun calculatePomodoroXp(streakDays: Int): Int {
        return POMODORO_COMPLETED + (streakDays * STREAK_BONUS_PER_DAY).coerceAtMost(50)
    }
    
    fun calculateTaskXp(priority: String, streakDays: Int): Int {
        val baseXp = when (priority.uppercase()) {
            "HIGH" -> 20
            "MEDIUM" -> 15
            "LOW" -> 10
            else -> 15
        }
        return baseXp + (streakDays * STREAK_BONUS_PER_DAY).coerceAtMost(30)
    }
}