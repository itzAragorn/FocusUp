package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusup.domain.model.PomodoroSession
import com.example.focusup.domain.model.PomodoroState
import com.example.focusup.notifications.NotificationHelper
import com.example.focusup.data.repository.ProductivityStatsRepository
import com.example.focusup.utils.DateTimeUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PomodoroViewModel(
    private val notificationHelper: NotificationHelper,
    private val statsRepository: ProductivityStatsRepository
) : ViewModel() {
    
    private val _session = MutableStateFlow(
        PomodoroSession(
            sessionNumber = 1,
            state = PomodoroState.IDLE,
            timeLeftSeconds = PomodoroSession.WORK_DURATION,
            totalTimeSeconds = PomodoroSession.WORK_DURATION,
            completedPomodoros = 0
        )
    )
    val session: StateFlow<PomodoroSession> = _session.asStateFlow()
    
    private var timerJob: Job? = null
    private var currentUserId: Long? = null
    
    fun setUserId(userId: Long) {
        currentUserId = userId
    }
    
    fun startWork() {
        val currentSession = _session.value
        _session.value = currentSession.copy(
            state = PomodoroState.WORK,
            timeLeftSeconds = PomodoroSession.WORK_DURATION,
            totalTimeSeconds = PomodoroSession.WORK_DURATION
        )
        startTimer()
    }
    
    fun startBreak() {
        val currentSession = _session.value
        val isLongBreak = (currentSession.completedPomodoros % PomodoroSession.POMODOROS_BEFORE_LONG_BREAK) == 0
                && currentSession.completedPomodoros > 0
        
        val duration = if (isLongBreak) {
            PomodoroSession.LONG_BREAK_DURATION
        } else {
            PomodoroSession.SHORT_BREAK_DURATION
        }
        
        val newState = if (isLongBreak) PomodoroState.LONG_BREAK else PomodoroState.SHORT_BREAK
        
        _session.value = currentSession.copy(
            state = newState,
            timeLeftSeconds = duration,
            totalTimeSeconds = duration
        )
        startTimer()
    }
    
    fun pause() {
        timerJob?.cancel()
        val currentSession = _session.value
        _session.value = currentSession.copy(state = PomodoroState.PAUSED)
    }
    
    fun resume() {
        val currentSession = _session.value
        if (currentSession.state == PomodoroState.PAUSED) {
            val previousState = when {
                currentSession.totalTimeSeconds == PomodoroSession.WORK_DURATION -> PomodoroState.WORK
                currentSession.totalTimeSeconds == PomodoroSession.SHORT_BREAK_DURATION -> PomodoroState.SHORT_BREAK
                currentSession.totalTimeSeconds == PomodoroSession.LONG_BREAK_DURATION -> PomodoroState.LONG_BREAK
                else -> PomodoroState.WORK
            }
            _session.value = currentSession.copy(state = previousState)
            startTimer()
        }
    }
    
    fun reset() {
        timerJob?.cancel()
        _session.value = PomodoroSession(
            sessionNumber = 1,
            state = PomodoroState.IDLE,
            timeLeftSeconds = PomodoroSession.WORK_DURATION,
            totalTimeSeconds = PomodoroSession.WORK_DURATION,
            completedPomodoros = 0
        )
    }
    
    fun skip() {
        timerJob?.cancel()
        val currentSession = _session.value
        
        when (currentSession.state) {
            PomodoroState.WORK -> {
                // Si estaba trabajando, contar como completado y pasar a descanso
                viewModelScope.launch {
                    incrementPomodoro()
                }
                _session.value = currentSession.copy(
                    completedPomodoros = currentSession.completedPomodoros + 1
                )
                startBreak()
            }
            PomodoroState.SHORT_BREAK, PomodoroState.LONG_BREAK -> {
                // Si estaba en descanso, volver a trabajo
                startWork()
            }
            else -> {}
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_session.value.timeLeftSeconds > 0 && 
                   _session.value.state != PomodoroState.PAUSED &&
                   _session.value.state != PomodoroState.IDLE) {
                delay(1000)
                
                val currentSession = _session.value
                val newTimeLeft = currentSession.timeLeftSeconds - 1
                
                _session.value = currentSession.copy(timeLeftSeconds = newTimeLeft)
                
                // Si terminó el tiempo
                if (newTimeLeft == 0) {
                    onTimerComplete()
                }
            }
        }
    }
    
    private suspend fun onTimerComplete() {
        val currentSession = _session.value
        
        when (currentSession.state) {
            PomodoroState.WORK -> {
                // Pomodoro completado
                notificationHelper.showPomodoroNotification(
                    title = "¡Pomodoro completado! 🎉",
                    message = "Es hora de tomar un descanso"
                )
                incrementPomodoro()
                
                _session.value = currentSession.copy(
                    completedPomodoros = currentSession.completedPomodoros + 1,
                    state = PomodoroState.IDLE
                )
            }
            PomodoroState.SHORT_BREAK, PomodoroState.LONG_BREAK -> {
                // Descanso completado
                notificationHelper.showPomodoroNotification(
                    title = "Descanso terminado",
                    message = "¿Listo para otro Pomodoro?"
                )
                _session.value = currentSession.copy(state = PomodoroState.IDLE)
            }
            else -> {}
        }
    }
    
    private suspend fun incrementPomodoro() {
        currentUserId?.let { userId ->
            val today = DateTimeUtils.getCurrentDate()
            statsRepository.incrementPomodoro(userId, today)
            
            // Agregar tiempo de estudio (25 minutos)
            statsRepository.addStudyTime(userId, today, 25)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
