package com.example.focusup.utils

object ValidationUtils {
    
    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
    
    fun isValidName(name: String): Boolean {
        return name.isNotBlank() && name.length >= 2
    }
    
    fun isValidTimeRange(startTime: String, endTime: String): Boolean {
        try {
            val start = DateTimeUtils.parseStoredTime(startTime)
            val end = DateTimeUtils.parseStoredTime(endTime)
            return start != null && end != null && start.before(end)
        } catch (e: Exception) {
            return false
        }
    }
    
    fun isValidTaskName(taskName: String): Boolean {
        return taskName.isNotBlank() && taskName.length <= 100
    }
    
    fun isValidDescription(description: String): Boolean {
        return description.length <= 500
    }
    
    fun getPasswordErrorMessage(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña no puede estar vacía"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }
    
    fun getEmailErrorMessage(email: String): String? {
        return when {
            email.isBlank() -> "El email no puede estar vacío"
            !isValidEmail(email) -> "El email no tiene un formato válido"
            else -> null
        }
    }
    
    fun getNameErrorMessage(name: String): String? {
        return when {
            name.isBlank() -> "El nombre no puede estar vacío"
            name.length < 2 -> "El nombre debe tener al menos 2 caracteres"
            else -> null
        }
    }
}