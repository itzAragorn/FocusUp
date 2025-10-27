package com.example.focusup.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusup.presentation.viewmodels.StatsViewModel
import com.example.focusup.presentation.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel,
    userId: Long,
    onNavigateBack: () -> Unit
) {
    val uiState by statsViewModel.uiState.collectAsState()
    
    LaunchedEffect(userId) {
        statsViewModel.loadStats(userId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            LoadingState(message = "Cargando estadísticas...")
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Resumen del día
                item {
                    TodaySummaryCard(
                        tasksCompleted = uiState.todayStats?.tasksCompleted ?: 0,
                        pomodorosCompleted = uiState.todayStats?.pomodorosCompleted ?: 0,
                        studyTimeMinutes = uiState.todayStats?.studyTimeMinutes ?: 0,
                        completionRate = statsViewModel.getCompletionRate()
                    )
                }
                
                // Racha de días
                item {
                    StreakCard(
                        currentStreak = uiState.currentStreak,
                        longestStreak = uiState.longestStreak
                    )
                }
                
                // Gráfico semanal simple
                item {
                    WeeklyProgressCard(
                        weeklyStats = uiState.weeklyStats,
                        weeklyAverage = statsViewModel.getWeeklyAverage()
                    )
                }
                
                // Totales generales
                item {
                    TotalsCard(
                        totalTasks = uiState.totalTasksCompleted,
                        totalPomodoros = uiState.totalPomodoros,
                        totalStudyTime = statsViewModel.getTotalStudyTimeFormatted()
                    )
                }
            }
        }
    }
}

@Composable
private fun TodaySummaryCard(
    tasksCompleted: Int,
    pomodorosCompleted: Int,
    studyTimeMinutes: Int,
    completionRate: Float
) {
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
                text = "Hoy",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.CheckCircle,
                    value = tasksCompleted.toString(),
                    label = "Tareas"
                )
                StatItem(
                    icon = Icons.Default.Timer,
                    value = pomodorosCompleted.toString(),
                    label = "Pomodoros"
                )
                StatItem(
                    icon = Icons.Default.Schedule,
                    value = "${studyTimeMinutes}m",
                    label = "Estudio"
                )
            }
            
            if (completionRate > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Tasa de completado: ${String.format("%.0f", completionRate)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun StreakCard(
    currentStreak: Int,
    longestStreak: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = "Racha",
                    tint = Color(0xFFFF6B35),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Racha actual",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$currentStreak días consecutivos",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Racha más larga: $longestStreak días",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeeklyProgressCard(
    weeklyStats: List<com.example.focusup.data.database.entities.ProductivityStats>,
    weeklyAverage: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Esta semana",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (weeklyStats.isNotEmpty()) {
                val maxTasks = weeklyStats.maxOfOrNull { it.tasksCompleted } ?: 1
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(weeklyStats) { stat ->
                        WeekDayBar(
                            tasksCompleted = stat.tasksCompleted,
                            maxTasks = maxTasks,
                            date = stat.date
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Promedio: ${String.format("%.1f", weeklyAverage)} tareas/día",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "No hay datos para esta semana",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WeekDayBar(
    tasksCompleted: Int,
    maxTasks: Int,
    date: String
) {
    val heightFraction = if (maxTasks > 0) tasksCompleted.toFloat() / maxTasks.toFloat() else 0f
    val barHeight = (heightFraction * 100).dp.coerceAtLeast(4.dp)
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(100.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.primary
                            )
                        )
                    )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = tasksCompleted.toString(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = date.takeLast(2),
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TotalsCard(
    totalTasks: Int,
    totalPomodoros: Int,
    totalStudyTime: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Totales",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TotalItem(
                icon = Icons.Default.CheckCircle,
                label = "Tareas completadas",
                value = totalTasks.toString()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TotalItem(
                icon = Icons.Default.Timer,
                label = "Pomodoros completados",
                value = totalPomodoros.toString()
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TotalItem(
                icon = Icons.Default.Schedule,
                label = "Tiempo total de estudio",
                value = totalStudyTime
            )
        }
    }
}

@Composable
private fun TotalItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp
            )
        }
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
