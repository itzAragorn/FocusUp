package com.example.focusup.domain.model

enum class ProfileType(val displayName: String) {
    STUDENT("Estudiante"),
    WORKER("Trabajador")
}

enum class DayOfWeek(val displayName: String, val value: Int) {
    MONDAY("Lunes", 1),
    TUESDAY("Martes", 2),
    WEDNESDAY("Miércoles", 3),
    THURSDAY("Jueves", 4),
    FRIDAY("Viernes", 5),
    SATURDAY("Sábado", 6),
    SUNDAY("Domingo", 7);
    
    companion object {
        fun fromValue(value: Int): DayOfWeek {
            return values().first { it.value == value }
        }
    }
}