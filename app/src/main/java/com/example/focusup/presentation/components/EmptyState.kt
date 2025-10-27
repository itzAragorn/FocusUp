package com.example.focusup.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Animación de fade in
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "emptyStateAlpha"
    )
    
    // Animación de flotación del ícono
    val infiniteTransition = rememberInfiniteTransition(label = "iconFloat")
    val iconOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconFloatOffset"
    )
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .alpha(alpha)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .offset(y = iconOffset.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(text = actionText)
            }
        }
    }
}

// Estados vacíos predefinidos para usar en toda la app
object EmptyStates {
    @Composable
    fun NoTasks(onAddTask: () -> Unit) {
        EmptyState(
            icon = Icons.Default.CheckCircle,
            title = "Sin tareas pendientes",
            description = "¡Genial! No tienes tareas por hacer. Agrega una nueva para comenzar a organizar tu día.",
            actionText = "Agregar Tarea",
            onActionClick = onAddTask
        )
    }
    
    @Composable
    fun NoScheduleBlocks(onAddBlock: () -> Unit) {
        EmptyState(
            icon = Icons.Default.DateRange,
            title = "Sin bloques programados",
            description = "Crea bloques de tiempo para organizar tu día y mejorar tu productividad.",
            actionText = "Crear Bloque",
            onActionClick = onAddBlock
        )
    }
    
    @Composable
    fun NoCalendarEvents() {
        EmptyState(
            icon = Icons.Default.Event,
            title = "Sin eventos este día",
            description = "No hay tareas ni bloques programados para esta fecha. Selecciona otro día o agrega algo nuevo.",
            actionText = null,
            onActionClick = null
        )
    }
    
    @Composable
    fun NoSearchResults() {
        EmptyState(
            icon = Icons.Default.Search,
            title = "Sin resultados",
            description = "No encontramos nada que coincida con tu búsqueda. Intenta con otros términos.",
            actionText = null,
            onActionClick = null
        )
    }
}
