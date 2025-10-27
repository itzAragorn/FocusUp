package com.example.focusup.domain.manager

import com.example.focusup.data.database.entities.Task
import com.example.focusup.data.repository.TaskRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class RecurrenceManager(
    private val taskRepository: TaskRepository
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    /**
     * Genera todas las tareas recurrentes para una tarea padre
     * @param parentTask Tarea original con recurrencia configurada
     * @param windowDays Número de días hacia adelante para generar (default: 90 días)
     */
    suspend fun generateRecurringTasks(parentTask: Task, windowDays: Int = 90) {
        if (parentTask.recurrenceType == "NONE") return
        
        // Verificar que no se hayan generado ya
        val existingChildren = taskRepository.getChildTasks(parentTask.id).first()
        if (existingChildren.isNotEmpty()) {
            // Ya existen tareas hijas, actualizar ventana si es necesario
            updateRecurrenceWindow(parentTask, windowDays)
            return
        }
        
        when (parentTask.recurrenceType) {
            "DAILY" -> generateDailyTasks(parentTask, windowDays)
            "WEEKLY" -> generateWeeklyTasks(parentTask, windowDays)
            "MONTHLY" -> generateMonthlyTasks(parentTask, windowDays)
        }
    }
    
    /**
     * Genera tareas diarias
     */
    private suspend fun generateDailyTasks(parentTask: Task, windowDays: Int) {
        val startDate = parseDate(parentTask.date) ?: return
        val endDate = calculateEndDate(parentTask, startDate, windowDays)
        
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        calendar.add(Calendar.DAY_OF_MONTH, 1) // Comenzar desde el día siguiente
        
        val tasksToInsert = mutableListOf<Task>()
        
        while (calendar.time <= endDate) {
            val newTask = parentTask.copy(
                id = 0, // Room generará nuevo ID
                date = formatDate(calendar.time),
                parentTaskId = parentTask.id,
                isCompleted = false,
                createdAt = System.currentTimeMillis()
            )
            tasksToInsert.add(newTask)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        // Insertar en lote para mejor rendimiento
        tasksToInsert.forEach { task ->
            taskRepository.insertTask(task)
        }
    }
    
    /**
     * Genera tareas semanales (mismo día de la semana)
     */
    private suspend fun generateWeeklyTasks(parentTask: Task, windowDays: Int) {
        val startDate = parseDate(parentTask.date) ?: return
        val endDate = calculateEndDate(parentTask, startDate, windowDays)
        
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        calendar.add(Calendar.WEEK_OF_YEAR, 1) // Comenzar desde la siguiente semana
        
        val tasksToInsert = mutableListOf<Task>()
        
        while (calendar.time <= endDate) {
            val newTask = parentTask.copy(
                id = 0,
                date = formatDate(calendar.time),
                parentTaskId = parentTask.id,
                isCompleted = false,
                createdAt = System.currentTimeMillis()
            )
            tasksToInsert.add(newTask)
            calendar.add(Calendar.WEEK_OF_YEAR, 1)
        }
        
        tasksToInsert.forEach { task ->
            taskRepository.insertTask(task)
        }
    }
    
    /**
     * Genera tareas mensuales (mismo día del mes)
     */
    private suspend fun generateMonthlyTasks(parentTask: Task, windowDays: Int) {
        val startDate = parseDate(parentTask.date) ?: return
        val endDate = calculateEndDate(parentTask, startDate, windowDays)
        
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.add(Calendar.MONTH, 1) // Comenzar desde el siguiente mes
        
        val tasksToInsert = mutableListOf<Task>()
        
        while (calendar.time <= endDate) {
            // Ajustar por meses con menos días (ej: 31 en febrero)
            val maxDayOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            calendar.set(Calendar.DAY_OF_MONTH, minOf(dayOfMonth, maxDayOfMonth))
            
            val newTask = parentTask.copy(
                id = 0,
                date = formatDate(calendar.time),
                parentTaskId = parentTask.id,
                isCompleted = false,
                createdAt = System.currentTimeMillis()
            )
            tasksToInsert.add(newTask)
            calendar.add(Calendar.MONTH, 1)
        }
        
        tasksToInsert.forEach { task ->
            taskRepository.insertTask(task)
        }
    }
    
    /**
     * Actualiza la ventana de recurrencia, generando más tareas si es necesario
     */
    private suspend fun updateRecurrenceWindow(parentTask: Task, windowDays: Int) {
        val children = taskRepository.getChildTasks(parentTask.id).first()
        if (children.isEmpty()) return
        
        // Obtener la última tarea generada
        val lastChild = children.maxByOrNull { it.date } ?: return
        val lastDate = parseDate(lastChild.date) ?: return
        
        val today = Calendar.getInstance().time
        val daysUntilLast = ((lastDate.time - today.time) / (1000 * 60 * 60 * 24)).toInt()
        
        // Si quedan menos de 30 días, generar más
        if (daysUntilLast < 30) {
            generateRecurringTasks(parentTask, windowDays)
        }
    }
    
    /**
     * Elimina toda la serie de tareas recurrentes
     */
    suspend fun deleteRecurringSeries(taskId: Long) {
        val task = taskRepository.getTaskById(taskId) ?: return
        
        // Si es una tarea hija, obtener la tarea padre
        val parentId = task.parentTaskId ?: task.id
        
        // Eliminar todas las tareas hijas
        val children = taskRepository.getChildTasks(parentId).first()
        children.forEach { childTask ->
            taskRepository.deleteTask(childTask)
        }
        
        // Eliminar la tarea padre
        val parentTask = taskRepository.getTaskById(parentId)
        parentTask?.let {
            taskRepository.deleteTask(it)
        }
    }
    
    /**
     * Actualiza toda la serie de tareas recurrentes futuras
     */
    suspend fun updateRecurringSeries(
        taskId: Long,
        updateAction: (Task) -> Task
    ) {
        val task = taskRepository.getTaskById(taskId) ?: return
        val parentId = task.parentTaskId ?: task.id
        
        // Obtener todas las tareas futuras (incluyendo la actual si no está completada)
        val today = formatDate(Date())
        val children = taskRepository.getChildTasks(parentId).first()
        
        val futureTasks = children.filter { childTask ->
            childTask.date >= today && !childTask.isCompleted
        }
        
        // Actualizar cada tarea futura
        futureTasks.forEach { futureTask ->
            val updatedTask = updateAction(futureTask)
            taskRepository.updateTask(updatedTask)
        }
        
        // También actualizar la tarea padre si aplica
        if (task.parentTaskId == null && !task.isCompleted) {
            val updatedParent = updateAction(task)
            taskRepository.updateTask(updatedParent)
        }
    }
    
    /**
     * Verifica si una tarea es parte de una serie recurrente
     */
    suspend fun isRecurringTask(taskId: Long): Boolean {
        val task = taskRepository.getTaskById(taskId) ?: return false
        return task.recurrenceType != "NONE" || task.parentTaskId != null
    }
    
    /**
     * Obtiene la tarea padre de una serie recurrente
     */
    suspend fun getParentTask(taskId: Long): Task? {
        val task = taskRepository.getTaskById(taskId) ?: return null
        return if (task.parentTaskId != null) {
            taskRepository.getTaskById(task.parentTaskId)
        } else {
            task
        }
    }
    
    // Utilidades
    
    private fun parseDate(dateString: String): Date? {
        return try {
            dateFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun formatDate(date: Date): String {
        return dateFormat.format(date)
    }
    
    private fun calculateEndDate(parentTask: Task, startDate: Date, windowDays: Int): Date {
        val calendar = Calendar.getInstance()
        
        // Si hay fecha de fin de recurrencia, usarla
        parentTask.recurrenceEndDate?.let { endDateString ->
            parseDate(endDateString)?.let { endDate ->
                // Usar la fecha más cercana entre endDate y startDate + windowDays
                calendar.time = startDate
                calendar.add(Calendar.DAY_OF_MONTH, windowDays)
                val windowEndDate = calendar.time
                
                return if (endDate.before(windowEndDate)) endDate else windowEndDate
            }
        }
        
        // Si no hay fecha de fin, usar ventana de días
        calendar.time = startDate
        calendar.add(Calendar.DAY_OF_MONTH, windowDays)
        return calendar.time
    }
}
