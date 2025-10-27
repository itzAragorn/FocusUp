package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusup.data.database.entities.Task
import com.example.focusup.data.repository.TaskRepository
import com.example.focusup.domain.manager.RecurrenceManager
import com.example.focusup.utils.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TaskUiState(
    val isLoading: Boolean = false,
    val isTaskSaved: Boolean = false,
    val errorMessage: String? = null,
    val showRecurrenceDialog: Boolean = false,
    val selectedTask: Task? = null
)

class TaskViewModel(
    private val taskRepository: TaskRepository
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
                    val updatedTask = it.copy(isCompleted = !it.isCompleted)
                    taskRepository.updateTask(updatedTask)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error al actualizar la tarea: ${e.message}"
                )
            }
        }
    }
    
    fun clearState() {
        _uiState.value = TaskUiState()
    }
}