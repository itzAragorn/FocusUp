package com.example.focusup.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.focusup.data.database.dao.ScheduleBlockDao
import com.example.focusup.data.database.dao.TaskDao
import com.example.focusup.data.database.dao.UserDao
import com.example.focusup.data.database.dao.ProductivityStatsDao
import com.example.focusup.data.database.entities.ScheduleBlock
import com.example.focusup.data.database.entities.Task
import com.example.focusup.data.database.entities.User
import com.example.focusup.data.database.entities.ProductivityStats

@Database(
    entities = [User::class, ScheduleBlock::class, Task::class, ProductivityStats::class],
    version = 8,
    exportSchema = false
)
abstract class FocusUpDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun scheduleBlockDao(): ScheduleBlockDao
    abstract fun taskDao(): TaskDao
    abstract fun productivityStatsDao(): ProductivityStatsDao
    
    companion object {
        @Volatile
        private var INSTANCE: FocusUpDatabase? = null
        
        fun getDatabase(context: Context): FocusUpDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FocusUpDatabase::class.java,
                    "focusup_database"
                )
                    .fallbackToDestructiveMigration() // Recrear BD si hay cambios de esquema
                    .allowMainThreadQueries() // Solo para desarrollo, remover en producción
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}