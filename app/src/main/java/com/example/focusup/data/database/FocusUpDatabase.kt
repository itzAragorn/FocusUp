package com.example.focusup.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.focusup.data.database.dao.ScheduleBlockDao
import com.example.focusup.data.database.dao.TaskDao
import com.example.focusup.data.database.dao.UserDao
import com.example.focusup.data.database.dao.ProductivityStatsDao
import com.example.focusup.data.database.dao.DailyStatsDao
import com.example.focusup.data.database.dao.AchievementDao
import com.example.focusup.data.database.dao.UserProgressDao
import com.example.focusup.data.database.entities.ScheduleBlock
import com.example.focusup.data.database.entities.Task
import com.example.focusup.data.database.entities.User
import com.example.focusup.data.database.entities.ProductivityStats
import com.example.focusup.data.database.entities.DailyStats
import com.example.focusup.data.database.entities.Achievement
import com.example.focusup.data.database.entities.UserProgress

@Database(
    entities = [User::class, ScheduleBlock::class, Task::class, ProductivityStats::class, DailyStats::class, Achievement::class, UserProgress::class],
    version = 10,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FocusUpDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun scheduleBlockDao(): ScheduleBlockDao
    abstract fun taskDao(): TaskDao
    abstract fun productivityStatsDao(): ProductivityStatsDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun achievementDao(): AchievementDao
    abstract fun userProgressDao(): UserProgressDao
    
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

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType) ?: emptyList()
    }
}