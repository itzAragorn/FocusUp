package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusup.data.database.entities.Task
import com.example.focusup.data.repository.TaskRepository
import com.example.focusup.data.repository.DailyStatsRepository
import com.example.focusup.domain.manager.RecurrenceManager
import com.example.focusup.presentation.viewmodels.GamificationViewModel
import com.example.focusup.presentation.viewmodels.XpCalculator
import com.example.focusup.utils.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class TaskUiState(
    val isLoading: Boolean = false,
    val isTaskSaved: Boolean = false,
    val errorMessage: String? = null,
    val showRecurrenceDialog: Boolean = false,
    val selectedTask: Task? = null
)

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val dailyStatsRepository: DailyStatsRepository,
    private val userId: Long = 1L, // TODO: Get from auth
    private val gamificationViewModel: GamificationViewModel? = null
) : ViewModel() {
    
    private val recurrenceManager = RecurrenceManager(taskRepository)
    
    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()
    
    fun saveTask(
        userId: Long,
        name: String,
        description: String?,
        date: String,
        time: String,
        isNotificationEnabled: Boolean,
        priority: String = "NONE",
        tags: String? = null,
        attachments: String? = null,
        recurrenceType: String = "NONE",
        recurrenceEndDate: String? = null
    ) {
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "El nombre de la tarea es obligatorio")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val task = Task(
                    userId = userId,
                    name = name,
                    description = description,
                    date = date,
                    time = time,
                    isNotificationEnabled = isNotificationEnabled,
                    priority = priority,
                    tags = tags,
                    attachments = attachments,
                    recurrenceType = recurrenceType,
                    recurrenceEndDate = recurrenceEndDate
                )
                
                val taskId = taskRepository.insertTask(task)
                
                // Si la tarea tiene recurrencia, generar tareas hijas
                if (task.recurrenceType != "NONE") {
                    val savedTask = task.copy(id = taskId)
                    recurrenceManager.generateRecurringTasks(savedTask)
                }
                
                // *** GAMIFICACIÓN: Actualizar stats por crear tarea ***
                updateDailyStatsForTaskCreation()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isTaskSaved = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al guardar la tarea: ${e.message}"
                )
            }
        }
    }
    
    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val task = taskRepository.getTaskById(taskId)
                task?.let {
                    // Verificar si es parte de una serie recurrente
                    if (recurrenceManager.isRecurringTask(taskId)) {
                        // Guardar tarea para mostrar diálogo
                        _uiState.value = _uiState.value.copy(
                            selectedTask = it,
                            showRecurrenceDialog = true,
                            isLoading = false
                        )
                    } else {
                        // Eliminar solo esta tarea
                        taskRepository.deleteTask(it)
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al eliminar la tarea: ${e.message}"
                )
            }
        }
    }
    
    fun deleteTaskOnly(taskId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val task = taskRepository.getTaskById(taskId)
                task?.let {
                    taskRepository.deleteTask(it)
                }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showRecurrenceDialog = false,
                    selectedTask = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al eliminar la tarea: ${e.message}"
                )
            }
        }
    }
    
    fun deleteRecurringSeries(taskId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                recurrenceManager.deleteRecurringSeries(taskId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    showRecurrenceDialog = false,
                    selectedTask = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al eliminar la serie: ${e.message}"
                )
            }
        }
    }
    
    fun dismissRecurrenceDialog() {
        _uiState.value = _uiState.value.copy(
            showRecurrenceDialog = false,
            selectedTask = null
        )
    }
    
    fun toggleTaskCompletion(taskId: Long) {
        viewModelScope.launch {
            try {
                val task = taskRepository.getTaskById(taskId)
                task?.let {
                    val wasCompleted = it.isCompleted
                    val updatedTask = it.copy(isCompleted = !it.isCompleted)
                    taskRepository.updateTask(updatedTask)
                    
                    // Actualizar estadísticas diarias si la tarea se completó
                    if (!wasCompleted && updatedTask.isCompleted) {
                        updateDailyStatsForTaskCompletion(it.priority)
                    } else if (wasCompleted && !updatedTask.isCompleted) {
                        // Si se desmarcó una tarea completada, decrementar stats
                        updateDailyStatsForTaskUncompletion()
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al actualizar la tarea: ${e.message}"
                )
            }
        }
    }
    
    private suspend fun updateDailyStatsForTaskCompletion(priority: String) {
        try {
            dailyStatsRepository.updateTodayStats(
                userId = userId,
                pomodoroCompleted = false,
                focusTimeMinutes = 0,
                taskCompleted = true,
                taskCreated = false
            )
            
            // *** GAMIFICACIÓN: Actualizar progreso por completar tarea ***
            gamificationViewModel?.let { gamification ->
                val stats = dailyStatsRepository.getStatsForToday(userId)
                stats?.let { dailyStats ->
                    val totalStats = dailyStatsRepository.getTotalStats(userId)
                    
                    // Calcular XP basado en prioridad y streak
                    val streakDays = dailyStats.studyStreakDays
                    val xpToAdd = XpCalculator.calculateTaskXp(priority, streakDays)
                    
                    // Verificar si es un día perfecto (completó todas las tareas del día)
                    val totalTasksToday = taskRepository.getTasksForUserAndDate(userId, getCurrentDate()).size
                    val completedTasksToday = dailyStats.tasksCompleted + 1
                    
                    if (totalTasksToday > 0 && completedTasksToday >= totalTasksToday) {
                        // Bonus por día perfecto
                        gamification.onActivityCompleted(
                            xpToAdd = xpToAdd + XpCalculator.PERFECT_DAY_BONUS,
                            totalPomodoros = totalStats.totalPomodoros,
                            totalTasks = totalStats.totalTasks + 1,
                            totalFocusTimeMinutes = totalStats.totalFocusTime,
                            currentStreak = dailyStats.studyStreakDays,
                            dailyPomodoros = dailyStats.pomodoroSessionsCompleted,
                            dailyTasks = completedTasksToday
                        )
                    } else {
                        gamification.onActivityCompleted(
                            xpToAdd = xpToAdd,
                            totalPomodoros = totalStats.totalPomodoros,
                            totalTasks = totalStats.totalTasks + 1,
                            totalFocusTimeMinutes = totalStats.totalFocusTime,
                            currentStreak = dailyStats.studyStreakDays,
                            dailyPomodoros = dailyStats.pomodoroSessionsCompleted,
                            dailyTasks = completedTasksToday
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Log error but don't show to user as it's not critical
        }
    }
    
    private suspend fun updateDailyStatsForTaskUncompletion() {
        try {
            // Para desmarcar una tarea, necesitamos decrementar manualmente
            // Esto requiere una función especial en el repository
            decrementTaskStats()
        } catch (e: Exception) {
            // Log error but don't show to user as it's not critical
        }
    }
    
    private suspend fun decrementTaskStats() {
        // Obtener stats actuales y decrementar
        val today = getCurrentDate()
        val stats = dailyStatsRepository.getStatsForToday(userId)
        stats?.let {
            if (it.tasksCompleted > 0) {
                val updatedStats = it.copy(tasksCompleted = it.tasksCompleted - 1)
                dailyStatsRepository.insertOrUpdateStats(updatedStats)
            }
        }
    }
    
    private suspend fun updateDailyStatsForTaskCreation() {
        try {
            dailyStatsRepository.updateTodayStats(
                userId = userId,
                pomodoroCompleted = false,
                focusTimeMinutes = 0,
                taskCompleted = false,
                taskCreated = true
            )
        } catch (e: Exception) {
            // Log error but don't show to user as it's not critical
        }
    }
    
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    fun clearState() {
        _uiState.value = TaskUiState()
    }
}