package com.example.focusup.data.database.dao

import androidx.room.*
import com.example.focusup.data.database.entities.ScheduleBlock
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleBlockDao {
    
    @Query("SELECT * FROM schedule_blocks WHERE userId = :userId ORDER BY dayOfWeek, startTime")
    fun getScheduleBlocksByUser(userId: Long): Flow<List<ScheduleBlock>>
    
    @Query("SELECT * FROM schedule_blocks WHERE userId = :userId AND dayOfWeek = :dayOfWeek ORDER BY startTime")
    fun getScheduleBlocksByUserAndDay(userId: Long, dayOfWeek: Int): Flow<List<ScheduleBlock>>
    
    @Query("SELECT * FROM schedule_blocks WHERE id = :id LIMIT 1")
    suspend fun getScheduleBlockById(id: Long): ScheduleBlock?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleBlock(scheduleBlock: ScheduleBlock): Long
    
    @Update
    suspend fun updateScheduleBlock(scheduleBlock: ScheduleBlock)
    
    @Delete
    suspend fun deleteScheduleBlock(scheduleBlock: ScheduleBlock)
    
    @Query("DELETE FROM schedule_blocks WHERE userId = :userId")
    suspend fun deleteAllScheduleBlocksByUser(userId: Long)
}