package com.example.focusup.domain.model

enum class RecurrenceType(val displayName: String) {
    NONE("Sin recurrencia"),
    DAILY("Diaria"),
    WEEKLY("Semanal"),
    MONTHLY("Mensual");
    
    companion object {
        fun fromString(value: String?): RecurrenceType {
            return when (value) {
                "DAILY" -> DAILY
                "WEEKLY" -> WEEKLY
                "MONTHLY" -> MONTHLY
                else -> NONE
            }
        }
    }
}
