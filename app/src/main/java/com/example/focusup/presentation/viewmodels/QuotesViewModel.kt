package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.focusup.data.api.models.Quote
import com.example.focusup.data.api.models.QuoteState
import com.example.focusup.data.api.network.NetworkResult
import com.example.focusup.data.repository.QuoteContext
import com.example.focusup.data.repository.QuotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar las citas motivacionales
 */
class QuotesViewModel(
    private val quotesRepository: QuotesRepository
) : ViewModel() {
    
    // Estados para diferentes tipos de citas
    private val _dailyQuoteState = MutableStateFlow<QuoteState>(QuoteState.Loading)
    val dailyQuoteState: StateFlow<QuoteState> = _dailyQuoteState.asStateFlow()
    
    private val _pomodoroQuoteState = MutableStateFlow<QuoteState>(QuoteState.Loading)
    val pomodoroQuoteState: StateFlow<QuoteState> = _pomodoroQuoteState.asStateFlow()
    
    private val _achievementQuoteState = MutableStateFlow<QuoteState>(QuoteState.Loading)
    val achievementQuoteState: StateFlow<QuoteState> = _achievementQuoteState.asStateFlow()
    
    private val _randomQuoteState = MutableStateFlow<QuoteState>(QuoteState.Loading)
    val randomQuoteState: StateFlow<QuoteState> = _randomQuoteState.asStateFlow()
    
    // Estado general para controlar errores
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadDailyQuote()
    }
    
    /**
     * Cargar la cita del día
     */
    fun loadDailyQuote() {
        viewModelScope.launch {
            _dailyQuoteState.value = QuoteState.Loading
            
            when (val result = quotesRepository.getQuoteOfTheDay()) {
                is NetworkResult.Success -> {
                    _dailyQuoteState.value = QuoteState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _dailyQuoteState.value = QuoteState.Error(result.message)
                    _errorMessage.value = result.message
                }
                is NetworkResult.Loading -> {
                    _dailyQuoteState.value = QuoteState.Loading
                }
            }
        }
    }
    
    /**
     * Cargar cita para Pomodoro
     */
    fun loadPomodoroQuote() {
        viewModelScope.launch {
            _pomodoroQuoteState.value = QuoteState.Loading
            
            when (val result = quotesRepository.getContextualQuote(QuoteContext.POMODORO_BREAK)) {
                is NetworkResult.Success -> {
                    _pomodoroQuoteState.value = QuoteState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _pomodoroQuoteState.value = QuoteState.Error(result.message)
                    _errorMessage.value = result.message
                }
                is NetworkResult.Loading -> {
                    _pomodoroQuoteState.value = QuoteState.Loading
                }
            }
        }
    }
    
    /**
     * Cargar cita para celebrar logros
     */
    fun loadAchievementQuote() {
        viewModelScope.launch {
            _achievementQuoteState.value = QuoteState.Loading
            
            when (val result = quotesRepository.getContextualQuote(QuoteContext.ACHIEVEMENT_UNLOCK)) {
                is NetworkResult.Success -> {
                    _achievementQuoteState.value = QuoteState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _achievementQuoteState.value = QuoteState.Error(result.message)
                    _errorMessage.value = result.message
                }
                is NetworkResult.Loading -> {
                    _achievementQuoteState.value = QuoteState.Loading
                }
            }
        }
    }
    
    /**
     * Cargar cita aleatoria
     */
    fun loadRandomQuote() {
        viewModelScope.launch {
            _randomQuoteState.value = QuoteState.Loading
            
            when (val result = quotesRepository.getRandomQuote()) {
                is NetworkResult.Success -> {
                    _randomQuoteState.value = QuoteState.Success(result.data)
                }
                is NetworkResult.Error -> {
                    _randomQuoteState.value = QuoteState.Error(result.message)
                    _errorMessage.value = result.message
                }
                is NetworkResult.Loading -> {
                    _randomQuoteState.value = QuoteState.Loading
                }
            }
        }
    }
    
    /**
     * Cargar cita contextual
     */
    fun loadContextualQuote(context: QuoteContext) {
        viewModelScope.launch {
            _isLoading.value = true
            
            when (val result = quotesRepository.getContextualQuote(context)) {
                is NetworkResult.Success -> {
                    // Actualizar el estado apropiado según el contexto
                    when (context) {
                        QuoteContext.DAILY_INSPIRATION -> _dailyQuoteState.value = QuoteState.Success(result.data)
                        QuoteContext.POMODORO_BREAK -> _pomodoroQuoteState.value = QuoteState.Success(result.data)
                        QuoteContext.ACHIEVEMENT_UNLOCK -> _achievementQuoteState.value = QuoteState.Success(result.data)
                        else -> _randomQuoteState.value = QuoteState.Success(result.data)
                    }
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                    // También actualizar el estado apropiado con error
                    when (context) {
                        QuoteContext.DAILY_INSPIRATION -> _dailyQuoteState.value = QuoteState.Error(result.message)
                        QuoteContext.POMODORO_BREAK -> _pomodoroQuoteState.value = QuoteState.Error(result.message)
                        QuoteContext.ACHIEVEMENT_UNLOCK -> _achievementQuoteState.value = QuoteState.Error(result.message)
                        else -> _randomQuoteState.value = QuoteState.Error(result.message)
                    }
                }
                is NetworkResult.Loading -> {
                    // Los estados Loading ya se manejan arriba
                }
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Limpiar mensaje de error
     */
    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Refresh general - recarga la cita del día
     */
    fun refresh() {
        loadDailyQuote()
    }
    
    /**
     * Factory para crear el ViewModel
     */
    class Factory(
        private val quotesRepository: QuotesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuotesViewModel::class.java)) {
                return QuotesViewModel(quotesRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}