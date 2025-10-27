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
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import com.example.focusup.data.database.entities.User
import com.example.focusup.data.database.entities.ScheduleBlock
import com.example.focusup.domain.model.ProfileType
import com.example.focusup.presentation.viewmodels.ScheduleScreenViewModel
import com.example.focusup.presentation.viewmodels.ScheduleViewModel
import com.example.focusup.presentation.components.EmptyStates

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    user: User?,
    onLogout: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onAddScheduleBlock: (Int) -> Unit = {},
    scheduleScreenViewModel: ScheduleScreenViewModel,
    scheduleViewModel: ScheduleViewModel
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteBlockDialog by remember { mutableStateOf(false) }
    var blockToDelete by remember { mutableStateOf<ScheduleBlock?>(null) }
    val uiState by scheduleScreenViewModel.uiState.collectAsState()
    
    // Load schedule blocks when user changes
    LaunchedEffect(user?.id) {
        user?.id?.let { userId ->
            scheduleScreenViewModel.loadScheduleBlocksForUser(userId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (user?.profileType == ProfileType.STUDENT.name) {
                            "Mis Clases"
                        } else {
                            "Mi Horario"
                        }
                    )
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
                    selected = true,
                    onClick = { }
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
                    selected = false,
                    onClick = onNavigateToCalendar
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAddScheduleBlock(1) }, // Default to Monday
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar bloque de horario")
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
                // Información del usuario
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
                            text = if (user?.profileType == ProfileType.STUDENT.name) {
                                "📚 Horario de Clases"
                            } else {
                                "💼 Horario de Trabajo"
                            },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (user?.profileType == ProfileType.STUDENT.name) {
                                "Organiza tus clases por día de la semana"
                            } else {
                                "Gestiona tu horario laboral semanal"
                            },
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            item {
                // Vista semanal con datos reales
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    WeeklyScheduleView(
                        profileType = user?.profileType,
                        scheduleBlocks = uiState.scheduleBlocks,
                        onAddScheduleBlock = onAddScheduleBlock,
                        onDeleteBlock = { block ->
                            blockToDelete = block
                            showDeleteBlockDialog = true
                        }
                    )
                }
            }
        }
    }
    
    // Diálogo de confirmación para eliminar bloque de horario
    if (showDeleteBlockDialog && blockToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteBlockDialog = false
                blockToDelete = null
            },
            title = { Text("Eliminar bloque de horario") },
            text = { 
                Text("¿Estás seguro de que quieres eliminar \"${blockToDelete!!.name}\"?") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        blockToDelete?.let { block ->
                            scheduleViewModel.deleteScheduleBlock(block)
                        }
                        showDeleteBlockDialog = false
                        blockToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteBlockDialog = false
                        blockToDelete = null
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
}

@Composable
private fun WeeklyScheduleView(
    profileType: String?,
    scheduleBlocks: List<ScheduleBlock>,
    onAddScheduleBlock: (Int) -> Unit,
    onDeleteBlock: (ScheduleBlock) -> Unit
) {
    val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Horario Semanal",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            daysOfWeek.forEachIndexed { index, day ->
                val dayBlocks = scheduleBlocks.filter { it.dayOfWeek == index + 1 }
                DayScheduleItem(
                    dayName = day,
                    dayNumber = index + 1,
                    scheduleBlocks = dayBlocks,
                    profileType = profileType,
                    onAddBlock = { onAddScheduleBlock(index + 1) },
                    onDeleteBlock = onDeleteBlock
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DayScheduleItem(
    dayName: String,
    dayNumber: Int,
    scheduleBlocks: List<ScheduleBlock>,
    profileType: String?,
    onAddBlock: () -> Unit,
    onDeleteBlock: (ScheduleBlock) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dayName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (scheduleBlocks.isEmpty()) {
                            "Sin actividades programadas"
                        } else {
                            "${scheduleBlocks.size} actividad${if (scheduleBlocks.size > 1) "es" else ""}"
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                OutlinedButton(
                    onClick = onAddBlock,
                    modifier = Modifier.size(width = 100.dp, height = 32.dp),
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Icon(
                        Icons.Default.Add, 
                        contentDescription = "Agregar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Agregar",
                        fontSize = 12.sp
                    )
                }
            }
            
            // Mostrar bloques de horario para este día
            if (scheduleBlocks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                scheduleBlocks.sortedBy { it.startTime }.forEach { block ->
                    ScheduleBlockItem(
                        scheduleBlock = block,
                        profileType = profileType,
                        onDeleteBlock = { onDeleteBlock(block) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun ScheduleBlockItem(
    scheduleBlock: ScheduleBlock,
    profileType: String?,
    onDeleteBlock: (ScheduleBlock) -> Unit = {}
) {
    val blockColor = try {
        Color(android.graphics.Color.parseColor(scheduleBlock.color))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(blockColor.copy(alpha = 0.1f))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(blockColor)
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = scheduleBlock.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "${scheduleBlock.startTime} - ${scheduleBlock.endTime}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (profileType == ProfileType.STUDENT.name) {
                scheduleBlock.professor?.let { professor ->
                    Text(
                        text = "Prof: $professor",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                scheduleBlock.classroom?.let { classroom ->
                    Text(
                        text = "Aula: $classroom",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        IconButton(
            onClick = { onDeleteBlock(scheduleBlock) }
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Eliminar bloque",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}