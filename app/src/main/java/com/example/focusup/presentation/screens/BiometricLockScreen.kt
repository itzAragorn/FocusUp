package com.example.focusup.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.focusup.ui.theme.DarkGraphite
import com.example.focusup.ui.theme.ElectricPurple
import com.example.focusup.utils.BiometricHelper

@Composable
fun BiometricLockScreen(
    userName: String,
    onAuthenticated: () -> Unit,
    onUsePassword: () -> Unit
) {
    val context = LocalContext.current
    val biometricHelper = remember { BiometricHelper(context) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isAuthenticating by remember { mutableStateOf(false) }
    
    // Intentar autenticar automáticamente al entrar
    LaunchedEffect(Unit) {
        if (biometricHelper.canAuthenticate() == BiometricHelper.BiometricStatus.AVAILABLE) {
            (context as? FragmentActivity)?.let { activity ->
                isAuthenticating = true
                biometricHelper.authenticate(
                    activity = activity,
                    title = "Desbloquear FocusUp",
                    subtitle = "Verifica tu identidad para continuar",
                    onSuccess = {
                        isAuthenticating = false
                        onAuthenticated()
                    },
                    onError = { code, message ->
                        isAuthenticating = false
                        errorMessage = message
                    },
                    onFailed = {
                        isAuthenticating = false
                        errorMessage = "No se pudo verificar tu identidad"
                    }
                )
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGraphite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Icono de huella
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = "Autenticación Biométrica",
                modifier = Modifier.size(120.dp),
                tint = ElectricPurple
            )
            
            // Título
            Text(
                text = "FocusUp",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricPurple
            )
            
            // Mensaje de bienvenida
            Text(
                text = "Hola, $userName",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            // Instrucciones
            Text(
                text = "Usa tu huella digital o Face ID para desbloquear",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón para reintentar
            if (!isAuthenticating && errorMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Red.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = Color.Red,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        errorMessage = null
                        (context as? FragmentActivity)?.let { activity ->
                            isAuthenticating = true
                            biometricHelper.authenticate(
                                activity = activity,
                                title = "Desbloquear FocusUp",
                                subtitle = "Verifica tu identidad para continuar",
                                onSuccess = {
                                    isAuthenticating = false
                                    onAuthenticated()
                                },
                                onError = { code, message ->
                                    isAuthenticating = false
                                    errorMessage = message
                                },
                                onFailed = {
                                    isAuthenticating = false
                                    errorMessage = "No se pudo verificar tu identidad"
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricPurple
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Intentar de nuevo")
                }
            }
            
            // Indicador de carga
            if (isAuthenticating) {
                CircularProgressIndicator(
                    color = ElectricPurple,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botón para usar contraseña
            TextButton(
                onClick = onUsePassword,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Usar contraseña en su lugar",
                    color = ElectricPurple
                )
            }
        }
    }
}
