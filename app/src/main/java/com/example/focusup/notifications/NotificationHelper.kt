package com.example.focusup.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.focusup.MainActivity
import com.example.focusup.R

class NotificationHelper(private val context: Context) {
    
    companion object {
        private const val CHANNEL_ID_TASKS = "task_reminders"
        private const val CHANNEL_ID_SCHEDULE = "schedule_blocks"
        private const val CHANNEL_ID_POMODORO = "pomodoro_timer"
        private const val CHANNEL_ID_GAMIFICATION = "gamification_rewards"
        private const val CHANNEL_NAME_TASKS = "Recordatorios de Tareas"
        private const val CHANNEL_NAME_SCHEDULE = "Bloques de Horario"
        private const val CHANNEL_NAME_POMODORO = "Temporizador Pomodoro"
        private const val CHANNEL_NAME_GAMIFICATION = "Recompensas y Logros"
        private const val CHANNEL_DESCRIPTION_TASKS = "Notificaciones para recordatorios de tareas"
        private const val CHANNEL_DESCRIPTION_SCHEDULE = "Notificaciones para bloques de horario"
        private const val CHANNEL_DESCRIPTION_POMODORO = "Notificaciones del temporizador Pomodoro"
        private const val CHANNEL_DESCRIPTION_GAMIFICATION = "Notificaciones de level up y achievements desbloqueados"
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal para tareas
            val taskChannel = NotificationChannel(
                CHANNEL_ID_TASKS,
                CHANNEL_NAME_TASKS,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION_TASKS
                enableVibration(true)
                enableLights(true)
            }
            
            // Canal para bloques de horario
            val scheduleChannel = NotificationChannel(
                CHANNEL_ID_SCHEDULE,
                CHANNEL_NAME_SCHEDULE,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION_SCHEDULE
                enableVibration(true)
            }
            
            // Canal para Pomodoro
            val pomodoroChannel = NotificationChannel(
                CHANNEL_ID_POMODORO,
                CHANNEL_NAME_POMODORO,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION_POMODORO
                enableVibration(true)
                enableLights(true)
            }
            
            // Canal para Gamificación
            val gamificationChannel = NotificationChannel(
                CHANNEL_ID_GAMIFICATION,
                CHANNEL_NAME_GAMIFICATION,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION_GAMIFICATION
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(taskChannel)
            notificationManager.createNotificationChannel(scheduleChannel)
            notificationManager.createNotificationChannel(pomodoroChannel)
            notificationManager.createNotificationChannel(gamificationChannel)
        }
    }
    
    fun showTaskReminder(taskId: Long, taskName: String, taskTime: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("taskId", taskId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TASKS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⏰ Recordatorio: $taskName")
            .setContentText("Tu tarea está programada para $taskTime")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Tu tarea \"$taskName\" está programada para las $taskTime. ¡No olvides completarla!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        NotificationManagerCompat.from(context).notify(taskId.toInt(), notification)
    }
    
    fun showScheduleBlockReminder(blockId: Long, blockName: String, startTime: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("scheduleBlockId", blockId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            blockId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_SCHEDULE)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Próximo bloque: $blockName")
            .setContentText("Comienza a las $startTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(blockId.toInt(), notification)
    }
    
    fun showPomodoroNotification(title: String, message: String, notificationId: Int = 1000) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_POMODORO)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
    
    fun showPomodoroProgressNotification(timeLeft: String, isWorking: Boolean) {
        val title = if (isWorking) "⏱️ Pomodoro en progreso" else "☕ Descanso en progreso"
        val message = "Tiempo restante: $timeLeft"
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_POMODORO)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(999, notification)
    }
    
    fun showUpcomingTaskNotification(taskId: Long, taskName: String, minutesUntil: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("taskId", taskId)
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            (taskId + 10000).toInt(), // Diferente ID para notificación previa
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_TASKS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🔔 Tarea próxima: $taskName")
            .setContentText("Comienza en $minutesUntil minutos. Prepárate para enfocarte 📝")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Tu tarea \"$taskName\" comienza en $minutesUntil minutos. Es un buen momento para prepararte."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        NotificationManagerCompat.from(context).notify((taskId + 10000).toInt(), notification)
    }
    
    fun cancelNotification(notificationId: Int) {
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
    
    fun showLevelUpNotification(newLevel: Int, xpGained: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            3000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_GAMIFICATION)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🎉 ¡LEVEL UP!")
            .setContentText("¡Has alcanzado el nivel $newLevel! (+$xpGained XP)")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("¡Increíble! Has subido al nivel $newLevel. Tu dedicación está dando frutos. ¡Sigue así! 🚀"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 300, 100, 300, 100, 300))
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            .build()
        
        NotificationManagerCompat.from(context).notify(3000, notification)
    }
    
    fun showAchievementUnlockedNotification(achievementName: String, xpReward: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            3001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_GAMIFICATION)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("🏆 ¡Logro Desbloqueado!")
            .setContentText("$achievementName (+$xpReward XP)")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("¡Felicidades! Has desbloqueado el logro \"$achievementName\". Tu constancia es admirable 💪"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 200, 100, 200))
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
            .build()
        
        NotificationManagerCompat.from(context).notify(3001, notification)
    }
    
    fun showXpGainedNotification(xpGained: Int, activity: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            3002,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_GAMIFICATION)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("⭐ ¡XP Ganado!")
            .setContentText("+$xpGained XP por $activity")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSilent(true)
            .build()
        
        NotificationManagerCompat.from(context).notify(3002, notification)
    }
    
    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }
}
