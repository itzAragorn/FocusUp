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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.focusup.data.database.entities.User
import com.example.focusup.presentation.components.*
import com.example.focusup.presentation.components.gamification.*
import com.example.focusup.presentation.viewmodels.DashboardViewModel
import com.example.focusup.presentation.viewmodels.DashboardUiState
import com.example.focusup.presentation.viewmodels.GamificationViewModel
import com.example.focusup.ui.theme.ElectricPurple
import com.example.focusup.ui.theme.DarkGraphite
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    user: User?,
    onNavigateBack: () -> Unit,
    onNavigateToPomodoro: () -> Unit = {},
    onNavigateToTasks: () -> Unit = {},
    onNavigateToSchedule: () -> Unit = {},
    onNavigateToAchievement: () -> Unit = {},
    dashboardViewModel: DashboardViewModel = viewModel(
        factory = DashboardViewModel.Factory(user?.id ?: 0L)
    ),
    gamificationViewModel: GamificationViewModel? = null
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val gamificationUiState by gamificationViewModel?.uiState?.collectAsState() ?: remember { mutableStateOf(null) }
    var showGoalDialog by remember { mutableStateOf(false) }
    var showXpAnimation by remember { mutableStateOf(false) }
    var lastXpGained by remember { mutableStateOf(0) }
    
    LaunchedEffect(user?.id) {
        user?.id?.let { userId ->
            dashboardViewModel.loadDashboardData()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "📊 Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getGreeting() + ", ${user?.name ?: "Usuario"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showGoalDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Configurar objetivo")
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = ElectricPurple)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Welcome Header
                item {
                    WelcomeHeader(user = user)
                }
                
                // Gamification Progress - XP y Nivel en tiempo real
                gamificationUiState?.let { state ->
                    item {
                        XpProgressCard(
                            userProgress = state.userProgress,
                            showXpGainAnimation = showXpAnimation,
                            xpGained = lastXpGained,
                            onAnimationComplete = { 
                                showXpAnimation = false 
                                lastXpGained = 0
                            }
                        )
                    }
                }
                
                // Quick Actions - Prominente al inicio
                item {
                    QuickActionsSection(
                        onNavigateToPomodoro = onNavigateToPomodoro,
                        onNavigateToTasks = onNavigateToTasks,
                        onNavigateToSchedule = onNavigateToSchedule,
                        onNavigateToAchievement = onNavigateToAchievement
                    )
                }
                
                // Today's Progress - Enfoque en el día actual
                item {
                    TodayProgressSection(
                        uiState = uiState
                    )
                }
                
                // Current Streak - Motivación
                item {
                    MotivationalStreakSection(
                        currentStreak = uiState.currentStreak,
                        maxStreak = uiState.maxStreak
                    )
                }
                
                // Today's Overview - Compacto
                item {
                    TodayOverviewSection(
                        uiState = uiState,
                        onNavigateToPomodoro = onNavigateToPomodoro,
                        onNavigateToTasks = onNavigateToTasks
                    )
                }
                
                // Weekly Progress - Solo resumen
                item {
                    WeeklyProgressSection(
                        uiState = uiState,
                        onSetGoal = { showGoalDialog = true }
                    )
                }
                
                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
        
        // Error handling
        uiState.errorMessage?.let { error ->
            LaunchedEffect(error) {
                // Could show a snackbar here
                dashboardViewModel.clearError()
            }
        }
        
        // Gamification Dialogs
        gamificationUiState?.let { state ->
            // Level Up Dialog
            if (state.showLevelUpDialog && state.pendingLevelUp != null) {
                LevelUpDialog(
                    levelUpResult = state.pendingLevelUp,
                    onDismiss = { gamificationViewModel?.dismissLevelUpDialog() }
                )
            }
            
            // Achievement Unlocked Dialog
            if (state.showAchievementDialog && state.pendingAchievementUnlocks.isNotEmpty()) {
                AchievementUnlockedDialog(
                    achievements = state.pendingAchievementUnlocks,
                    onDismiss = { gamificationViewModel?.dismissAchievementDialog() }
                )
            }
        }
    }
    
    // Goal Setting Dialog
    if (showGoalDialog) {
        WeeklyGoalDialog(
            currentGoal = uiState.weeklyGoal,
            onGoalSet = { goal ->
                dashboardViewModel.setWeeklyGoal(goal)
                showGoalDialog = false
            },
            onDismiss = { showGoalDialog = false }
        )
    }
}

@Composable
private fun TodayOverviewSection(
    uiState: com.example.focusup.presentation.viewmodels.DashboardUiState,
    onNavigateToPomodoro: () -> Unit,
    onNavigateToTasks: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkGraphite),
        shape = RoundedCornerShape(16.dp)
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
                    text = "🌟 Hoy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ElectricPurple
                )
                
                Text(
                    text = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Today's stats in a row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Pomodoros",
                    value = "${uiState.todayStats?.pomodoroSessionsCompleted ?: 0}",
                    icon = "🍅",
                    modifier = Modifier.weight(1f)
                )
                
                StatCard(
                    title = "Tareas",
                    value = "${uiState.todayStats?.tasksCompleted ?: 0}",
                    icon = "✅",
                    modifier = Modifier.weight(1f)
                )
                
                StatCard(
                    title = "Tiempo",
                    value = "${uiState.todayStats?.totalFocusTimeMinutes ?: 0}m",
                    icon = "⏱️",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Productivity score
            LinearProgressIndicator(
                progress = (uiState.todayProductivityScore / 100f).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth(),
                color = ElectricPurple,
                trackColor = ElectricPurple.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Productividad: ${uiState.todayProductivityScore.toInt()}% - ${getProductivityLevel(uiState.todayProductivityScore)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun WeeklyProgressSection(
    uiState: com.example.focusup.presentation.viewmodels.DashboardUiState,
    onSetGoal: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkGraphite),
        shape = RoundedCornerShape(16.dp)
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
                    text = "🎯 Objetivo Semanal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ElectricPurple
                )
                
                TextButton(onClick = onSetGoal) {
                    Text("Cambiar", color = ElectricPurple)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Progress indicator
            LinearProgressIndicator(
                progress = (uiState.weeklyProgress / 100f).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth(),
                color = ElectricPurple,
                trackColor = ElectricPurple.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${uiState.weeklyFocusTime} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ElectricPurple,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "${uiState.weeklyGoal} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = "${uiState.weeklyProgress.toInt()}% completado esta semana",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickStatsSection(
    uiState: com.example.focusup.presentation.viewmodels.DashboardUiState
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        item {
            StatCard(
                title = "Esta Semana",
                value = "${uiState.weeklyTasksCompleted}",
                subtitle = "tareas completadas",
                icon = "📋",
                modifier = Modifier.width(120.dp)
            )
        }
        
        item {
            StatCard(
                title = "Promedio",
                value = "${uiState.weeklyProductivityAverage.toInt()}%",
                subtitle = "productividad",
                icon = "📊",
                modifier = Modifier.width(120.dp)
            )
        }
        
        item {
            StatCard(
                title = "Total",
                value = "${uiState.weeklyFocusTime / 60}h",
                subtitle = "tiempo enfocado",
                icon = "🎯",
                modifier = Modifier.width(120.dp)
            )
        }
    }
}

@Composable
private fun WeeklySummarySection(
    uiState: com.example.focusup.presentation.viewmodels.DashboardUiState,
    dashboardViewModel: DashboardViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkGraphite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📈 Resumen Semanal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ElectricPurple
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val summary = buildString {
                append("Has completado ${uiState.weeklyTasksCompleted} tareas ")
                append("y enfocado durante ${dashboardViewModel.getFormattedFocusTime(uiState.weeklyFocusTime)} esta semana. ")
                
                when {
                    uiState.weeklyProgress >= 100f -> append("¡Felicidades! Has superado tu objetivo. 🎉")
                    uiState.weeklyProgress >= 80f -> append("¡Muy cerca de tu objetivo! 💪")
                    uiState.weeklyProgress >= 50f -> append("Vas por buen camino. 👍")
                    else -> append("Aún puedes alcanzar tu objetivo. ¡Sigue así! 🚀")
                }
            }
            
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onNavigateToPomodoro: () -> Unit,
    onNavigateToTasks: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    onNavigateToAchievement: () -> Unit
) {
    Column {
        Text(
            text = "⚡ Acciones Rápidas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = ElectricPurple,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElevatedButton(
                onClick = onNavigateToPomodoro,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = ElectricPurple,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🍅",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pomodoro",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
            
            ElevatedButton(
                onClick = onNavigateToTasks,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = ElectricPurple,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📝",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Tareas",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
            
            ElevatedButton(
                onClick = onNavigateToSchedule,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = ElectricPurple,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📅",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Horario",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Segunda fila de botones
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElevatedButton(
                onClick = onNavigateToAchievement,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = Color(0xFFFF9800), // Color dorado para logros
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🏆",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Logros",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
            
            // Botón placeholder para mantener simetría
            Spacer(modifier = Modifier.weight(2f))
        }
    }
}

@Composable
private fun WeeklyGoalDialog(
    currentGoal: Int,
    onGoalSet: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var goalText by remember { mutableStateOf((currentGoal / 60).toString()) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("🎯 Objetivo Semanal")
        },
        text = {
            Column {
                Text(
                    text = "Establece tu objetivo de tiempo enfocado para esta semana:",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = goalText,
                    onValueChange = { goalText = it },
                    label = { Text("Horas por semana") },
                    suffix = { Text("horas") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val hours = goalText.toIntOrNull() ?: 5
                    onGoalSet(hours * 60) // Convert to minutes
                }
            ) {
                Text("Establecer", color = ElectricPurple)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Buenos días"
        in 12..17 -> "Buenas tardes"
        else -> "Buenas noches"
    }
}

private fun getProductivityLevel(score: Float): String {
    return when {
        score >= 90f -> "Excepcional 🌟"
        score >= 80f -> "Excelente 🚀"
        score >= 70f -> "Muy bueno 💪"
        score >= 60f -> "Bueno 👍"
        score >= 40f -> "Regular 📈"
        else -> "Puedes mejorar 💡"
    }
}

@Composable
private fun WelcomeHeader(user: User?) {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        currentHour < 12 -> "Buenos días"
        currentHour < 18 -> "Buenas tardes"
        else -> "Buenas noches"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ElectricPurple.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "$greeting, ${user?.name ?: "Usuario"}! 👋",
                style = MaterialTheme.typography.headlineSmall,
                color = ElectricPurple,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "¿Listo para ser productivo hoy?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun TodayProgressSection(uiState: DashboardUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progreso de hoy",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${uiState.todayProductivityScore.toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    color = ElectricPurple,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Barra de progreso del día
            LinearProgressIndicator(
                progress = { uiState.todayProductivityScore / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = ElectricPurple,
                trackColor = ElectricPurple.copy(alpha = 0.2f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = getMotivationalMessage(uiState.todayProductivityScore),
                style = MaterialTheme.typography.bodyMedium,
                color = ElectricPurple,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun MotivationalStreakSection(
    currentStreak: Int,
    maxStreak: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (currentStreak > 0) 
                ElectricPurple.copy(alpha = 0.1f) 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (currentStreak > 0) "¡Racha activa! 🔥" else "Inicia tu racha 💪",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (currentStreak > 0) ElectricPurple else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (currentStreak > 0) 
                        "$currentStreak días consecutivos" 
                    else 
                        "Completa un pomodoro hoy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (maxStreak > currentStreak) {
                    Text(
                        text = "Récord: $maxStreak días",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            // Icono grande de la racha
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        if (currentStreak > 0) ElectricPurple.copy(alpha = 0.2f) else Color.Transparent,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (currentStreak > 0) "🔥" else "💤",
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }
    }
}

private fun getMotivationalMessage(score: Float): String {
    return when {
        score >= 90f -> "¡Increíble! Estás en fuego hoy 🔥"
        score >= 70f -> "¡Excelente trabajo! Sigue así 💪"
        score >= 50f -> "Buen progreso, ¡puedes lograr más! 📈"
        score >= 30f -> "¡Vamos! El día apenas comienza 🚀"
        else -> "¡Hora de comenzar! Tu futuro yo te lo agradecerá ✨"
    }
}