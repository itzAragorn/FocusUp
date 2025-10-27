package com.example.focusup.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("userId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val name: String,
    val description: String?,
    val date: String, // Formato "yyyy-MM-dd"
    val time: String, // Formato "HH:mm"
    val imagePath: String? = null, // Ruta de la imagen adjunta
    val isCompleted: Boolean = false,
    val isNotificationEnabled: Boolean = true,
    val priority: String = "NONE", // HIGH, MEDIUM, LOW, NONE
    val tags: String? = null, // Etiquetas separadas por comas
    val attachments: String? = null, // Archivos adjuntos serializados (uri|##|fileName|##|fileSize|||...)
    val recurrenceType: String = "NONE", // NONE, DAILY, WEEKLY, MONTHLY
    val recurrenceEndDate: String? = null, // Formato "yyyy-MM-dd" - cuándo termina la recurrencia
    val parentTaskId: Long? = null, // ID de la tarea padre si es una tarea generada por recurrencia
    val createdAt: Long = System.currentTimeMillis()
)