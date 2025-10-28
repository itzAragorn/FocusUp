package com.example.focusup.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    fun scheduleTaskReminder(
        taskId: Long,
        taskName: String,
        taskDate: String,
        taskTime: String,
        minutesBefore: Int = 15
    ) {
        // Alarma principal (a la hora exacta)
        val mainIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TYPE, AlarmReceiver.TYPE_TASK)
            putExtra(AlarmReceiver.EXTRA_TASK_ID, taskId)
            putExtra(AlarmReceiver.EXTRA_TASK_NAME, taskName)
            putExtra(AlarmReceiver.EXTRA_TASK_TIME, taskTime)
        }
        
        val mainPendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Alarma anticipada (15 minutos antes)
        val upcomingIntent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TYPE, AlarmReceiver.TYPE_TASK_UPCOMING)
            putExtra(AlarmReceiver.EXTRA_TASK_ID, taskId)
            putExtra(AlarmReceiver.EXTRA_TASK_NAME, taskName)
            putExtra(AlarmReceiver.EXTRA_MINUTES_UNTIL, minutesBefore)
        }
        
        val upcomingPendingIntent = PendingIntent.getBroadcast(
            context,
            (taskId + 10000).toInt(), // ID diferente para la alarma anticipada
            upcomingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Parsear fecha y hora
        val triggerTime = parseDateTime(taskDate, taskTime)
        
        // Programar alarma principal
        val mainCalendar = Calendar.getInstance().apply {
            timeInMillis = triggerTime
        }
        
        if (mainCalendar.timeInMillis > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    mainCalendar.timeInMillis,
                    mainPendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    mainCalendar.timeInMillis,
                    mainPendingIntent
                )
            }
        }
        
        // Programar alarma anticipada (15 minutos antes)
        val upcomingCalendar = Calendar.getInstance().apply {
            timeInMillis = triggerTime
            add(Calendar.MINUTE, -minutesBefore)
        }
        
        if (upcomingCalendar.timeInMillis > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    upcomingCalendar.timeInMillis,
                    upcomingPendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    upcomingCalendar.timeInMillis,
                    upcomingPendingIntent
                )
            }
        }
    }
    
    fun scheduleScheduleBlockReminder(
        blockId: Long,
        blockName: String,
        dayOfWeek: Int,
        startTime: String,
        minutesBefore: Int = 10
    ) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TYPE, AlarmReceiver.TYPE_SCHEDULE_BLOCK)
            putExtra(AlarmReceiver.EXTRA_BLOCK_ID, blockId)
            putExtra(AlarmReceiver.EXTRA_BLOCK_NAME, blockName)
            putExtra(AlarmReceiver.EXTRA_BLOCK_START_TIME, startTime)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            blockId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val calendar = getNextOccurrence(dayOfWeek, startTime)
        calendar.add(Calendar.MINUTE, -minutesBefore)
        
        // Programar alarma recurrente (semanal)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7, // Repetir cada semana
            pendingIntent
        )
    }
    
    fun cancelReminder(id: Long) {
        // Cancelar alarma principal
        val mainIntent = Intent(context, AlarmReceiver::class.java)
        val mainPendingIntent = PendingIntent.getBroadcast(
            context,
            id.toInt(),
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(mainPendingIntent)
        
        // Cancelar alarma anticipada
        val upcomingIntent = Intent(context, AlarmReceiver::class.java)
        val upcomingPendingIntent = PendingIntent.getBroadcast(
            context,
            (id + 10000).toInt(),
            upcomingIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(upcomingPendingIntent)
    }
    
    private fun parseDateTime(date: String, time: String): Long {
        val calendar = Calendar.getInstance()
        
        // Parsear fecha (formato: yyyy-MM-dd)
        val dateParts = date.split("-")
        if (dateParts.size == 3) {
            calendar.set(Calendar.YEAR, dateParts[0].toInt())
            calendar.set(Calendar.MONTH, dateParts[1].toInt() - 1) // Mes es 0-based
            calendar.set(Calendar.DAY_OF_MONTH, dateParts[2].toInt())
        }
        
        // Parsear hora (formato: HH:mm)
        val timeParts = time.split(":")
        if (timeParts.size == 2) {
            calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            calendar.set(Calendar.MINUTE, timeParts[1].toInt())
            calendar.set(Calendar.SECOND, 0)
        }
        
        return calendar.timeInMillis
    }
    
    private fun getNextOccurrence(dayOfWeek: Int, time: String): Calendar {
        val calendar = Calendar.getInstance()
        
        // Parsear hora
        val timeParts = time.split(":")
        if (timeParts.size == 2) {
            calendar.set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            calendar.set(Calendar.MINUTE, timeParts[1].toInt())
            calendar.set(Calendar.SECOND, 0)
        }
        
        // Ajustar al próximo día de la semana especificado
        // dayOfWeek: 1=Lunes, 2=Martes, ..., 7=Domingo
        // Calendar.DAY_OF_WEEK: 1=Domingo, 2=Lunes, ..., 7=Sábado
        val targetDayOfWeek = if (dayOfWeek == 7) Calendar.SUNDAY else dayOfWeek + 1
        
        while (calendar.get(Calendar.DAY_OF_WEEK) != targetDayOfWeek ||
               calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        
        return calendar
    }
}
