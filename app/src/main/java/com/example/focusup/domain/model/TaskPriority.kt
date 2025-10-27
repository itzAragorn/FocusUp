package com.example.focusup.domain.model

import androidx.compose.ui.graphics.Color

enum class TaskPriority(val displayName: String, val color: Color) {
    HIGH("Alta", Color(0xFFF44336)),      // Rojo
    MEDIUM("Media", Color(0xFFFF9800)),   // Naranja
    LOW("Baja", Color(0xFF4CAF50)),       // Verde
    NONE("Sin prioridad", Color(0xFF9E9E9E)) // Gris
}

fun String?.toPriority(): TaskPriority {
    return when (this?.uppercase()) {
        "HIGH" -> TaskPriority.HIGH
        "MEDIUM" -> TaskPriority.MEDIUM
        "LOW" -> TaskPriority.LOW
        else -> TaskPriority.NONE
    }
}
