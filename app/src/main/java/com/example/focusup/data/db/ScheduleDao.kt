package com.example.focusup.data.db

import androidx.room.*
import com.example.focusup.data.model.ScheduleItemEntity

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheduleItem(item: ScheduleItemEntity)

    @Update
    suspend fun updateScheduleItem(item: ScheduleItemEntity)

    @Delete
    suspend fun deleteScheduleItem(item: ScheduleItemEntity)

    @Query("SELECT * FROM schedule_items WHERE userId = :userId")
    suspend fun getScheduleByUser(userId: Int): List<ScheduleItemEntity>

}