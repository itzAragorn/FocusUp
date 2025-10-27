package com.example.focusup.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlin.math.absoluteValue

@Composable
fun TagChip(
    tag: String,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    onRemove: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.2f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "#$tag",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
            
            if (onRemove != null) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Eliminar etiqueta",
                    modifier = Modifier
                        .size(16.dp)
                        .clickable(onClick = onRemove),
                    tint = color
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSelector(
    selectedTags: List<String>,
    onTagsChanged: (List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTagDialog by remember { mutableStateOf(false) }
    var newTagText by remember { mutableStateOf("") }
    
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Etiquetas",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            
            OutlinedButton(
                onClick = { showTagDialog = true },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Agregar", fontSize = 13.sp)
            }
        }
        
        if (selectedTags.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedTags) { tag ->
                    TagChip(
                        tag = tag,
                        color = getColorForTag(tag),
                        onRemove = {
                            onTagsChanged(selectedTags - tag)
                        }
                    )
                }
            }
        } else {
            Text(
                text = "Sin etiquetas",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
    
    // Diálogo para agregar nueva etiqueta
    if (showTagDialog) {
        AlertDialog(
            onDismissRequest = {
                showTagDialog = false
                newTagText = ""
            },
            title = { Text("Agregar Etiqueta") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newTagText,
                        onValueChange = { newTagText = it },
                        label = { Text("Nombre de la etiqueta") },
                        placeholder = { Text("ej: Trabajo, Personal, Urgente") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text(
                        text = "💡 Separa múltiples etiquetas con comas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newTags = newTagText.split(",")
                            .map { it.trim() }
                            .filter { it.isNotEmpty() && !selectedTags.contains(it) }
                        
                        if (newTags.isNotEmpty()) {
                            onTagsChanged(selectedTags + newTags)
                        }
                        showTagDialog = false
                        newTagText = ""
                    },
                    enabled = newTagText.isNotBlank()
                ) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showTagDialog = false
                        newTagText = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TagFilterChips(
    availableTags: List<String>,
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Filtrar por etiquetas",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        
        if (availableTags.isEmpty()) {
            Text(
                text = "No hay etiquetas disponibles",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(availableTags) { tag ->
                    val isSelected = selectedTags.contains(tag)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTagToggle(tag) },
                        label = {
                            Text(
                                text = "#$tag",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = getColorForTag(tag).copy(alpha = 0.3f),
                            selectedLabelColor = getColorForTag(tag)
                        )
                    )
                }
            }
        }
    }
}

private fun getColorForTag(tag: String): Color {
    val colors = listOf(
        Color(0xFFE57373), // Red
        Color(0xFFFFB74D), // Orange
        Color(0xFF81C784), // Green
        Color(0xFF64B5F6), // Blue
        Color(0xFF9575CD), // Purple
        Color(0xFFBA68C8), // Pink
        Color(0xFF4DD0E1), // Cyan
        Color(0xFFA1887F)  // Brown
    )
    
    // Hash del nombre para obtener un color consistente
    val hash = tag.hashCode()
    val index = hash.absoluteValue % colors.size
    return colors[index]
}
