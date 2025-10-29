package com.example.focusup.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.focusup.data.database.entities.Task
import com.example.focusup.domain.model.TaskPriority
import com.example.focusup.domain.model.Tag
import com.example.focusup.domain.model.FileType
import com.example.focusup.presentation.components.*
import com.example.focusup.presentation.viewmodels.CalendarScreenViewModel
import com.example.focusup.presentation.viewmodels.TaskViewModel
import com.example.focusup.utils.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavigateBack: () -> Unit,
    onAddTask: () -> Unit,
    calendarScreenViewModel: CalendarScreenViewModel,
    taskViewModel: TaskViewModel,
    userId: Long
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    var showDeleteTaskDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var refreshTrigger by remember { mutableStateOf(0) }
    val uiState by calendarScreenViewModel.uiState.collectAsState()
    val taskUiState by taskViewModel.uiState.collectAsState()
    
    // Cargar las tareas del usuario cuando se abre la pantalla
    LaunchedEffect(userId, refreshTrigger) {
        calendarScreenViewModel.loadTasksForUser(userId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todas las Tareas") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTask,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar tarea")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Barra de búsqueda
            TaskSearchBar(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { query ->
                    calendarScreenViewModel.updateSearchQuery(query)
                },
                onClearSearch = {
                    calendarScreenViewModel.updateSearchQuery("")
                }
            )
            
            // Botón de filtros y contador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.filteredTasks.size} ${if (uiState.filteredTasks.size == 1) "tarea" else "tareas"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Badge de filtros activos
                    if (uiState.selectedFilter != TaskFilter.ALL || uiState.selectedPriorities.isNotEmpty()) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary
                        ) {
                            val filterCount = (if (uiState.selectedFilter != TaskFilter.ALL) 1 else 0) + 
                                             uiState.selectedPriorities.size
                            Text("$filterCount")
                        }
                    }
                    
                    OutlinedButton(
                        onClick = { showFilterSheet = true }
                    ) {
                        Icon(Icons.Default.FilterList, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Filtros")
                    }
                }
            }
            
            // Lista de tareas
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (uiState.searchQuery.isNotEmpty() || 
                                      uiState.selectedFilter != TaskFilter.ALL || 
                                      uiState.selectedPriorities.isNotEmpty()) {
                                "No se encontraron tareas con los filtros aplicados"
                            } else {
                                "No tienes tareas aún"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (uiState.searchQuery.isNotEmpty() || 
                            uiState.selectedFilter != TaskFilter.ALL || 
                            uiState.selectedPriorities.isNotEmpty()) {
                            OutlinedButton(onClick = { calendarScreenViewModel.clearFilters() }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Limpiar filtros")
                            }
                        } else {
                            Button(onClick = onAddTask) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Crear tarea")
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = uiState.filteredTasks,
                        key = { task -> task.id }
                    ) { task ->
                        TaskItemCard(
                            task = task,
                            onToggleComplete = { taskId ->
                                taskViewModel.toggleTaskCompletion(taskId)
                                refreshTrigger++
                            },
                            onDeleteTask = { selectedTask ->
                                taskToDelete = selectedTask
                                showDeleteTaskDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
    
    // Bottom Sheet de filtros
    FilterBottomSheet(
        showBottomSheet = showFilterSheet,
        onDismiss = { showFilterSheet = false },
        selectedFilter = uiState.selectedFilter,
        onFilterSelected = { filter ->
            calendarScreenViewModel.updateFilter(filter)
        },
        selectedPriorities = uiState.selectedPriorities,
        onPriorityToggle = { priority ->
            calendarScreenViewModel.togglePriorityFilter(priority)
        },
        availableTags = uiState.availableTags,
        selectedTags = uiState.selectedTags,
        onTagToggle = { tag ->
            calendarScreenViewModel.toggleTagFilter(tag)
        },
        onClearFilters = {
            calendarScreenViewModel.clearFilters()
        },
        onApplyFilters = {
            // Los filtros ya se aplican en tiempo real
        }
    )
    
    // Diálogo de recurrencia (si la tarea es recurrente)
    if (taskUiState.showRecurrenceDialog && taskUiState.selectedTask != null) {
        RecurrenceDeleteDialog(
            taskName = taskUiState.selectedTask!!.name,
            onDeleteThis = {
                taskUiState.selectedTask?.let { task ->
                    taskViewModel.deleteTaskOnly(task.id)
                    refreshTrigger++
                }
            },
            onDeleteAll = {
                taskUiState.selectedTask?.let { task ->
                    taskViewModel.deleteRecurringSeries(task.id)
                    refreshTrigger++
                }
            },
            onDismiss = {
                taskViewModel.dismissRecurrenceDialog()
            }
        )
    }
    
    // Diálogo de confirmación para eliminar tarea (no recurrente)
    if (showDeleteTaskDialog && taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteTaskDialog = false
                taskToDelete = null
            },
            title = { Text("Eliminar tarea") },
            text = { 
                Text("¿Estás seguro de que quieres eliminar la tarea \"${taskToDelete!!.name}\"?") 
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        taskToDelete?.let { task ->
                            taskViewModel.deleteTask(task.id)
                            refreshTrigger++
                        }
                        showDeleteTaskDialog = false
                        taskToDelete = null
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDeleteTaskDialog = false
                        taskToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun TaskItemCard(
    task: Task,
    onToggleComplete: (Long) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    
                    // Priority badge
                    if (task.priority != "NONE") {
                        val priority = try {
                            TaskPriority.valueOf(task.priority)
                        } catch (e: Exception) {
                            TaskPriority.NONE
                        }
                        if (priority != TaskPriority.NONE) {
                            PriorityBadge(priority = priority, showLabel = false)
                        }
                    }
                }

                task.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Tags
                if (!task.tags.isNullOrBlank()) {
                    val tagsList = Tag.parseFromString(task.tags)
                    if (tagsList.isNotEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            tagsList.take(3).forEach { tag ->
                                TagChip(tag = tag)
                            }
                            if (tagsList.size > 3) {
                                Text(
                                    text = "+${tagsList.size - 3}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                    }
                }
                
                // Archivos adjuntos
                if (!task.attachments.isNullOrBlank()) {
                    val attachmentsList = FileType.parseFromString(task.attachments)
                    if (attachmentsList.isNotEmpty()) {
                        AttachmentsList(
                            attachments = attachmentsList,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = DateTimeUtils.formatDateForDisplay(task.date),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = task.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleComplete(task.id) }
                )
                IconButton(
                    onClick = { onDeleteTask(task) }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar tarea",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
