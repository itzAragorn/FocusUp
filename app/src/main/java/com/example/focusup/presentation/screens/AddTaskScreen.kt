package com.example.focusup.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusup.presentation.viewmodels.TaskViewModel
import com.example.focusup.presentation.components.PrioritySelector
import com.example.focusup.presentation.components.TagSelector
import com.example.focusup.presentation.components.AttachmentSelector
import com.example.focusup.presentation.components.RecurrenceSelector
import com.example.focusup.domain.model.TaskPriority
import com.example.focusup.domain.model.TaskAttachment
import com.example.focusup.domain.model.FileType
import com.example.focusup.domain.model.RecurrenceType
import com.example.focusup.utils.DateTimeUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    userId: Long,
    selectedDate: String? = null,
    onTaskCreated: () -> Unit,
    onNavigateBack: () -> Unit,
    taskViewModel: TaskViewModel
) {
    var taskName by remember { mutableStateOf("") }
    var taskDescription by remember { mutableStateOf("") }
    var selectedDateState by remember { 
        mutableStateOf(selectedDate ?: DateTimeUtils.getCurrentDate()) 
    }
    var selectedTime by remember { mutableStateOf("09:00") }
    var selectedPriority by remember { mutableStateOf(TaskPriority.NONE) }
    var selectedTags by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedAttachments by remember { mutableStateOf<List<TaskAttachment>>(emptyList()) }
    var selectedRecurrence by remember { mutableStateOf(RecurrenceType.NONE) }
    var recurrenceEndDate by remember { mutableStateOf<String?>(null) }
    var isNotificationEnabled by remember { mutableStateOf(true) }
    
    // Diálogos
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showRecurrenceEndDatePicker by remember { mutableStateOf(false) }
    
    val uiState by taskViewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    // Handle UI state changes
    LaunchedEffect(uiState.isTaskSaved) {
        if (uiState.isTaskSaved) {
            onTaskCreated()
        }
    }
    
    // Show error message if needed
    uiState.errorMessage?.let { error ->
        LaunchedEffect(error) {
            // TODO: Show snackbar or toast with error message
            // For now, just print to console
            println("Error saving task: $error")
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Tarea") },
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
                        text = "📝 Crear Nueva Tarea",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Organiza tu tiempo y mantente enfocado",
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
                    // Nombre de la tarea
                    OutlinedTextField(
                        value = taskName,
                        onValueChange = { taskName = it },
                        label = { Text("Nombre de la tarea") },
                        leadingIcon = {
                            Icon(Icons.Default.Assignment, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    // Descripción
                    OutlinedTextField(
                        value = taskDescription,
                        onValueChange = { taskDescription = it },
                        label = { Text("Descripción (opcional)") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                    
                    // Fecha
                    OutlinedTextField(
                        value = DateTimeUtils.formatDateForDisplay(selectedDateState),
                        onValueChange = { },
                        label = { Text("Fecha") },
                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )
                    
                    // Hora
                    OutlinedTextField(
                        value = selectedTime,
                        onValueChange = { selectedTime = it },
                        label = { Text("Hora") },
                        leadingIcon = {
                            Icon(Icons.Default.AccessTime, contentDescription = null)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(Icons.Default.Schedule, contentDescription = "Seleccionar hora")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )
                    
                    // Selector de prioridad
                    PrioritySelector(
                        selectedPriority = selectedPriority,
                        onPrioritySelected = { selectedPriority = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    HorizontalDivider()
                    
                    // Selector de etiquetas
                    TagSelector(
                        selectedTags = selectedTags,
                        onTagsChanged = { selectedTags = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    HorizontalDivider()
                    
                    // Selector de archivos adjuntos
                    AttachmentSelector(
                        attachments = selectedAttachments,
                        onAttachmentsChanged = { selectedAttachments = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    HorizontalDivider()
                    
                    // Selector de recurrencia
                    RecurrenceSelector(
                        selectedRecurrence = selectedRecurrence,
                        onRecurrenceSelected = { recurrence ->
                            selectedRecurrence = recurrence
                            // Si selecciona una recurrencia, limpiar fecha de fin anterior
                            if (recurrence == RecurrenceType.NONE) {
                                recurrenceEndDate = null
                            }
                        },
                        recurrenceEndDate = recurrenceEndDate,
                        onEndDateSelected = { date ->
                            when {
                                date == null -> {
                                    // null = abrir date picker
                                    showRecurrenceEndDatePicker = true
                                }
                                date.isEmpty() -> {
                                    // cadena vacía = limpiar fecha
                                    recurrenceEndDate = null
                                }
                                else -> {
                                    // fecha válida = guardar
                                    recurrenceEndDate = date
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    HorizontalDivider()
                    
                    // Notificación
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Recordatorio",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        
                        Switch(
                            checked = isNotificationEnabled,
                            onCheckedChange = { isNotificationEnabled = it }
                        )
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
                        taskViewModel.saveTask(
                            userId = userId,
                            name = taskName,
                            description = taskDescription.ifBlank { null },
                            date = selectedDateState,
                            time = selectedTime,
                            isNotificationEnabled = isNotificationEnabled,
                            priority = selectedPriority.name,
                            tags = selectedTags.joinToString(","),
                            attachments = FileType.convertToString(selectedAttachments),
                            recurrenceType = selectedRecurrence.name,
                            recurrenceEndDate = recurrenceEndDate
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = taskName.isNotBlank() && !uiState.isLoading
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
    
    // Diálogo selector de fecha
    if (showDatePicker) {
        DatePickerDialog(
            selectedDate = selectedDateState,
            onDateSelected = { date ->
                selectedDateState = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
    
    // Diálogo selector de hora
    if (showTimePicker) {
        TimePickerDialog(
            selectedTime = selectedTime,
            onTimeSelected = { time ->
                selectedTime = time
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false }
        )
    }
    
    // Diálogo selector de fecha de fin de recurrencia
    if (showRecurrenceEndDatePicker) {
        RecurrenceEndDatePickerDialog(
            selectedDate = recurrenceEndDate,
            minDate = selectedDateState, // No puede ser antes de la fecha de inicio
            onDateSelected = { date ->
                recurrenceEndDate = date
                showRecurrenceEndDatePicker = false
            },
            onDismiss = { showRecurrenceEndDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecurrenceEndDatePickerDialog(
    selectedDate: String?,
    minDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    // Parse selected date or use min date
    val calendar = Calendar.getInstance()
    try {
        calendar.time = if (selectedDate != null) {
            dateFormat.parse(selectedDate) ?: dateFormat.parse(minDate)!!
        } else {
            dateFormat.parse(minDate)!!
        }
    } catch (e: Exception) {
        calendar.time = Date()
    }
    
    // Fecha mínima: fecha de inicio de la tarea
    val minCalendar = Calendar.getInstance().apply {
        time = dateFormat.parse(minDate)!!
    }
    
    // Fecha máxima: 2 años adelante
    val maxCalendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, 2)
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= minCalendar.timeInMillis && 
                       utcTimeMillis <= maxCalendar.timeInMillis
            }
            
            override fun isSelectableYear(year: Int): Boolean {
                val minYear = minCalendar.get(Calendar.YEAR)
                val maxYear = maxCalendar.get(Calendar.YEAR)
                return year >= minYear && year <= maxYear
            }
        }
    )
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedCalendar = Calendar.getInstance().apply {
                            timeInMillis = millis
                        }
                        val dateString = dateFormat.format(selectedCalendar.time)
                        onDateSelected(dateString)
                    }
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
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    // Parse selected date or use current date
    val calendar = Calendar.getInstance()
    try {
        calendar.time = dateFormat.parse(selectedDate) ?: Date()
    } catch (e: Exception) {
        calendar.time = Date()
    }
    
    // Configurar fechas límite: 3 meses atrás y 9 meses adelante
    val currentCalendar = Calendar.getInstance()
    
    // Fecha mínima: 3 meses atrás
    val minCalendar = Calendar.getInstance().apply {
        add(Calendar.MONTH, -3)
    }
    
    // Fecha máxima: 9 meses adelante
    val maxCalendar = Calendar.getInstance().apply {
        add(Calendar.MONTH, 9)
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= minCalendar.timeInMillis && 
                       utcTimeMillis <= maxCalendar.timeInMillis
            }
            
            override fun isSelectableYear(year: Int): Boolean {
                val currentYear = currentCalendar.get(Calendar.YEAR)
                return year >= (currentYear - 1) && year <= (currentYear + 1)
            }
        }
    )
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedCalendar = Calendar.getInstance().apply {
                            timeInMillis = millis
                        }
                        val dateString = dateFormat.format(selectedCalendar.time)
                        onDateSelected(dateString)
                    }
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
    ) {
        DatePicker(state = datePickerState)
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