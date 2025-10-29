package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.focusup.data.database.FocusUpDatabase
import com.example.focusup.data.database.entities.DailyStats
import com.example.focusup.data.repository.DailyStatsRepository
import com.example.focusup.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class DashboardUiState(
    val isLoading: Boolean = false,
    val todayStats: DailyStats? = null,
    val weeklyStats: List<DailyStats> = emptyList(),
    val currentStreak: Int = 0,
    val maxStreak: Int = 0,
    val weeklyFocusTime: Int = 0,
    val weeklyTasksCompleted: Int = 0,
    val weeklyProductivityAverage: Float = 0f,
    val todayProductivityScore: Float = 0f,
    val weeklyGoal: Int = 300, // Default weekly goal in minutes
    val weeklyProgress: Float = 0f,
    val errorMessage: String? = null
)

class DashboardViewModel(
    private val userId: Long,
    private val dailyStatsRepository: DailyStatsRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // Load today's stats
                val todayStats = dailyStatsRepository.getStatsForToday(userId)
                
                // Load weekly stats
                val weeklyStats = dailyStatsRepository.getStatsForWeek(userId)
                
                // Load streak information
                val currentStreak = dailyStatsRepository.getCurrentStreak(userId)
                val maxStreak = dailyStatsRepository.getMaxStreak(userId)
                
                // Load weekly aggregated data
                val weeklyFocusTime = dailyStatsRepository.getTotalFocusTimeThisWeek(userId)
                val weeklyTasksCompleted = dailyStatsRepository.getTotalTasksCompletedThisWeek(userId)
                val weeklyProductivityAverage = dailyStatsRepository.getWeeklyAverageProductivity(userId)
                
                // Calculate progress towards weekly goal
                val weeklyGoal = _uiState.value.weeklyGoal
                val weeklyProgress = if (weeklyGoal > 0) {
                    (weeklyFocusTime.toFloat() / weeklyGoal.toFloat()) * 100f
                } else 0f
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    todayStats = todayStats,
                    weeklyStats = weeklyStats,
                    currentStreak = currentStreak,
                    maxStreak = maxStreak,
                    weeklyFocusTime = weeklyFocusTime,
                    weeklyTasksCompleted = weeklyTasksCompleted,
                    weeklyProductivityAverage = weeklyProductivityAverage,
                    todayProductivityScore = todayStats?.productivityScore ?: 0f,
                    weeklyProgress = minOf(weeklyProgress, 100f)
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error loading dashboard data: ${e.message}"
                )
            }
        }
    }
    
    fun updateTodayStats(
        pomodoroCompleted: Boolean = false,
        focusTimeMinutes: Int = 0,
        taskCompleted: Boolean = false,
        taskCreated: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                dailyStatsRepository.updateTodayStats(
                    userId = userId,
                    pomodoroCompleted = pomodoroCompleted,
                    focusTimeMinutes = focusTimeMinutes,
                    taskCompleted = taskCompleted,
                    taskCreated = taskCreated
                )
                
                // Reload dashboard data
                loadDashboardData()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error updating stats: ${e.message}"
                )
            }
        }
    }
    
    fun setWeeklyGoal(goalMinutes: Int) {
        _uiState.value = _uiState.value.copy(weeklyGoal = goalMinutes)
        
        // Recalculate progress
        val weeklyProgress = if (goalMinutes > 0) {
            (_uiState.value.weeklyFocusTime.toFloat() / goalMinutes.toFloat()) * 100f
        } else 0f
        
        _uiState.value = _uiState.value.copy(weeklyProgress = minOf(weeklyProgress, 100f))
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun getFormattedFocusTime(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        
        return when {
            hours > 0 -> "${hours}h ${mins}m"
            else -> "${mins}m"
        }
    }
    
    fun getStreakMessage(streak: Int): String {
        return when {
            streak == 0 -> "Empieza tu racha hoy 🚀"
            streak == 1 -> "¡Primer día! Sigue así 💪"
            streak < 7 -> "¡$streak días seguidos! 🔥"
            streak < 30 -> "¡Increíble! $streak días 🏆"
            else -> "¡Leyenda! $streak días 👑"
        }
    }
    
    fun getProductivityLevel(score: Float): String {
        return when {
            score >= 90f -> "Excepcional 🌟"
            score >= 80f -> "Excelente 🚀"
            score >= 70f -> "Muy bueno 💪"
            score >= 60f -> "Bueno 👍"
            score >= 40f -> "Regular 📈"
            else -> "Puedes mejorar 💡"
        }
    }
    
    companion object {
        fun Factory(userId: Long): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val database = FocusUpDatabase.getDatabase(application)
                val dailyStatsRepository = DailyStatsRepository(database.dailyStatsDao())
                val taskRepository = TaskRepository(database.taskDao())
                
                return DashboardViewModel(userId, dailyStatsRepository, taskRepository) as T
            }
        }
    }
}