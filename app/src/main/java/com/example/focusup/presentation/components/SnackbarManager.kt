package com.example.focusup.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Manager para manejar Snackbars de forma centralizada
class SnackbarManager {
    private val _messages = mutableListOf<SnackbarMessage>()
    private var currentSnackbarData: SnackbarData? = null
    
    fun showMessage(message: SnackbarMessage) {
        _messages.add(message)
    }
    
    fun showSuccess(message: String) {
        showMessage(SnackbarMessage.Success(message))
    }
    
    fun showError(message: String) {
        showMessage(SnackbarMessage.Error(message))
    }
    
    fun showInfo(message: String) {
        showMessage(SnackbarMessage.Info(message))
    }
    
    fun showWithAction(
        message: String,
        actionLabel: String,
        onAction: () -> Unit
    ) {
        showMessage(SnackbarMessage.WithAction(message, actionLabel, onAction))
    }
    
    suspend fun processMessages(
        snackbarHostState: SnackbarHostState
    ) {
        while (_messages.isNotEmpty()) {
            val message = _messages.removeAt(0)
            val result = when (message) {
                is SnackbarMessage.Success -> {
                    snackbarHostState.showSnackbar(
                        message = message.text,
                        duration = SnackbarDuration.Short
                    )
                }
                is SnackbarMessage.Error -> {
                    snackbarHostState.showSnackbar(
                        message = message.text,
                        duration = SnackbarDuration.Long
                    )
                }
                is SnackbarMessage.Info -> {
                    snackbarHostState.showSnackbar(
                        message = message.text,
                        duration = SnackbarDuration.Short
                    )
                }
                is SnackbarMessage.WithAction -> {
                    snackbarHostState.showSnackbar(
                        message = message.text,
                        actionLabel = message.actionLabel,
                        duration = SnackbarDuration.Long
                    )
                }
            }
            
            if (result == SnackbarResult.ActionPerformed) {
                (message as? SnackbarMessage.WithAction)?.onAction?.invoke()
            }
        }
    }
}

sealed class SnackbarMessage {
    abstract val text: String
    
    data class Success(override val text: String) : SnackbarMessage()
    data class Error(override val text: String) : SnackbarMessage()
    data class Info(override val text: String) : SnackbarMessage()
    data class WithAction(
        override val text: String,
        val actionLabel: String,
        val onAction: () -> Unit
    ) : SnackbarMessage()
}

@Composable
fun rememberSnackbarManager(): SnackbarManager {
    return remember { SnackbarManager() }
}

@Composable
fun ObserveSnackbarMessages(
    snackbarManager: SnackbarManager,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    LaunchedEffect(snackbarManager) {
        scope.launch {
            snackbarManager.processMessages(snackbarHostState)
        }
    }
}

// Snackbar personalizado con colores
@Composable
fun CustomSnackbar(
    snackbarData: SnackbarData,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    containerColor: Color = MaterialTheme.colorScheme.inverseSurface,
    contentColor: Color = MaterialTheme.colorScheme.inverseOnSurface
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
        actionColor = MaterialTheme.colorScheme.primary
    )
}
