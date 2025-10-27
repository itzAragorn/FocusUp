package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusup.data.database.entities.Task
import com.example.focusup.data.repository.TaskRepository
import com.example.focusup.presentation.components.TaskFilter
import com.example.focusup.domain.model.TaskPriority
import com.example.focusup.domain.model.Tag
import com.example.focusup.utils.DateTimeUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class CalendarScreenUiState(
    val tasks: List<Task> = emptyList(),
    val filteredTasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val selectedPriorities: Set<TaskPriority> = emptySet(),
    val selectedTags: Set<String> = emptySet(),
    val availableTags: List<String> = emptyList()
)

class CalendarScreenViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(CalendarScreenUiState())
    val uiState: StateFlow<CalendarScreenUiState> = _uiState.asStateFlow()
    
    fun loadTasksForUser(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                taskRepository.getTasksByUser(userId).collect { tasks ->
                    // Extraer todas las etiquetas disponibles
                    val allTags = tasks
                        .mapNotNull { it.tags }
                        .filter { it.isNotBlank() }
                        .flatMap { Tag.parseFromString(it) }
                        .distinct()
                        .sorted()
                    
                    _uiState.value = _uiState.value.copy(
                        tasks = tasks,
                        availableTags = allTags,
                        isLoading = false
                    )
                    applyFilters()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar tareas: ${e.message}"
                )
            }
        }
    }
    
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }
    
    fun updateFilter(filter: TaskFilter) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
        applyFilters()
    }
    
    fun togglePriorityFilter(priority: TaskPriority) {
        val currentPriorities = _uiState.value.selectedPriorities.toMutableSet()
        if (currentPriorities.contains(priority)) {
            currentPriorities.remove(priority)
        } else {
            currentPriorities.add(priority)
        }
        _uiState.value = _uiState.value.copy(selectedPriorities = currentPriorities)
        applyFilters()
    }
    
    fun toggleTagFilter(tag: String) {
        val currentTags = _uiState.value.selectedTags.toMutableSet()
        if (currentTags.contains(tag)) {
            currentTags.remove(tag)
        } else {
            currentTags.add(tag)
        }
        _uiState.value = _uiState.value.copy(selectedTags = currentTags)
        applyFilters()
    }
    
    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedFilter = TaskFilter.ALL,
            selectedPriorities = emptySet(),
            selectedTags = emptySet()
        )
        applyFilters()
    }
    
    private fun applyFilters() {
        var filtered = _uiState.value.tasks
        
        // Aplicar búsqueda por texto
        if (_uiState.value.searchQuery.isNotEmpty()) {
            val query = _uiState.value.searchQuery.lowercase()
            filtered = filtered.filter { task ->
                task.name.lowercase().contains(query) ||
                task.description?.lowercase()?.contains(query) == true
            }
        }
        
        // Aplicar filtro de estado
        filtered = when (_uiState.value.selectedFilter) {
            TaskFilter.ALL -> filtered
            TaskFilter.PENDING -> filtered.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> filtered.filter { it.isCompleted }
            TaskFilter.TODAY -> {
                val today = DateTimeUtils.getCurrentDate()
                filtered.filter { it.date == today }
            }
            TaskFilter.UPCOMING -> {
                val today = DateTimeUtils.getCurrentDate()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayDate = dateFormat.parse(today) ?: Date()
                filtered.filter { task ->
                    try {
                        val taskDate = dateFormat.parse(task.date)
                        taskDate != null && taskDate.after(todayDate)
                    } catch (e: Exception) {
                        false
                    }
                }
            }
        }
        
        // Aplicar filtro de prioridad
        if (_uiState.value.selectedPriorities.isNotEmpty()) {
            filtered = filtered.filter { task ->
                try {
                    val priority = TaskPriority.valueOf(task.priority)
                    _uiState.value.selectedPriorities.contains(priority)
                } catch (e: Exception) {
                    false
                }
            }
        }
        
        // Aplicar filtro de etiquetas
        if (_uiState.value.selectedTags.isNotEmpty()) {
            filtered = filtered.filter { task ->
                if (task.tags.isNullOrBlank()) {
                    false
                } else {
                    val taskTags = Tag.parseFromString(task.tags)
                    _uiState.value.selectedTags.any { selectedTag ->
                        taskTags.contains(selectedTag)
                    }
                }
            }
        }
        
        _uiState.value = _uiState.value.copy(filteredTasks = filtered)
    }
    
    fun getTasksForDate(date: String): List<Task> {
        return _uiState.value.tasks.filter { it.date == date }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}