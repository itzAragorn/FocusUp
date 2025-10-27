package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusup.data.database.entities.ProductivityStats
import com.example.focusup.data.repository.ProductivityStatsRepository
import com.example.focusup.utils.DateTimeUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StatsUiState(
    val todayStats: ProductivityStats? = null,
    val weeklyStats: List<ProductivityStats> = emptyList(),
    val monthlyStats: List<ProductivityStats> = emptyList(),
    val totalTasksCompleted: Int = 0,
    val totalPomodoros: Int = 0,
    val totalStudyTimeMinutes: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class StatsViewModel(
    private val statsRepository: ProductivityStatsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()
    
    fun loadStats(userId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val today = DateTimeUtils.getCurrentDate()
                val weekStart = DateTimeUtils.getStartOfWeek()
                val weekEnd = DateTimeUtils.getEndOfWeek()
                val monthStart = DateTimeUtils.getStartOfMonth()
                val monthEnd = DateTimeUtils.getEndOfMonth()
                
                // Combinar todos los flows
                combine(
                    statsRepository.getStatsForDate(userId, today),
                    statsRepository.getWeeklyStats(userId, weekStart, weekEnd),
                    statsRepository.getMonthlyStats(userId, monthStart, monthEnd),
                    statsRepository.getTotalTasksCompleted(userId),
                    statsRepository.getTotalPomodoros(userId),
                    statsRepository.getTotalStudyTime(userId),
                    statsRepository.getLongestStreak(userId)
                ) { flows ->
                    val todayStats = flows[0] as? ProductivityStats
                    val weeklyStats = flows[1] as? List<ProductivityStats> ?: emptyList()
                    val monthlyStats = flows[2] as? List<ProductivityStats> ?: emptyList()
                    val totalTasks = flows[3] as? Int ?: 0
                    val totalPomodoros = flows[4] as? Int ?: 0
                    val totalStudyTime = flows[5] as? Int ?: 0
                    val longestStreak = flows[6] as? Int ?: 0
                    
                    // Calcular racha actual
                    val currentStreak = todayStats?.streak ?: 0
                    
                    StatsUiState(
                        todayStats = todayStats,
                        weeklyStats = weeklyStats,
                        monthlyStats = monthlyStats,
                        totalTasksCompleted = totalTasks,
                        totalPomodoros = totalPomodoros,
                        totalStudyTimeMinutes = totalStudyTime,
                        currentStreak = currentStreak,
                        longestStreak = longestStreak,
                        isLoading = false
                    )
                }.collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al cargar estadísticas"
                    )
                }
            }
        }
    }
    
    fun getCompletionRate(): Float {
        val state = _uiState.value
        val today = state.todayStats ?: return 0f
        val total = today.tasksCreated
        val completed = today.tasksCompleted
        return if (total > 0) (completed.toFloat() / total.toFloat()) * 100f else 0f
    }
    
    fun getWeeklyAverage(): Float {
        val weeklyStats = _uiState.value.weeklyStats
        if (weeklyStats.isEmpty()) return 0f
        val totalCompleted = weeklyStats.sumOf { it.tasksCompleted }
        return totalCompleted.toFloat() / weeklyStats.size.toFloat()
    }
    
    fun getTotalStudyTimeFormatted(): String {
        val totalMinutes = _uiState.value.totalStudyTimeMinutes
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (hours > 0) {
            "${hours}h ${minutes}m"
        } else {
            "${minutes}m"
        }
    }
}
