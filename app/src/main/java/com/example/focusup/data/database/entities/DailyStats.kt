package com.example.focusup.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stats")
data class DailyStats(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val date: String, // YYYY-MM-DD format
    val pomodoroSessionsCompleted: Int = 0,
    val totalFocusTimeMinutes: Int = 0,
    val tasksCompleted: Int = 0,
    val tasksCreated: Int = 0,
    val studyStreakDays: Int = 0,
    val productivityScore: Float = 0.0f, // 0-100 scale
    val createdAt: Long = System.currentTimeMillis()
)