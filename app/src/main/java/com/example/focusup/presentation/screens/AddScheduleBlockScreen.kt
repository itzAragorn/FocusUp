package com.example.focusup.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.example.focusup.data.database.entities.User
import com.example.focusup.domain.model.ProfileType
import com.example.focusup.presentation.viewmodels.ScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScheduleBlockScreen(
    user: User?,
    selectedDay: Int = 1,
    onScheduleBlockCreated: () -> Unit,
    onNavigateBack: () -> Unit,
    scheduleViewModel: ScheduleViewModel
) {
    var blockName by remember { mutableStateOf("") }
    var blockDescription by remember { mutableStateOf("") }
    var selectedDayState by remember { mutableStateOf(selectedDay) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("09:00") }
    var selectedColor by remember { mutableStateOf("#1976D2") }
    
    // Campos específicos para estudiantes
    var professor by remember { mutableStateOf("") }
    var classroom by remember { mutableStateOf("") }
    
    var showDayPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    
    val uiState by scheduleViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val isStudent = user?.profileType == ProfileType.STUDENT.name
    
    // Handle UI state changes
    LaunchedEffect(uiState.isScheduleBlockSaved) {
        if (uiState.isScheduleBlockSaved) {
            onScheduleBlockCreated()
        }
    }
    
    // Show error message if needed
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // TODO: Show snackbar or toast with error message
            println("Error saving schedule block: $error")
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (isStudent) "Nueva Clase" else "Nuevo Bloque Horario") 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
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
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = if (isStudent) "📚 Agregar Clase" else "📅 Agregar Bloque Horario",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = if (isStudent) 
                            "Registra tus clases con profesor y sala" 
                        else 
                            "Organiza tu horario de trabajo semanal",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
            
            // Formulario
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Nombre del bloque/clase
                    OutlinedTextField(
                        value = blockName,
                        onValueChange = { blockName = it },
                        label = { Text(if (isStudent) "Nombre de la asignatura" else "Nombre de la actividad") },
                        leadingIcon = {
                            Icon(Icons.Default.School, contentDescription = null)
                        },
                        placeholder = { 
                            Text(if (isStudent) "Ej: Matemáticas" else "Ej: Reunión de equipo") 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Descripción
                    OutlinedTextField(
                        value = blockDescription,
                        onValueChange = { blockDescription = it },
                        label = { Text("Descripción (opcional)") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        maxLines = 3
                    )
                    
                    // Campos específicos para estudiantes
                    if (isStudent) {
                        OutlinedTextField(
                            value = professor,
                            onValueChange = { professor = it },
                            label = { Text("Profesor") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null)
                            },
                            placeholder = { Text("Nombre del profesor") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        
                        OutlinedTextField(
                            value = classroom,
                            onValueChange = { classroom = it },
                            label = { Text("Sala/Aula") },
                            leadingIcon = {
                                Icon(Icons.Default.Room, contentDescription = null)
                            },
                            placeholder = { Text("Número de sala o ubicación") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    
                    Divider()
                    
                    // Día de la semana
                    OutlinedTextField(
                        value = getDayName(selectedDayState),
                        onValueChange = { },
                        label = { Text("Día de la semana") },
                        leadingIcon = {
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showDayPicker = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Seleccionar día")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )
                    
                    // Horario
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Hora inicio") },
                            leadingIcon = {
                                Icon(Icons.Default.AccessTime, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showStartTimePicker = true }) {
                                    Icon(Icons.Default.Schedule, contentDescription = "Seleccionar hora")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            readOnly = true
                        )
                        
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("Hora fin") },
                            leadingIcon = {
                                Icon(Icons.Default.AccessTime, contentDescription = null)
                            },
                            trailingIcon = {
                                IconButton(onClick = { showEndTimePicker = true }) {
                                    Icon(Icons.Default.Schedule, contentDescription = "Seleccionar hora")
                                }
                            },
                            modifier = Modifier.weight(1f),
                            readOnly = true
                        )
                    }
                    
                    // Selector de color
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Color de identificación",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = Color(android.graphics.Color.parseColor(selectedColor)),
                                    shape = CircleShape
                                )
                                .clickable { showColorPicker = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Cambiar color",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancelar")
                }
                
                Button(
                    onClick = {
                        user?.let { currentUser ->
                            scheduleViewModel.saveScheduleBlock(
                                userId = currentUser.id,
                                name = blockName,
                                description = blockDescription.ifBlank { null },
                                dayOfWeek = selectedDayState,
                                startTime = startTime,
                                endTime = endTime,
                                color = selectedColor,
                                professor = if (isStudent) professor.ifBlank { null } else null,
                                classroom = if (isStudent) classroom.ifBlank { null } else null
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = blockName.isNotBlank() && !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Diálogo selector de día
    if (showDayPicker) {
        DayPickerDialog(
            selectedDay = selectedDayState,
            onDaySelected = { day ->
                selectedDayState = day
                showDayPicker = false
            },
            onDismiss = { showDayPicker = false }
        )
    }
    
    // Diálogo selector de color
    if (showColorPicker) {
        ColorPickerDialog(
            selectedColor = selectedColor,
            onColorSelected = { color ->
                selectedColor = color
                showColorPicker = false
            },
            onDismiss = { showColorPicker = false }
        )
    }
    
    // Diálogo selector de hora de inicio
    if (showStartTimePicker) {
        TimePickerDialog(
            selectedTime = startTime,
            onTimeSelected = { time ->
                startTime = time
                showStartTimePicker = false
            },
            onDismiss = { showStartTimePicker = false }
        )
    }
    
    // Diálogo selector de hora de fin
    if (showEndTimePicker) {
        TimePickerDialog(
            selectedTime = endTime,
            onTimeSelected = { time ->
                endTime = time
                showEndTimePicker = false
            },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

@Composable
private fun DayPickerDialog(
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val days = listOf(
        1 to "Lunes",
        2 to "Martes",
        3 to "Miércoles",
        4 to "Jueves",
        5 to "Viernes",
        6 to "Sábado",
        7 to "Domingo"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar día") },
        text = {
            Column {
                days.forEach { (dayValue, dayName) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDaySelected(dayValue) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedDay == dayValue,
                            onClick = { onDaySelected(dayValue) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = dayName,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun ColorPickerDialog(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = listOf(
        "#1976D2" to "Azul",
        "#388E3C" to "Verde",
        "#F57C00" to "Naranja",
        "#C62828" to "Rojo",
        "#7B1FA2" to "Púrpura",
        "#00796B" to "Verde Azulado",
        "#C2185B" to "Rosa",
        "#5D4037" to "Marrón",
        "#455A64" to "Gris Azulado",
        "#FBC02D" to "Amarillo"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar color") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colors.chunked(5).forEach { rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowColors.forEach { (colorHex, colorName) ->
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(
                                        color = Color(android.graphics.Color.parseColor(colorHex)),
                                        shape = CircleShape
                                    )
                                    .clickable { onColorSelected(colorHex) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedColor == colorHex) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Seleccionado",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

private fun getDayName(day: Int): String {
    return when (day) {
        1 -> "Lunes"
        2 -> "Martes"
        3 -> "Miércoles"
        4 -> "Jueves"
        5 -> "Viernes"
        6 -> "Sábado"
        7 -> "Domingo"
        else -> "Lunes"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Parse selected time
    val timeParts = selectedTime.split(":")
    val hour = timeParts.getOrNull(0)?.toIntOrNull() ?: 9
    val minute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0
    
    val timePickerState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar hora") },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val formattedTime = String.format(
                        "%02d:%02d",
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    onTimeSelected(formattedTime)
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}