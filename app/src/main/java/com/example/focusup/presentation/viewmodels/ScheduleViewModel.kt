package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusup.data.database.entities.ScheduleBlock
import com.example.focusup.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ScheduleUiState(
    val isLoading: Boolean = false,
    val isScheduleBlockSaved: Boolean = false,
    val errorMessage: String? = null
)

class ScheduleViewModel(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()
    
    fun saveScheduleBlock(
        userId: Long,
        name: String,
        description: String?,
        dayOfWeek: Int,
        startTime: String,
        endTime: String,
        color: String,
        professor: String? = null,
        classroom: String? = null
    ) {
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "El nombre es obligatorio")
            return
        }
        
        // Validar que la hora de inicio sea antes que la de fin
        if (!isValidTimeRange(startTime, endTime)) {
            _uiState.value = _uiState.value.copy(errorMessage = "La hora de inicio debe ser anterior a la hora de fin")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val scheduleBlock = ScheduleBlock(
                    userId = userId,
                    name = name,
                    description = description,
                    color = color,
                    dayOfWeek = dayOfWeek,
                    startTime = startTime,
                    endTime = endTime,
                    professor = professor,
                    classroom = classroom
                )
                
                scheduleRepository.insertScheduleBlock(scheduleBlock)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isScheduleBlockSaved = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al guardar el bloque: ${e.message}"
                )
            }
        }
    }
    
    private fun isValidTimeRange(startTime: String, endTime: String): Boolean {
        return try {
            val start = startTime.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
            val end = endTime.split(":").let { it[0].toInt() * 60 + it[1].toInt() }
            start < end
        } catch (e: Exception) {
            false
        }
    }
    
    fun deleteScheduleBlock(scheduleBlock: ScheduleBlock) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                scheduleRepository.deleteScheduleBlock(scheduleBlock)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al eliminar el bloque de horario: ${e.message}"
                )
            }
        }
    }
    
    fun clearState() {
        _uiState.value = ScheduleUiState()
    }
}