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
import com.example.focusup.ui.theme.ElectricPurple

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tabs para diferentes períodos de análisis
                var selectedTab by remember { mutableIntStateOf(0) }
                val tabs = listOf("Semana", "Mes", "Año", "Todo")
                
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = ElectricPurple
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
                
                // Contenido según la pestaña seleccionada
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> { // Semana
                            item {
                                WeeklyAnalysisSection(
                                    uiState = uiState,
                                    statsViewModel = statsViewModel
                                )
                            }
                        }
                        1 -> { // Mes
                            item {
                                MonthlyAnalysisSection(
                                    uiState = uiState,
                                    statsViewModel = statsViewModel
                                )
                            }
                        }
                        2 -> { // Año
                            item {
                                YearlyAnalysisSection(
                                    uiState = uiState,
                                    statsViewModel = statsViewModel
                                )
                            }
                        }
                        3 -> { // Todo
                            item {
                                AllTimeAnalysisSection(
                                    uiState = uiState,
                                    statsViewModel = statsViewModel
                                )
                            }
                        }
                    }
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

@Composable
private fun WeeklyAnalysisSection(
    uiState: com.example.focusup.presentation.viewmodels.StatsUiState,
    statsViewModel: StatsViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Header
        AnalysisHeader(
            title = "Análisis Semanal",
            subtitle = "Tu productividad de los últimos 7 días"
        )
        
        // Gráfico mejorado
        WeeklyProgressCard(
            weeklyStats = uiState.weeklyStats,
            weeklyAverage = statsViewModel.getWeeklyAverage()
        )
        
        // Métricas clave
        WeeklyMetricsGrid(uiState = uiState)
        
        // Comparativa con semana anterior
        WeekComparisonCard(uiState = uiState)
    }
}

@Composable
private fun MonthlyAnalysisSection(
    uiState: com.example.focusup.presentation.viewmodels.StatsUiState,
    statsViewModel: StatsViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AnalysisHeader(
            title = "Análisis Mensual", 
            subtitle = "Tendencias y patrones del mes"
        )
        
        // Placeholder para gráfico mensual
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("📈 Gráfico mensual próximamente", style = MaterialTheme.typography.bodyLarge)
            }
        }
        
        // Totales del mes
        MonthlyTotalsCard(uiState = uiState)
    }
}

@Composable
private fun YearlyAnalysisSection(
    uiState: com.example.focusup.presentation.viewmodels.StatsUiState,
    statsViewModel: StatsViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AnalysisHeader(
            title = "Análisis Anual",
            subtitle = "Tu progreso durante todo el año"
        )
        
        // Placeholder para métricas anuales
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("🎯 Resumen Anual", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Función disponible próximamente con más datos históricos", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun AllTimeAnalysisSection(
    uiState: com.example.focusup.presentation.viewmodels.StatsUiState,
    statsViewModel: StatsViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AnalysisHeader(
            title = "Estadísticas Totales",
            subtitle = "Todo tu historial de productividad"
        )
        
        // Totales generales mejorados
        TotalsCard(
            totalTasks = uiState.totalTasksCompleted,
            totalPomodoros = uiState.totalPomodoros,
            totalStudyTime = statsViewModel.getTotalStudyTimeFormatted()
        )
        
        // Récords personales
        PersonalRecordsCard(uiState = uiState)
        
        // Racha histórica
        StreakCard(
            currentStreak = uiState.currentStreak,
            longestStreak = uiState.longestStreak
        )
    }
}

@Composable
private fun AnalysisHeader(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = ElectricPurple
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun WeeklyMetricsGrid(uiState: com.example.focusup.presentation.viewmodels.StatsUiState) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(4) { index ->
            val (title, value, icon) = when(index) {
                0 -> Triple("Promedio diario", "${uiState.weeklyStats.sumOf { it.studyTimeMinutes } / 7} min", Icons.Default.Schedule)
                1 -> Triple("Mejor día", "${uiState.weeklyStats.maxByOrNull { it.studyTimeMinutes }?.studyTimeMinutes ?: 0} min", Icons.Default.Star)
                2 -> Triple("Días activos", "${uiState.weeklyStats.count { it.studyTimeMinutes > 0 }}/7", Icons.Default.CheckCircle)
                else -> Triple("Consistency", "${if(uiState.weeklyStats.count { it.studyTimeMinutes > 0 } >= 5) "Alta" else "Media"}", Icons.Default.TrendingUp)
            }
            
            MetricCard(
                title = title,
                value = value,
                icon = icon
            )
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier.width(140.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ElectricPurple,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ElectricPurple
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun WeekComparisonCard(uiState: com.example.focusup.presentation.viewmodels.StatsUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📊 Comparativa Semanal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Función disponible próximamente - compararemos tu productividad con semanas anteriores",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun MonthlyTotalsCard(uiState: com.example.focusup.presentation.viewmodels.StatsUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "📈 Totales del Mes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Próximamente: estadísticas detalladas del mes actual y comparativas",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun PersonalRecordsCard(uiState: com.example.focusup.presentation.viewmodels.StatsUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ElectricPurple.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🏆 Récords Personales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ElectricPurple
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Récord de racha
            RecordItem(
                title = "Racha más larga",
                value = "${uiState.longestStreak} días",
                icon = "🔥"
            )
            
            RecordItem(
                title = "Total de Pomodoros",
                value = "${uiState.totalPomodoros}",
                icon = "🍅"
            )
            
            RecordItem(
                title = "Tareas completadas",
                value = "${uiState.totalTasksCompleted}",
                icon = "✅"
            )
        }
    }
}

@Composable
private fun RecordItem(title: String, value: String, icon: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = icon, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ElectricPurple
        )
    }
}
