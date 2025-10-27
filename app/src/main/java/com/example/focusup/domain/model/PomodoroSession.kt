package com.example.focusup.domain.model

enum class PomodoroState {
    IDLE,
    WORK,
    SHORT_BREAK,
    LONG_BREAK,
    PAUSED
}

data class PomodoroSession(
    val sessionNumber: Int,
    val state: PomodoroState,
    val timeLeftSeconds: Int,
    val totalTimeSeconds: Int,
    val completedPomodoros: Int = 0
) {
    val progress: Float
        get() = if (totalTimeSeconds > 0) {
            1f - (timeLeftSeconds.toFloat() / totalTimeSeconds.toFloat())
        } else 0f
    
    fun getTimeFormatted(): String {
        val minutes = timeLeftSeconds / 60
        val seconds = timeLeftSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
    
    companion object {
        const val WORK_DURATION = 25 * 60 // 25 minutos
        const val SHORT_BREAK_DURATION = 5 * 60 // 5 minutos
        const val LONG_BREAK_DURATION = 15 * 60 // 15 minutos
        const val POMODOROS_BEFORE_LONG_BREAK = 4
    }
}
