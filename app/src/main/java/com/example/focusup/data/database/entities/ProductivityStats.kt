package com.example.focusup.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "productivity_stats",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class ProductivityStats(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val date: String, // formato: yyyy-MM-dd
    val tasksCompleted: Int = 0,
    val tasksCreated: Int = 0,
    val pomodorosCompleted: Int = 0,
    val studyTimeMinutes: Int = 0, // tiempo total de estudio/trabajo
    val streak: Int = 0 // racha de días consecutivos
)
