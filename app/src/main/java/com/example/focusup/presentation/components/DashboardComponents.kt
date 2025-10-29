package com.example.focusup.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusup.data.database.entities.DailyStats
import com.example.focusup.ui.theme.ElectricPurple
import com.example.focusup.ui.theme.DarkGraphite
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProductivityChart(
    weeklyStats: List<DailyStats>,
    modifier: Modifier = Modifier
) {
    val maxValue = weeklyStats.maxOfOrNull { it.productivityScore } ?: 100f
    val chartColor = ElectricPurple
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkGraphite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📈 Productividad Semanal",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ElectricPurple
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                drawProductivityChart(
                    stats = weeklyStats,
                    maxValue = maxValue,
                    chartColor = chartColor
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Days labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawProductivityChart(
    stats: List<DailyStats>,
    maxValue: Float,
    chartColor: Color
) {
        val width = size.width
        val height = size.height
        val padding = 32f
        
        val chartWidth = width - padding * 2
        val chartHeight = height - padding * 2
        
        // Draw background grid
        val gridColor = Color.White.copy(alpha = 0.1f)
        for (i in 0..4) {
            val y = padding + (chartHeight * i / 4)
            drawLine(
                color = gridColor,
                start = Offset(padding, y),
                end = Offset(width - padding, y),
                strokeWidth = 1.dp.toPx()
            )
        }
    
    // Draw chart line
    if (stats.size >= 2) {
        val path = Path()
        val points = mutableListOf<Offset>()
        
        stats.forEachIndexed { index, stat ->
            val x = padding + (chartWidth * index / (stats.size - 1).coerceAtLeast(1))
            val normalizedValue = if (maxValue > 0) stat.productivityScore / maxValue else 0f
            val y = padding + chartHeight - (chartHeight * normalizedValue)
            
            points.add(Offset(x, y))
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        // Draw the line
        drawPath(
            path = path,
            color = chartColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        
        // Draw points
        points.forEach { point ->
            drawCircle(
                color = chartColor,
                radius = 4.dp.toPx(),
                center = point
            )
        }
        
        // Draw gradient under the line
        val gradientPath = Path().apply {
            addPath(path)
            lineTo(points.last().x, padding + chartHeight)
            lineTo(points.first().x, padding + chartHeight)
            close()
        }
        
        val gradient = Brush.verticalGradient(
            colors = listOf(
                chartColor.copy(alpha = 0.3f),
                Color.Transparent
            ),
            startY = padding,
            endY = padding + chartHeight
        )
        
        drawPath(
            path = gradientPath,
            brush = gradient
        )
    }
}

@Composable
fun CircularProgressIndicator(
    current: Float,
    goal: Float,
    label: String,
    modifier: Modifier = Modifier,
    color: Color = ElectricPurple
) {
    val progress = if (goal > 0) (current / goal).coerceIn(0f, 1f) else 0f
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkGraphite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)
                    
                    // Background circle
                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.3f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )
                    
                    // Progress arc
                    drawArc(
                        color = color,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                        size = Size(size.width - strokeWidth, size.height - strokeWidth)
                    )
                }
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StreakCounter(
    currentStreak: Int,
    maxStreak: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkGraphite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fire icon with streak animation
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(ElectricPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🔥",
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Racha Actual",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "$currentStreak días",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ElectricPurple
                )
                
                if (maxStreak > currentStreak) {
                    Text(
                        text = "Récord: $maxStreak días",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Progress indicator for streak milestones
            val nextMilestone = when {
                currentStreak < 7 -> 7
                currentStreak < 30 -> 30
                currentStreak < 100 -> 100
                else -> (currentStreak / 100 + 1) * 100
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Siguiente: $nextMilestone",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                LinearProgressIndicator(
                    progress = (currentStreak.toFloat() / nextMilestone.toFloat()).coerceIn(0f, 1f),
                    modifier = Modifier.width(60.dp),
                    color = ElectricPurple,
                    trackColor = ElectricPurple.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: String,
    color: Color = ElectricPurple,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkGraphite),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 32.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}