package com.example.focusup.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "achievements",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class Achievement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val achievementType: String, // "FIRST_POMODORO", "STREAK_7", "TASK_MASTER", etc.
    val title: String,
    val description: String,
    val badgeIcon: String, // Emoji or icon name
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val targetValue: Int, // Valor objetivo (ej: 7 para racha de 7 días)
    val currentProgress: Int = 0, // Progreso actual hacia el objetivo
    val category: String, // "PRODUCTIVITY", "CONSISTENCY", "MILESTONE"
    val rarity: String = "COMMON" // "COMMON", "RARE", "EPIC", "LEGENDARY"
)

enum class AchievementType(
    val id: String,
    val title: String,
    val description: String,
    val badgeIcon: String,
    val xpReward: Int,
    val targetValue: Int,
    val category: String,
    val rarity: String
) {
    // Achievements de primeros pasos
    FIRST_POMODORO("FIRST_POMODORO", "Primer Enfoque", "Completa tu primer pomodoro", "🍅", 50, 1, "MILESTONE", "COMMON"),
    FIRST_TASK("FIRST_TASK", "Primera Victoria", "Completa tu primera tarea", "✅", 50, 1, "MILESTONE", "COMMON"),
    FIRST_DAY("FIRST_DAY", "Día Productivo", "Completa al menos 3 pomodoros en un día", "🌟", 100, 3, "PRODUCTIVITY", "COMMON"),
    
    // Achievements de consistencia
    STREAK_3("STREAK_3", "Comenzando", "Mantén una racha de 3 días", "🔥", 150, 3, "CONSISTENCY", "COMMON"),
    STREAK_7("STREAK_7", "Una Semana", "Mantén una racha de 7 días", "🔥", 300, 7, "CONSISTENCY", "RARE"),
    STREAK_30("STREAK_30", "Un Mes", "Mantén una racha de 30 días", "🔥", 1000, 30, "CONSISTENCY", "EPIC"),
    STREAK_100("STREAK_100", "Centurión", "Mantén una racha de 100 días", "🔥", 5000, 100, "CONSISTENCY", "LEGENDARY"),
    
    // Achievements de productividad
    POMODORO_10("POMODORO_10", "Enfocado", "Completa 10 pomodoros", "🍅", 200, 10, "PRODUCTIVITY", "COMMON"),
    POMODORO_50("POMODORO_50", "Concentrado", "Completa 50 pomodoros", "🍅", 500, 50, "PRODUCTIVITY", "RARE"),
    POMODORO_100("POMODORO_100", "Maestro del Enfoque", "Completa 100 pomodoros", "🍅", 1000, 100, "PRODUCTIVITY", "EPIC"),
    POMODORO_500("POMODORO_500", "Leyenda Pomodoro", "Completa 500 pomodoros", "🍅", 2500, 500, "PRODUCTIVITY", "LEGENDARY"),
    
    // Achievements de tareas
    TASK_MASTER_25("TASK_MASTER_25", "Organizador", "Completa 25 tareas", "📋", 200, 25, "PRODUCTIVITY", "COMMON"),
    TASK_MASTER_100("TASK_MASTER_100", "Ejecutor", "Completa 100 tareas", "📋", 500, 100, "PRODUCTIVITY", "RARE"),
    TASK_MASTER_500("TASK_MASTER_500", "Conquistador", "Completa 500 tareas", "📋", 1500, 500, "PRODUCTIVITY", "EPIC"),
    
    // Achievements especiales
    PERFECT_DAY("PERFECT_DAY", "Día Perfecto", "Completa todas las tareas programadas en un día", "💎", 500, 1, "MILESTONE", "RARE"),
    EARLY_BIRD("EARLY_BIRD", "Madrugador", "Completa un pomodoro antes de las 7 AM", "🌅", 200, 1, "MILESTONE", "RARE"),
    NIGHT_OWL("NIGHT_OWL", "Búho Nocturno", "Completa un pomodoro después de las 10 PM", "🦉", 200, 1, "MILESTONE", "RARE"),
    SPEED_DEMON("SPEED_DEMON", "Veloz", "Completa 10 tareas en un día", "⚡", 300, 10, "PRODUCTIVITY", "RARE"),
    
    // Achievements de tiempo
    TIME_WARRIOR_10H("TIME_WARRIOR_10H", "Guerrero del Tiempo", "Acumula 10 horas de enfoque", "⏰", 400, 600, "PRODUCTIVITY", "COMMON"), // 600 minutos
    TIME_WARRIOR_50H("TIME_WARRIOR_50H", "Maestro del Tiempo", "Acumula 50 horas de enfoque", "⏰", 1000, 3000, "PRODUCTIVITY", "RARE"), // 3000 minutos
    TIME_WARRIOR_100H("TIME_WARRIOR_100H", "Señor del Tiempo", "Acumula 100 horas de enfoque", "⏰", 2000, 6000, "PRODUCTIVITY", "EPIC") // 6000 minutos
}