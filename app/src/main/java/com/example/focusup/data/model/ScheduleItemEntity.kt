package com.example.focusup.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_items")
data class ScheduleItemEntity (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: String, // "trabajo" o "estudio"
    val nombre: String,
    val color: String,
    val horaInicio: String,
    val horaFin: String,
    val descripcion: String,
    val profesor: String? = null,
    val sala: String? = null,
    val userId: Int
)