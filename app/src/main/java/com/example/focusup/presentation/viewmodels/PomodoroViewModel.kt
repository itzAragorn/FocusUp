package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusup.domain.model.PomodoroSession
import com.example.focusup.domain.model.PomodoroState
import com.example.focusup.notifications.NotificationHelper
import com.example.focusup.data.repository.ProductivityStatsRepository
import com.example.focusup.data.repository.DailyStatsRepository
import com.example.focusup.data.database.entities.AchievementType
import com.example.focusup.utils.DateTimeUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalTime

class PomodoroViewModel(
    private val notificationHelper: NotificationHelper,
    private val statsRepository: ProductivityStatsRepository,
    private val dailyStatsRepository: DailyStatsRepository,
    private val userId: Long = 1L, // TODO: Get from auth
    private val gamificationViewModel: GamificationViewModel? = null
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
                val completedCount = currentSession.completedPomodoros + 1
                notificationHelper.showPomodoroNotification(
                    title = "🎉 ¡Pomodoro #$completedCount completado!",
                    message = "Excelente trabajo. Es hora de tomar un descanso merecido 💪",
                    notificationId = 1000
                )
                incrementPomodoro()
                
                _session.value = currentSession.copy(
                    completedPomodoros = completedCount,
                    state = PomodoroState.IDLE
                )
            }
            PomodoroState.SHORT_BREAK -> {
                // Descanso corto completado
                notificationHelper.showPomodoroNotification(
                    title = "⏱️ Descanso corto terminado",
                    message = "¡Recargaste energías! ¿Listo para otro Pomodoro de 25 minutos?",
                    notificationId = 1001
                )
                _session.value = currentSession.copy(state = PomodoroState.IDLE)
            }
            PomodoroState.LONG_BREAK -> {
                // Descanso largo completado
                notificationHelper.showPomodoroNotification(
                    title = "🌟 Descanso largo terminado",
                    message = "¡Gran sesión de trabajo! Estás listo para continuar con energía renovada 🚀",
                    notificationId = 1002
                )
                _session.value = currentSession.copy(state = PomodoroState.IDLE)
            }
            else -> {}
        }
    }
    
    private suspend fun incrementPomodoro() {
        try {
            // Actualizar sistema viejo (ProductivityStats)
            currentUserId?.let { oldUserId ->
                val today = DateTimeUtils.getCurrentDate()
                statsRepository.incrementPomodoro(oldUserId, today)
                statsRepository.addStudyTime(oldUserId, today, 25)
            }
            
            // Actualizar sistema nuevo (DailyStats)
            dailyStatsRepository.updateTodayStats(
                userId = userId,
                pomodoroCompleted = true,
                focusTimeMinutes = 25, // 25 minutos por pomodoro
                taskCompleted = false,
                taskCreated = false
            )
            
            // *** GAMIFICACIÓN: Actualizar progreso y desbloquear achievements ***
            gamificationViewModel?.let { gamification ->
                val stats = dailyStatsRepository.getStatsForToday(userId)
                stats?.let { dailyStats ->
                    val totalStats = dailyStatsRepository.getTotalStats(userId)
                    
                    // Calcular XP basado en streak
                    val streakDays = dailyStats.studyStreakDays
                    val xpToAdd = XpCalculator.calculatePomodoroXp(streakDays)
                    
                    // Verificar si es Early Bird (6:00 - 9:00) o Night Owl (22:00 - 2:00)
                    val currentHour = LocalTime.now().hour
                    when {
                        currentHour in 6..8 -> {
                            gamification.unlockSpecialAchievement(AchievementType.EARLY_BIRD)
                        }
                        currentHour >= 22 || currentHour <= 2 -> {
                            gamification.unlockSpecialAchievement(AchievementType.NIGHT_OWL)
                        }
                    }
                    
                    // Actualizar progreso general
                    gamification.onActivityCompleted(
                        xpToAdd = xpToAdd,
                        totalPomodoros = totalStats.totalPomodoros + 1,
                        totalTasks = totalStats.totalTasks,
                        totalFocusTimeMinutes = totalStats.totalFocusTime + 25,
                        currentStreak = dailyStats.studyStreakDays,
                        dailyPomodoros = dailyStats.pomodoroSessionsCompleted + 1,
                        dailyTasks = dailyStats.tasksCompleted
                    )
                }
            }
        } catch (e: Exception) {
            // Log error but continue execution
        }
    }
    
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
