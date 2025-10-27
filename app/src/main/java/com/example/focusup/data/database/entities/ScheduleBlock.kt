package com.example.focusup.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedule_blocks",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("userId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class ScheduleBlock(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val description: String?,
    val color: String, // Color en formato hex (#RRGGBB)
    val dayOfWeek: Int, // 1-7 (Lunes a Domingo)
    val startTime: String, // Formato "HH:mm"
    val endTime: String, // Formato "HH:mm"
    
    // Campos específicos para estudiantes
    val professor: String? = null,
    val classroom: String? = null,
    
    val createdAt: Long = System.currentTimeMillis()
)