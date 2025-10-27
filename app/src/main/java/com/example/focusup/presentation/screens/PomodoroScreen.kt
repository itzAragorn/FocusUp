package com.example.focusup.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusup.domain.model.PomodoroState
import com.example.focusup.presentation.viewmodels.PomodoroViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    pomodoroViewModel: PomodoroViewModel,
    userId: Long,
    onNavigateBack: () -> Unit
) {
    val session by pomodoroViewModel.session.collectAsState()
    
    LaunchedEffect(userId) {
        pomodoroViewModel.setUserId(userId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Temporizador Pomodoro") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Estado actual
            StateIndicator(state = session.state)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Círculo de progreso con timer
            PomodoroCircle(
                progress = session.progress,
                timeText = session.getTimeFormatted(),
                state = session.state
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Contador de Pomodoros completados
            PomodoroCounter(completedPomodoros = session.completedPomodoros)
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Controles
            PomodoroControls(
                state = session.state,
                onStart = { pomodoroViewModel.startWork() },
                onPause = { pomodoroViewModel.pause() },
                onResume = { pomodoroViewModel.resume() },
                onReset = { pomodoroViewModel.reset() },
                onSkip = { pomodoroViewModel.skip() },
                onBreak = { pomodoroViewModel.startBreak() }
            )
        }
    }
}

@Composable
private fun StateIndicator(state: PomodoroState) {
    val (text, color) = when (state) {
        PomodoroState.IDLE -> "Listo para comenzar" to MaterialTheme.colorScheme.onBackground
        PomodoroState.WORK -> "¡Enfócate! 💪" to MaterialTheme.colorScheme.primary
        PomodoroState.SHORT_BREAK -> "Descanso corto ☕" to Color(0xFF4CAF50)
        PomodoroState.LONG_BREAK -> "Descanso largo 🎉" to Color(0xFF2196F3)
        PomodoroState.PAUSED -> "En pausa" to MaterialTheme.colorScheme.onSurfaceVariant
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun PomodoroCircle(
    progress: Float,
    timeText: String,
    state: PomodoroState
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "progress"
    )
    
    // Animación de pulsación cuando está activo
    val scale by rememberInfiniteTransition(label = "pulse").animateFloat(
        initialValue = 1f,
        targetValue = if (state == PomodoroState.WORK) 1.02f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val strokeWidth = 20.dp.toPx()
            
            // Círculo de fondo
            drawCircle(
                color = Color(0xFF2A2A2A),
                style = Stroke(width = strokeWidth)
            )
            
            // Círculo de progreso con gradiente morado
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF8B5CF6), // Morado eléctrico
                        Color(0xFFA78BFA), // Morado claro
                        Color(0xFF7C3AED)  // Morado oscuro
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }
        
        // Tiempo en el centro
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timeText,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            if (state != PomodoroState.IDLE) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = when (state) {
                        PomodoroState.WORK -> "Trabajando"
                        PomodoroState.SHORT_BREAK -> "Descanso corto"
                        PomodoroState.LONG_BREAK -> "Descanso largo"
                        PomodoroState.PAUSED -> "Pausado"
                        else -> ""
                    },
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PomodoroCounter(completedPomodoros: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Completados",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "$completedPomodoros Pomodoros completados hoy",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PomodoroControls(
    state: PomodoroState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit,
    onSkip: () -> Unit,
    onBreak: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botón principal
        when (state) {
            PomodoroState.IDLE -> {
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Comenzar Pomodoro", fontSize = 18.sp)
                }
            }
            PomodoroState.WORK, PomodoroState.SHORT_BREAK, PomodoroState.LONG_BREAK -> {
                Button(
                    onClick = onPause,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pausar", fontSize = 18.sp)
                }
            }
            PomodoroState.PAUSED -> {
                Button(
                    onClick = onResume,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continuar", fontSize = 18.sp)
                }
            }
        }
        
        // Botones secundarios
        if (state != PomodoroState.IDLE) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onSkip,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Saltar")
                }
                
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reiniciar")
                }
            }
        }
        
        // Botón de descanso manual
        if (state == PomodoroState.IDLE || state == PomodoroState.PAUSED) {
            TextButton(
                onClick = onBreak,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Coffee, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tomar descanso")
            }
        }
    }
}
