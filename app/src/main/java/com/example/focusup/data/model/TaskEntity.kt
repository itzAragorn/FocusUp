package com.example.focusup.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val fecha: String, // formato "yyyy-MM-dd"
    val hora: String,
    val descripcion: String,
    val userId: Int
)