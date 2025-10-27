package com.example.focusup.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.focusup.data.database.entities.User
import com.example.focusup.data.database.entities.Task
import com.example.focusup.domain.model.TaskPriority
import com.example.focusup.presentation.components.PriorityBadge
import com.example.focusup.utils.DateTimeUtils
import com.example.focusup.presentation.viewmodels.CalendarScreenViewModel
import com.example.focusup.presentation.viewmodels.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    user: User?,
    onLogout: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onAddTask: (String) -> Unit = {},
    calendarScreenViewModel: CalendarScreenViewModel,
    taskViewModel: TaskViewModel
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var showDeleteTaskDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var showCompleteTaskDialog by remember { mutableStateOf(false) }
    var taskToComplete by remember { mutableStateOf<Task?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }
    val uiState by calendarScreenViewModel.uiState.collectAsState()
    
    // Load tasks when user changes or refresh is triggered (NOT when date changes)
    LaunchedEffect(user?.id, refreshTrigger) {
        user?.id?.let { userId ->
            calendarScreenViewModel.loadTasksForUser(userId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Calendario y Tareas")
                },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = "Horarios") },
                    label = { Text("Horarios") },
                    selected = false,
                    onClick = onNavigateToSchedule
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = false,
                    onClick = onNavigateToHome
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendario") },
                    label = { Text("Calendario") },
                    selected = true,
                    onClick = { }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    val selectedDateString = DateTimeUtils.formatDateForStorage(selectedDate.time)
                    onAddTask(selectedDateString)
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Información del mes actual
                MonthHeader(selectedDate = selectedDate.time)
            }
            
            item {
                // Vista del calendario
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    CalendarGrid(
                        selectedDate = selectedDate,
                        tasks = uiState.tasks,
                        onDateSelected = { date -> selectedDate = date },
                        onMonthChanged = { date -> selectedDate = date }
                    )
                }
            }
            
            item {
                // Tareas del día seleccionado
                val selectedDateString = DateTimeUtils.formatDateForStorage(selectedDate.time)
                val tasksForDay = uiState.tasks.filter { it.date == selectedDateString }
                TasksForSelectedDay(
                    selectedDate = selectedDate.time,
                    tasks = tasksForDay,
                    onAddTask = onAddTask,
                    taskViewModel = taskViewModel,
                    onDeleteTask = { task ->
                        taskToDelete = task
                        showDeleteTaskDialog = true
                    },
                    onCompleteTask = { task ->
                        taskToComplete = task
                        showCompleteTaskDialog = true
                    },
                    onTaskUpdated = {
                        // Forzar actualización
                        refreshTrigger++
                    }
                )
            }
        }
    }
    
    // Diálogo de confirmación para eliminar tarea
    if (showDeleteTaskDialog && taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteTaskDialog = false
                taskToDelete = null
            },
            title = { Text("Eliminar tarea") },
            text = { 
                Text("¿Estás seguro de que quieres eliminar la tarea \"${taskToDelete!!.name}\"?") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToDelete?.let { task ->
                            taskViewModel.deleteTask(task.id)
                            // Solo forzar actualización global si es necesario
                            refreshTrigger++
                        }
                        showDeleteTaskDialog = false
                        taskToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteTaskDialog = false
                        taskToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de confirmación para logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de confirmación para completar tarea
    if (showCompleteTaskDialog && taskToComplete != null) {
        AlertDialog(
            onDismissRequest = { 
                showCompleteTaskDialog = false
                taskToComplete = null
            },
            title = { Text("Completar tarea") },
            text = { 
                Text("¿Estás seguro de que quieres marcar como completada la tarea \"${taskToComplete!!.name}\"?") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToComplete?.let { task ->
                            taskViewModel.toggleTaskCompletion(task.id)
                            // Solo forzar actualización global si es necesario
                            refreshTrigger++
                        }
                        showCompleteTaskDialog = false
                        taskToComplete = null
                    }
                ) {
                    Text("Completar", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showCompleteTaskDialog = false
                        taskToComplete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun MonthHeader(selectedDate: Date) {
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📅 ${getMonthYearInSpanish(calendar)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gestiona tus tareas y eventos",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    selectedDate: Calendar,
    tasks: List<Task>,
    onDateSelected: (Calendar) -> Unit,
    onMonthChanged: (Calendar) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate.time
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    // Configurar fechas límite: 3 meses atrás y 9 meses adelante
    val currentCalendar = Calendar.getInstance()
    
    // Fecha mínima: 3 meses atrás
    val minCalendar = Calendar.getInstance().apply {
        add(Calendar.MONTH, -3)
    }
    
    // Fecha máxima: 9 meses adelante
    val maxCalendar = Calendar.getInstance().apply {
        add(Calendar.MONTH, 9)
    }
    
    // Verificar si se puede navegar hacia atrás o adelante
    val canNavigateBack = selectedDate.get(Calendar.YEAR) > minCalendar.get(Calendar.YEAR) ||
            (selectedDate.get(Calendar.YEAR) == minCalendar.get(Calendar.YEAR) && 
             selectedDate.get(Calendar.MONTH) > minCalendar.get(Calendar.MONTH))
             
    val canNavigateForward = selectedDate.get(Calendar.YEAR) < maxCalendar.get(Calendar.YEAR) ||
            (selectedDate.get(Calendar.YEAR) == maxCalendar.get(Calendar.YEAR) && 
             selectedDate.get(Calendar.MONTH) < maxCalendar.get(Calendar.MONTH))

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header con navegación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (canNavigateBack) {
                            val newDate = Calendar.getInstance().apply {
                                time = selectedDate.time
                                add(Calendar.MONTH, -1)
                            }
                            onMonthChanged(newDate)
                        }
                    },
                    enabled = canNavigateBack
                ) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Mes anterior",
                        tint = if (canNavigateBack) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
                
                Text(
                    text = getMonthYearInSpanish(selectedDate),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                IconButton(
                    onClick = {
                        if (canNavigateForward) {
                            val newDate = Calendar.getInstance().apply {
                                time = selectedDate.time
                                add(Calendar.MONTH, 1)
                            }
                            onMonthChanged(newDate)
                        }
                    },
                    enabled = canNavigateForward
                ) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Mes siguiente",
                        tint = if (canNavigateForward) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Encabezados de días de la semana
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val dayHeaders = listOf("D", "L", "M", "X", "J", "V", "S")
                dayHeaders.forEach { day ->
                    Text(
                        text = day,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Grid del calendario
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.heightIn(max = 320.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Espacios vacíos para el inicio del mes
                items(firstDayOfWeek) {
                    Box(modifier = Modifier.aspectRatio(1f))
                }
                
                // Días del mes
                items(daysInMonth) { day ->
                    val dayCalendar = Calendar.getInstance()
                    dayCalendar.time = selectedDate.time
                    dayCalendar.set(Calendar.DAY_OF_MONTH, day + 1)
                    
                    val isToday = dayCalendar.get(Calendar.DAY_OF_YEAR) == 
                                 Calendar.getInstance().get(Calendar.DAY_OF_YEAR) &&
                                 dayCalendar.get(Calendar.YEAR) == 
                                 Calendar.getInstance().get(Calendar.YEAR)
                    
                    val isSelected = dayCalendar.get(Calendar.DAY_OF_MONTH) == 
                                    selectedDate.get(Calendar.DAY_OF_MONTH)
                    
                    // Verificar si hay tareas para este día
                    val dayDateString = DateTimeUtils.formatDateForStorage(dayCalendar.time)
                    val hasEvents = tasks.any { it.date == dayDateString }
                    
                    CalendarDayItem(
                        day = day + 1,
                        isToday = isToday,
                        isSelected = isSelected,
                        hasEvents = hasEvents,
                        onClick = { onDateSelected(dayCalendar) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarDayItem(
    day: Int,
    isToday: Boolean,
    isSelected: Boolean,
    hasEvents: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected || isToday) 4.dp else 1.dp
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = day.toString(),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                        color = when {
                            isSelected -> MaterialTheme.colorScheme.onPrimary
                            isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    if (hasEvents) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(
                                    color = when {
                                        isSelected -> MaterialTheme.colorScheme.onPrimary
                                        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
                                        else -> MaterialTheme.colorScheme.primary
                                    },
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TasksForSelectedDay(
    selectedDate: Date,
    tasks: List<Task>,
    onAddTask: (String) -> Unit,
    taskViewModel: TaskViewModel,
    onDeleteTask: (Task) -> Unit,
    onCompleteTask: (Task) -> Unit = {},
    onTaskUpdated: () -> Unit = {}
) {
    // Estado local para forzar recomposición solo de esta sección
    var localRefreshTrigger by remember { mutableStateOf(0) }
    var localTasks by remember { mutableStateOf(tasks) }
    
    // Actualizar las tareas locales cuando cambien las tareas o la fecha
    LaunchedEffect(tasks, selectedDate, localRefreshTrigger) {
        localTasks = tasks.filter { !it.isCompleted }
    }
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tareas para ${formatDateInSpanish(selectedDate)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                
                IconButton(onClick = { 
                    val dateString = DateTimeUtils.formatDateForStorage(selectedDate)
                    onAddTask(dateString)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Mostrar tareas del día
            if (tasks.isEmpty()) {
                // Sin tareas para este día
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hay tareas para este día",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(onClick = { 
                            val dateString = DateTimeUtils.formatDateForStorage(selectedDate)
                            onAddTask(dateString)
                        }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Agregar primera tarea")
                        }
                    }
                }
            } else if (localTasks.isEmpty()) {
                // Todas las tareas están completadas
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¡Todas las tareas completadas!",
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // Mostrar tareas pendientes
                localTasks.sortedBy { it.time }.forEach { task ->
                TaskItem(
                    task = task,
                    onToggleComplete = { taskId ->
                        // Actualizar inmediatamente la lista local
                        localTasks = localTasks.filter { it.id != task.id }
                        localRefreshTrigger++
                        onCompleteTask(task)
                    },
                    onDeleteTask = { taskId ->
                        // Actualizar inmediatamente la lista local
                        localTasks = localTasks.filter { it.id != task.id }
                        localRefreshTrigger++
                        onDeleteTask(task)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun TaskItem(
    task: Task,
    onToggleComplete: (Long) -> Unit = {},
    onDeleteTask: (Long) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    
                    // Priority badge
                    if (task.priority != "NONE") {
                        val priority = try {
                            TaskPriority.valueOf(task.priority)
                        } catch (e: Exception) {
                            TaskPriority.NONE
                        }
                        if (priority != TaskPriority.NONE) {
                            PriorityBadge(priority = priority, showLabel = false)
                        }
                    }
                }

                task.description?.let { description ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.time,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Row {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleComplete(task.id) }
                )
                IconButton(
                    onClick = { onDeleteTask(task.id) }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar tarea",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private fun getMonthYearInSpanish(calendar: Calendar): String {
    val months = arrayOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    
    val month = months[calendar.get(Calendar.MONTH)]
    val year = calendar.get(Calendar.YEAR)
    
    return "$month $year"
}

private fun formatDateInSpanish(date: Date): String {
    val calendar = Calendar.getInstance()
    calendar.time = date
    
    val months = arrayOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = months[calendar.get(Calendar.MONTH)]
    val year = calendar.get(Calendar.YEAR)
    
    return "$day de $month de $year"
}