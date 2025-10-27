package com.example.focusup.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
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
import com.example.focusup.domain.model.ProfileType

@Composable
fun ProfileSetupScreen(
    onProfileSelected: (ProfileType) -> Unit,
    onSkip: () -> Unit
) {
    var selectedProfileType by remember { mutableStateOf<ProfileType?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icono y título
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Personaliza tu experiencia",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Selecciona tu situación para personalizar FocusUp según tus necesidades",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Opciones de perfil
        ProfileType.values().forEach { profileType ->
            ProfileOptionCard(
                profileType = profileType,
                isSelected = selectedProfileType == profileType,
                onSelect = { selectedProfileType = profileType }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Botón continuar
        Button(
            onClick = { 
                selectedProfileType?.let { onProfileSelected(it) }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedProfileType != null
        ) {
            Text("Continuar")
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Botón omitir
        TextButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Omitir por ahora")
        }
    }
}

@Composable
private fun ProfileOptionCard(
    profileType: ProfileType,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelect
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) 
            null 
        else 
            CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (profileType) {
                    ProfileType.STUDENT -> Icons.Default.Person
                    ProfileType.WORKER -> Icons.Default.Person
                },
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = profileType.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (profileType) {
                        ProfileType.STUDENT -> "Gestiona horarios de clases, profesores y salas de estudio"
                        ProfileType.WORKER -> "Organiza tu horario laboral y tareas de trabajo"
                    },
                    fontSize = 14.sp,
                    color = if (isSelected) 
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Seleccionado",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}