package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusup.data.database.entities.Task
import com.example.focusup.data.database.entities.ScheduleBlock
import com.example.focusup.data.repository.TaskRepository
import com.example.focusup.data.repository.ScheduleRepository
import com.example.focusup.utils.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class HomeScreenUiState(
    val isLoading: Boolean = false,
    val todayTasks: List<Task> = emptyList(),
    val todayScheduleBlocks: List<ScheduleBlock> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val completedTasksCount: Int = 0,
    val errorMessage: String? = null
)

class HomeScreenViewModel(
    private val taskRepository: TaskRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()
    
    fun loadHomeData(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val today = DateTimeUtils.getCurrentDate()
                val currentCalendar = Calendar.getInstance()
                val currentDayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK)
                // Convert Calendar.DAY_OF_WEEK (Sunday=1) to our format (Monday=1)
                val adjustedDayOfWeek = if (currentDayOfWeek == Calendar.SUNDAY) 7 else currentDayOfWeek - 1
                
                // Get today's tasks
                val todayTasks = taskRepository.getTasksByUserAndDate(userId, today).first()
                
                // Get today's schedule blocks
                val todayScheduleBlocks = scheduleRepository.getScheduleBlocksByUserAndDay(userId, adjustedDayOfWeek).first()
                
                // Get upcoming tasks (next 7 days)
                val upcomingTasks = getUpcomingTasks(userId)
                
                // Count completed tasks for today
                val completedTasksCount = todayTasks.count { it.isCompleted }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    todayTasks = todayTasks,
                    todayScheduleBlocks = todayScheduleBlocks,
                    upcomingTasks = upcomingTasks,
                    completedTasksCount = completedTasksCount,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error loading home data"
                )
            }
        }
    }
    
    private suspend fun getUpcomingTasks(userId: Long): List<Task> {
        val tasks = mutableListOf<Task>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        // Get tasks for the next 7 days
        for (i in 0..6) {
            val date = dateFormat.format(calendar.time)
            val dayTasks = taskRepository.getTasksByUserAndDate(userId, date).first()
            tasks.addAll(dayTasks.filter { !it.isCompleted })
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        return tasks.sortedWith(compareBy<Task> { it.date }.thenBy { it.time }).take(10)
    }
    
    fun getTodayTasksCount(): Int = _uiState.value.todayTasks.size
    
    fun getTodayScheduleBlocksCount(): Int = _uiState.value.todayScheduleBlocks.size
    
    fun getCompletedTasksCount(): Int = _uiState.value.completedTasksCount
    
    fun clearState() {
        _uiState.value = HomeScreenUiState()
    }
}