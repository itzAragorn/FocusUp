package com.example.focusup.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    
    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_NAME = "extra_task_name"
        const val EXTRA_TASK_TIME = "extra_task_time"
        const val EXTRA_BLOCK_ID = "extra_block_id"
        const val EXTRA_BLOCK_NAME = "extra_block_name"
        const val EXTRA_BLOCK_START_TIME = "extra_block_start_time"
        const val EXTRA_TYPE = "extra_type"
        const val EXTRA_MINUTES_UNTIL = "extra_minutes_until"
        
        const val TYPE_TASK = "task"
        const val TYPE_TASK_UPCOMING = "task_upcoming"
        const val TYPE_SCHEDULE_BLOCK = "schedule_block"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)
        val type = intent.getStringExtra(EXTRA_TYPE)
        
        when (type) {
            TYPE_TASK -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1)
                val taskName = intent.getStringExtra(EXTRA_TASK_NAME) ?: "Tarea"
                val taskTime = intent.getStringExtra(EXTRA_TASK_TIME) ?: ""
                
                if (taskId != -1L) {
                    notificationHelper.showTaskReminder(taskId, taskName, taskTime)
                }
            }
            TYPE_TASK_UPCOMING -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1)
                val taskName = intent.getStringExtra(EXTRA_TASK_NAME) ?: "Tarea"
                val minutesUntil = intent.getIntExtra(EXTRA_MINUTES_UNTIL, 15)
                
                if (taskId != -1L) {
                    notificationHelper.showUpcomingTaskNotification(taskId, taskName, minutesUntil)
                }
            }
            TYPE_SCHEDULE_BLOCK -> {
                val blockId = intent.getLongExtra(EXTRA_BLOCK_ID, -1)
                val blockName = intent.getStringExtra(EXTRA_BLOCK_NAME) ?: "Bloque"
                val startTime = intent.getStringExtra(EXTRA_BLOCK_START_TIME) ?: ""
                
                if (blockId != -1L) {
                    notificationHelper.showScheduleBlockReminder(blockId, blockName, startTime)
                }
            }
        }
    }
}
