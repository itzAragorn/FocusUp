package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusup.data.database.entities.ScheduleBlock
import com.example.focusup.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ScheduleScreenUiState(
    val scheduleBlocks: List<ScheduleBlock> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ScheduleScreenViewModel(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ScheduleScreenUiState())
    val uiState: StateFlow<ScheduleScreenUiState> = _uiState.asStateFlow()
    
    fun loadScheduleBlocksForUser(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                scheduleRepository.getScheduleBlocksByUser(userId).collect { blocks ->
                    _uiState.value = _uiState.value.copy(
                        scheduleBlocks = blocks,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al cargar horarios: ${e.message}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}