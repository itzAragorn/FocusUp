package com.example.focusup.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "user_progress",
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
data class UserProgress(
    @PrimaryKey
    val userId: Long,
    val totalXp: Int = 0,
    val currentLevel: Int = 1,
    val xpToNextLevel: Int = 100, // XP necesario para el siguiente nivel
    val totalAchievements: Int = 0,
    val lastActiveDate: String? = null, // Fecha de última actividad para streaks
    val badges: List<String> = emptyList(), // Lista de badges desbloqueados
    val title: String = "Novato", // Título actual del usuario
    val updatedAt: Long = System.currentTimeMillis()
)

object LevelSystem {
    // Función para calcular el XP necesario para cada nivel
    fun getXpRequiredForLevel(level: Int): Int {
        return when {
            level <= 5 -> level * 100 // Niveles 1-5: 100, 200, 300, 400, 500
            level <= 10 -> 500 + (level - 5) * 200 // Niveles 6-10: 700, 900, 1100, 1300, 1500
            level <= 20 -> 1500 + (level - 10) * 300 // Niveles 11-20: más XP por nivel
            else -> 1500 + 10 * 300 + (level - 20) * 500 // Niveles 20+: mucho más XP
        }
    }
    
    // Función para calcular el nivel basado en XP total
    fun getLevelFromXp(totalXp: Int): Int {
        var level = 1
        var xpForCurrentLevel = 0
        
        while (xpForCurrentLevel <= totalXp) {
            level++
            xpForCurrentLevel += getXpRequiredForLevel(level - 1)
        }
        
        return maxOf(1, level - 1)
    }
    
    // Función para obtener el XP necesario para el siguiente nivel
    fun getXpToNextLevel(totalXp: Int): Int {
        val currentLevel = getLevelFromXp(totalXp)
        val xpForCurrentLevel = (1 until currentLevel).sumOf { getXpRequiredForLevel(it) }
        val xpRequiredForNext = getXpRequiredForLevel(currentLevel)
        
        return xpRequiredForNext - (totalXp - xpForCurrentLevel)
    }
    
    // Títulos basados en nivel
    fun getTitleForLevel(level: Int): String {
        return when {
            level < 5 -> "Novato"
            level < 10 -> "Aprendiz"
            level < 15 -> "Enfocado"
            level < 20 -> "Disciplinado"
            level < 30 -> "Maestro"
            level < 40 -> "Experto"
            level < 50 -> "Virtuoso"
            else -> "Leyenda"
        }
    }
}