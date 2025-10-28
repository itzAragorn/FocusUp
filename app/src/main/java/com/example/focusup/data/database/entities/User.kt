package com.example.focusup.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val password: String, // En una app real, esto debería estar hasheado
    val name: String,
    val profileType: String, // "STUDENT" o "WORKER"
    val createdAt: Long = System.currentTimeMillis(),
    // Nuevos campos de perfil
    val profileImagePath: String? = null,
    val phoneNumber: String? = null,
    val bio: String? = null,
    val institution: String? = null, // Universidad/Empresa
    val position: String? = null, // Carrera/Cargo
    val birthDate: String? = null,
    // Configuración de seguridad
    val biometricEnabled: Boolean = false
)