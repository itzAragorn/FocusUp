package com.example.focusup.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.focusup.domain.model.RecurrenceType
import com.example.focusup.utils.DateTimeUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurrenceSelector(
    selectedRecurrence: RecurrenceType,
    recurrenceEndDate: String?,
    onRecurrenceSelected: (RecurrenceType) -> Unit,
    onEndDateSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Recurrencia",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        
        // Dropdown para seleccionar tipo de recurrencia
        var expanded by remember { mutableStateOf(false) }
        
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedRecurrence.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de recurrencia") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                leadingIcon = {
                    Icon(Icons.Default.Repeat, contentDescription = null)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                RecurrenceType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.displayName) },
                        onClick = {
                            onRecurrenceSelected(type)
                            expanded = false
                            if (type == RecurrenceType.NONE) {
                                onEndDateSelected(null)
                            }
                        },
                        leadingIcon = {
                            Icon(
                                when (type) {
                                    RecurrenceType.NONE -> Icons.Default.Block
                                    RecurrenceType.DAILY -> Icons.Default.Today
                                    RecurrenceType.WEEKLY -> Icons.Default.CalendarToday
                                    RecurrenceType.MONTHLY -> Icons.Default.CalendarMonth
                                },
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
        
        // Campo de fecha final (solo si hay recurrencia)
        if (selectedRecurrence != RecurrenceType.NONE) {
            OutlinedTextField(
                value = recurrenceEndDate?.let { DateTimeUtils.formatDateForDisplay(it) } ?: "Sin fecha límite",
                onValueChange = {},
                readOnly = true,
                label = { Text("Termina el (opcional)") },
                leadingIcon = {
                    Icon(Icons.Default.Event, contentDescription = null)
                },
                trailingIcon = {
                    Row {
                        if (recurrenceEndDate != null) {
                            IconButton(onClick = { 
                                // Limpiar la fecha pasando una cadena vacía como señal
                                onEndDateSelected("")
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Quitar fecha límite")
                            }
                        }
                        IconButton(onClick = { 
                            // Notificar al padre que abra el date picker pasando null
                            onEndDateSelected(null)
                        }) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            
            Text(
                text = "💡 Las tareas se crearán automáticamente según la recurrencia hasta la fecha límite",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun RecurrenceChip(
    recurrenceType: RecurrenceType,
    modifier: Modifier = Modifier
) {
    if (recurrenceType != RecurrenceType.NONE) {
        AssistChip(
            onClick = {},
            label = { Text(recurrenceType.displayName) },
            leadingIcon = {
                Icon(
                    Icons.Default.Repeat,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            },
            modifier = modifier,
            colors = AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                labelColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        )
    }
}
