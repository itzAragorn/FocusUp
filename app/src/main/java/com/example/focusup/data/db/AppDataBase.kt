package com.example.focusup.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.focusup.data.model.*

@Database(
    entities = [UserEntity::class, ScheduleItemEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun scheduleDao(): ScheduleDao
    abstract fun taskDao(): TaskDao
}