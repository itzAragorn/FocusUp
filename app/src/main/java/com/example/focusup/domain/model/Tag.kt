package com.example.focusup.domain.model

import androidx.compose.ui.graphics.Color

data class Tag(
    val id: Long = 0,
    val name: String,
    val color: Color
) {
    companion object {
        // Colores predefinidos para tags
        val PREDEFINED_COLORS = listOf(
            Color(0xFFE57373), // Red
            Color(0xFFFFB74D), // Orange
            Color(0xFFFFF176), // Yellow
            Color(0xFF81C784), // Green
            Color(0xFF64B5F6), // Blue
            Color(0xFF9575CD), // Purple
            Color(0xFFBA68C8), // Pink
            Color(0xFF4DD0E1), // Cyan
            Color(0xFFA1887F), // Brown
            Color(0xFF90A4AE)  // Grey
        )
        
        fun parseFromString(tagsString: String?): List<String> {
            return tagsString?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
        }
        
        fun convertToString(tags: List<String>): String {
            return tags.joinToString(",")
        }
    }
}
