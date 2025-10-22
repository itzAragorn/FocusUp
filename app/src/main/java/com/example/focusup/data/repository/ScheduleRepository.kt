package com.example.focusup.data.repository

import com.example.focusup.data.db.ScheduleDao
import com.example.focusup.data.model.ScheduleItemEntity

class ScheduleRepository(private val scheduleDao: ScheduleDao) {

    suspend fun addScheduleItem(item: ScheduleItemEntity) {
        scheduleDao.insertScheduleItem(item)
    }

    suspend fun updateScheduleItem(item: ScheduleItemEntity) {
        scheduleDao.updateScheduleItem(item)
    }

    suspend fun deleteScheduleItem(item: ScheduleItemEntity) {
        scheduleDao.deleteScheduleItem(item)
    }

    suspend fun getScheduleByUser(userId: Int): List<ScheduleItemEntity> {
        return scheduleDao.getScheduleByUser(userId)
    }

}