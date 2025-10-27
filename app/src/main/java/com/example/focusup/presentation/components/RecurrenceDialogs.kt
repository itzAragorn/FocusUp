package com.example.focusup.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun RecurrenceDeleteDialog(
    taskName: String,
    onDeleteThis: () -> Unit,
    onDeleteAll: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("🔄", style = MaterialTheme.typography.headlineMedium)
        },
        title = {
            Text(
                text = "Eliminar tarea recurrente",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "\"$taskName\" es parte de una serie recurrente.\n\n¿Qué deseas eliminar?"
            )
        },
        confirmButton = {
            Button(
                onClick = onDeleteAll,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Toda la serie")
            }
        },
        dismissButton = {
            TextButton(onClick = onDeleteThis) {
                Text("Solo esta")
            }
        }
    )
}

@Composable
fun RecurrenceEditDialog(
    taskName: String,
    onEditThis: () -> Unit,
    onEditAll: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("🔄", style = MaterialTheme.typography.headlineMedium)
        },
        title = {
            Text(
                text = "Editar tarea recurrente",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "\"$taskName\" es parte de una serie recurrente.\n\n¿Qué deseas editar?"
            )
        },
        confirmButton = {
            Button(onClick = onEditAll) {
                Text("Todas las futuras")
            }
        },
        dismissButton = {
            TextButton(onClick = onEditThis) {
                Text("Solo esta")
            }
        }
    )
}
