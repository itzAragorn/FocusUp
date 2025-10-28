package com.example.focusup.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusup.data.database.entities.User
import com.example.focusup.data.database.entities.Task
import com.example.focusup.domain.model.ProfileType
import com.example.focusup.domain.model.TaskPriority
import com.example.focusup.presentation.viewmodels.HomeScreenViewModel
import com.example.focusup.presentation.components.EmptyStates
import com.example.focusup.presentation.components.PriorityBadge
import com.example.focusup.utils.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: User?,
    onLogout: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToPomodoro: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToTaskList: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    homeScreenViewModel: HomeScreenViewModel
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val uiState by homeScreenViewModel.uiState.collectAsState()
    
    // Load data when user changes
    LaunchedEffect(user?.id) {
        user?.id?.let { userId ->
            homeScreenViewModel.loadHomeData(userId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("FocusUp")
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
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
                    selected = true,
                    onClick = { }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Calendario") },
                    label = { Text("Calendario") },
                    selected = false,
                    onClick = onNavigateToCalendar
                )
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
                // Saludo personalizado
                WelcomeCard(user = user)
            }
            
            item {
                // Resumen del día
                TodaySummaryCard(
                    scheduleBlocksCount = homeScreenViewModel.getTodayScheduleBlocksCount(),
                    tasksCount = homeScreenViewModel.getTodayTasksCount(),
                    completedTasksCount = homeScreenViewModel.getCompletedTasksCount()
                )
            }
            
            item {
                // Acciones rápidas
                QuickActionsCard(
                    onNavigateToSchedule = onNavigateToSchedule,
                    onNavigateToCalendar = onNavigateToCalendar,
                    onNavigateToPomodoro = onNavigateToPomodoro,
                    onNavigateToStats = onNavigateToStats,
                    onNavigateToTaskList = onNavigateToTaskList,
                    profileType = user?.profileType
                )
            }
            
            item {
                // Próximas tareas
                UpcomingTasksCard(
                    tasks = uiState.upcomingTasks,
                    onNavigateToTaskList = onNavigateToTaskList
                )
            }
        }
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
}

@Composable
private fun WelcomeCard(user: User?) {
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
                text = "¡Hola, ${user?.name ?: "Usuario"}! 👋",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hoy es ${DateTimeUtils.formatDateForDisplay(DateTimeUtils.getCurrentDate())}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (user?.profileType == ProfileType.STUDENT.name) {
                    "Organiza tu día de estudios"
                } else {
                    "Organiza tu día de trabajo"
                },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun TodaySummaryCard(
    scheduleBlocksCount: Int,
    tasksCount: Int,
    completedTasksCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Resumen de hoy",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    icon = Icons.Default.List,
                    title = "Clases/Trabajo",
                    count = scheduleBlocksCount.toString()
                )
                SummaryItem(
                    icon = Icons.Default.List,
                    title = "Tareas",
                    count = tasksCount.toString()
                )
                SummaryItem(
                    icon = Icons.Default.CheckCircle,
                    title = "Completadas",
                    count = completedTasksCount.toString()
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    count: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = count,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun QuickActionsCard(
    onNavigateToSchedule: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToPomodoro: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToTaskList: () -> Unit = {},
    profileType: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Acciones rápidas",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            // Primera fila de botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToSchedule,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.List, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (profileType == ProfileType.STUDENT.name) "Ver clases" else "Ver horario"
                    )
                }
                
                OutlinedButton(
                    onClick = onNavigateToTaskList,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Nueva tarea")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Segunda fila de botones - Pomodoro y Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToPomodoro,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pomodoro")
                }
                
                OutlinedButton(
                    onClick = onNavigateToStats,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.BarChart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Estadísticas")
                }
            }
        }
    }
}

@Composable
private fun UpcomingTasksCard(
    tasks: List<Task>,
    onNavigateToTaskList: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Próximas tareas",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            if (tasks.isEmpty()) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                ) {
                    EmptyStates.NoTasks(onAddTask = onNavigateToTaskList)
                }
            } else {
                tasks.take(5).forEach { task ->
                    TaskPreviewItem(task = task)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = onNavigateToTaskList,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver todas las tareas")
            }
        }
    }
}

@Composable
private fun TaskPreviewItem(task: Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Schedule,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
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
            Text(
                text = "${DateTimeUtils.formatDateForDisplay(task.date)} • ${task.time}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}