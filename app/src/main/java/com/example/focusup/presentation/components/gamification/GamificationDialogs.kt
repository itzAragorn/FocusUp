package com.example.focusup.presentation.components.gamification

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.focusup.data.database.entities.Achievement
import com.example.focusup.data.database.entities.AchievementType
import com.example.focusup.data.repository.LevelUpResult
import com.example.focusup.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelUpDialog(
    levelUpResult: LevelUpResult,
    onDismiss: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header con gradiente
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    ElectricPurple,
                                    VibrantBlue
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icono animado
                        AnimatedLevelIcon()
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "¡LEVEL UP!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Información del nivel
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Felicidades!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Has alcanzado el nivel ${levelUpResult.newLevel}",
                        fontSize = 16.sp,
                        color = MediumGray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Stats del nivel
                    LevelStatsRow(
                        oldLevel = levelUpResult.previousLevel,
                        newLevel = levelUpResult.newLevel,
                        xpGained = levelUpResult.xpGained
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Botón de continuar
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricPurple
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "¡Continuar!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementUnlockedDialog(
    achievements: List<Achievement>,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    var showContent by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
    }
    
    if (achievements.isEmpty()) return
    
    val currentAchievement = achievements[currentIndex]
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header con gradiente dorado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700), // Gold
                                    Color(0xFFB8860B)  // Dark Gold
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Icono del achievement
                        AnimatedAchievementIcon(AchievementType.valueOf(currentAchievement.achievementType))
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "¡LOGRO DESBLOQUEADO!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Información del achievement
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = currentAchievement.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = currentAchievement.description,
                        fontSize = 14.sp,
                        color = MediumGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // XP ganado
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = BackgroundLight
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = "+${currentAchievement.xpReward} XP",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElectricPurple
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (achievements.size > 1) {
                            // Indicador de progreso
                            Text(
                                text = "${currentIndex + 1} de ${achievements.size}",
                                fontSize = 12.sp,
                                color = MediumGray,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            if (currentIndex < achievements.size - 1) {
                                Button(
                                    onClick = { currentIndex++ },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ElectricPurple
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Siguiente", color = Color.White)
                                }
                            } else {
                                Button(
                                    onClick = onDismiss,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = ElectricPurple
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("¡Genial!", color = Color.White)
                                }
                            }
                        } else {
                            Button(
                                onClick = onDismiss,
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricPurple
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "¡Genial!",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
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
private fun AnimatedLevelIcon() {
    val infiniteTransition = rememberInfiniteTransition()
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        )
    )
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .background(
                color = Color.White.copy(alpha = 0.2f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun AnimatedAchievementIcon(achievementType: AchievementType) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    val icon = when (achievementType) {
        AchievementType.FIRST_POMODORO -> Icons.Default.Timer
        AchievementType.FIRST_TASK -> Icons.Default.CheckCircle
        AchievementType.FIRST_DAY -> Icons.Default.Star
        AchievementType.STREAK_3, AchievementType.STREAK_7, AchievementType.STREAK_30, AchievementType.STREAK_100 -> Icons.Default.Whatshot
        AchievementType.POMODORO_10, AchievementType.POMODORO_50, AchievementType.POMODORO_100, AchievementType.POMODORO_500 -> Icons.Default.Timer
        AchievementType.TASK_MASTER_25, AchievementType.TASK_MASTER_100, AchievementType.TASK_MASTER_500 -> Icons.Default.CheckCircle
        AchievementType.PERFECT_DAY -> Icons.Default.StarRate
        AchievementType.EARLY_BIRD -> Icons.Default.WbSunny
        AchievementType.NIGHT_OWL -> Icons.Default.NightsStay
        AchievementType.SPEED_DEMON -> Icons.Default.FlashOn
        AchievementType.TIME_WARRIOR_10H, AchievementType.TIME_WARRIOR_50H, AchievementType.TIME_WARRIOR_100H -> Icons.Default.Schedule
    }
    
    Box(
        modifier = Modifier
            .size(56.dp)
            .scale(scale)
            .background(
                color = Color.White.copy(alpha = 0.2f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Composable
private fun LevelStatsRow(
    oldLevel: Int,
    newLevel: Int,
    xpGained: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nivel anterior
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nivel $oldLevel",
                fontSize = 12.sp,
                color = MediumGray
            )
            Text(
                text = "$oldLevel",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = LightGray
            )
        }
        
        // Flecha
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = ElectricPurple,
            modifier = Modifier.size(32.dp)
        )
        
        // Nivel nuevo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nivel $newLevel",
                fontSize = 12.sp,
                color = ElectricPurple,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$newLevel",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricPurple
            )
        }
        
        // XP ganado
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "XP Ganado",
                fontSize = 12.sp,
                color = MediumGray
            )
            Text(
                text = "+$xpGained",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD700)
            )
        }
    }
}