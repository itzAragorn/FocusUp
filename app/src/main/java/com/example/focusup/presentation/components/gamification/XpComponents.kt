package com.example.focusup.presentation.components.gamification

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusup.data.repository.UserProgressDisplay
import com.example.focusup.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun XpProgressCard(
    userProgress: UserProgressDisplay?,
    modifier: Modifier = Modifier,
    showXpGainAnimation: Boolean = false,
    xpGained: Int = 0,
    onAnimationComplete: () -> Unit = {}
) {
    var showGainedXp by remember { mutableStateOf(false) }
    
    LaunchedEffect(showXpGainAnimation) {
        if (showXpGainAnimation && xpGained > 0) {
            showGainedXp = true
            delay(2000) // Mostrar por 2 segundos
            showGainedXp = false
            onAnimationComplete()
        }
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            ElectricPurple.copy(alpha = 0.1f),
                            VibrantBlue.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            if (userProgress != null) {
                Column {
                    // Header con nivel y XP
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = ElectricPurple,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = "Nivel ${userProgress.currentLevel}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkGray
                            )
                        }
                        
                        // XP actual
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(20.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            Text(
                                text = "${userProgress.currentXp}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElectricPurple
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Barra de progreso XP
                    XpProgressBar(
                        currentXp = userProgress.currentXp,
                        xpForCurrentLevel = userProgress.xpForCurrentLevel,
                        xpForNextLevel = userProgress.xpForNextLevel,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Texto de progreso
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${userProgress.currentXp - userProgress.xpForCurrentLevel} / ${userProgress.xpForNextLevel - userProgress.xpForCurrentLevel} XP",
                            fontSize = 12.sp,
                            color = MediumGray
                        )
                        
                        Text(
                            text = "Nivel ${userProgress.currentLevel + 1}",
                            fontSize = 12.sp,
                            color = MediumGray
                        )
                    }
                }
                
            } else {
                // Loading state
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = ElectricPurple
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "Cargando progreso...",
                            fontSize = 16.sp,
                            color = MediumGray
                        )
                    }
                }
            }
            
        }
        
        // Animación de XP ganado - fuera del Card
        if (showGainedXp) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                XpGainedAnimation(xpGained = xpGained)
            }
        }
    }
}

@Composable
fun XpProgressBar(
    currentXp: Int,
    xpForCurrentLevel: Int,
    xpForNextLevel: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (xpForNextLevel > xpForCurrentLevel) {
        (currentXp - xpForCurrentLevel).toFloat() / (xpForNextLevel - xpForCurrentLevel).toFloat()
    } else {
        1f
    }
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = EaseOutCubic)
    )
    
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                color = LightGray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                ElectricPurple,
                                VibrantBlue
                            )
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
fun XpGainedAnimation(
    xpGained: Int,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    
    val offsetY by animateDpAsState(
        targetValue = if (visible) (-40).dp else 0.dp,
        animationSpec = tween(durationMillis = 1500, easing = EaseOutCubic)
    )
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 0f else 1f,
        animationSpec = tween(durationMillis = 1500, delayMillis = 500)
    )
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    Card(
        modifier = modifier.offset(y = offsetY),
        colors = CardDefaults.cardColors(
            containerColor = ElectricPurple
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = "+$xpGained XP",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun LevelProgressIndicator(
    currentLevel: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = ElectricPurple
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            
            Spacer(modifier = Modifier.width(6.dp))
            
            Text(
                text = "Nv. $currentLevel",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun AchievementNotificationBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    if (count > 0) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFD700) // Gold
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                
                if (count > 1) {
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "$count",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}