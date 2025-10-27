package com.example.focusup.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.focusup.data.database.FocusUpDatabase
import com.example.focusup.data.repository.TaskRepository
import com.example.focusup.domain.manager.RecurrenceManager
import kotlinx.coroutines.flow.first

/**
 * Worker que se ejecuta periódicamente para mantener actualizada
 * la ventana de tareas recurrentes generadas
 */
class RecurrenceWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Obtener instancias necesarias
            val database = FocusUpDatabase.getDatabase(applicationContext)
            val taskRepository = TaskRepository(database.taskDao())
            val recurrenceManager = RecurrenceManager(taskRepository)
            
            // Obtener todas las tareas padre con recurrencia
            val recurringTasks = taskRepository.getRecurringParentTasks().first()
            
            // Para cada tarea, verificar y actualizar ventana de generación
            recurringTasks.forEach { parentTask ->
                recurrenceManager.generateRecurringTasks(parentTask, windowDays = 90)
            }
            
            Result.success()
        } catch (e: Exception) {
            // Si falla, reintentar más tarde
            Result.retry()
        }
    }
}
