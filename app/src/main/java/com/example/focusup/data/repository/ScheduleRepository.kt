package com.example.focusup.data.repository

import com.example.focusup.data.database.dao.ScheduleBlockDao
import com.example.focusup.data.database.entities.ScheduleBlock
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(
    private val scheduleBlockDao: ScheduleBlockDao
) {
    
    fun getScheduleBlocksByUser(userId: Long): Flow<List<ScheduleBlock>> {
        return scheduleBlockDao.getScheduleBlocksByUser(userId)
    }
    
    fun getScheduleBlocksByUserAndDay(userId: Long, dayOfWeek: Int): Flow<List<ScheduleBlock>> {
        return scheduleBlockDao.getScheduleBlocksByUserAndDay(userId, dayOfWeek)
    }
    
    suspend fun getScheduleBlockById(id: Long): ScheduleBlock? {
        return scheduleBlockDao.getScheduleBlockById(id)
    }
    
    suspend fun insertScheduleBlock(scheduleBlock: ScheduleBlock): Long {
        return scheduleBlockDao.insertScheduleBlock(scheduleBlock)
    }
    
    suspend fun updateScheduleBlock(scheduleBlock: ScheduleBlock) {
        scheduleBlockDao.updateScheduleBlock(scheduleBlock)
    }
    
    suspend fun deleteScheduleBlock(scheduleBlock: ScheduleBlock) {
        scheduleBlockDao.deleteScheduleBlock(scheduleBlock)
    }
    
    suspend fun deleteAllScheduleBlocksByUser(userId: Long) {
        scheduleBlockDao.deleteAllScheduleBlocksByUser(userId)
    }
}