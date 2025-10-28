package com.example.focusup.presentation.screens

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.focusup.presentation.viewmodels.ProfileViewModel
import com.example.focusup.ui.theme.ElectricPurple
import com.example.focusup.ui.theme.DarkGraphite
import com.example.focusup.utils.BiometricHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: Long,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(userId)
    )
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()
    
    val biometricHelper = remember { BiometricHelper(context) }
    var showBiometricDialog by remember { mutableStateOf(false) }
    var biometricErrorMessage by remember { mutableStateOf<String?>(null) }
    
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Actualizar estados cuando cambie el usuario
    LaunchedEffect(user) {
        user?.let {
            name = it.name
            phoneNumber = it.phoneNumber ?: ""
            bio = it.bio ?: ""
            institution = it.institution ?: ""
            position = it.position ?: ""
            birthDate = it.birthDate ?: ""
            selectedImageUri = it.profileImagePath?.let { path -> Uri.fromFile(File(path)) }
        }
    }
    
    // Launcher para galería (se declara primero)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            viewModel.saveProfileImage(context, it)
        }
    }
    
    // Launcher para cámara (se declara segundo)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri?.let {
                viewModel.saveProfileImage(context, it)
            }
        }
    }
    
    // Launcher para permisos de cámara (ahora puede usar cameraLauncher)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = viewModel.createImageUri(context)
            selectedImageUri = uri
            cameraLauncher.launch(uri)
        }
    }
    
    // Launcher para permisos de galería (ahora puede usar galleryLauncher)
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (isEditing) {
                        IconButton(
                            onClick = {
                                viewModel.updateProfile(
                                    name = name,
                                    phoneNumber = phoneNumber.takeIf { it.isNotBlank() },
                                    bio = bio.takeIf { it.isNotBlank() },
                                    institution = institution.takeIf { it.isNotBlank() },
                                    position = position.takeIf { it.isNotBlank() },
                                    birthDate = birthDate.takeIf { it.isNotBlank() }
                                )
                                isEditing = false
                            }
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Guardar")
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ElectricPurple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(DarkGraphite)
        ) {
            // Sección de foto de perfil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Box {
                    // Foto de perfil
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(selectedImageUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .border(4.dp, ElectricPurple, CircleShape)
                                .clickable(enabled = isEditing) {
                                    if (isEditing) showImageSourceDialog = true
                                },
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(CircleShape)
                                .background(ElectricPurple.copy(alpha = 0.3f))
                                .border(4.dp, ElectricPurple, CircleShape)
                                .clickable(enabled = isEditing) {
                                    if (isEditing) showImageSourceDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Sin foto",
                                modifier = Modifier.size(80.dp),
                                tint = ElectricPurple
                            )
                        }
                    }
                    
                    // Botón de cámara si está en modo edición
                    if (isEditing) {
                        FloatingActionButton(
                            onClick = { showImageSourceDialog = true },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(40.dp),
                            containerColor = ElectricPurple
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Cambiar foto",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            // Información del usuario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2D2D2D))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Nombre
                    ProfileField(
                        label = "Nombre",
                        value = name,
                        onValueChange = { name = it },
                        isEditing = isEditing,
                        icon = Icons.Default.Person
                    )
                    
                    // Email (no editable)
                    ProfileField(
                        label = "Email",
                        value = user?.email ?: "",
                        onValueChange = {},
                        isEditing = false,
                        icon = Icons.Default.Email
                    )
                    
                    // Teléfono
                    ProfileField(
                        label = "Teléfono",
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        isEditing = isEditing,
                        icon = Icons.Default.Phone,
                        placeholder = "Opcional"
                    )
                    
                    // Fecha de nacimiento
                    ProfileField(
                        label = "Fecha de nacimiento",
                        value = birthDate,
                        onValueChange = {},
                        isEditing = isEditing,
                        icon = Icons.Default.Cake,
                        placeholder = "Opcional",
                        onClick = if (isEditing) { { showDatePicker = true } } else null
                    )
                    
                    // Tipo de perfil (no editable)
                    ProfileField(
                        label = "Tipo de perfil",
                        value = if (user?.profileType == "STUDENT") "Estudiante" else "Trabajador",
                        onValueChange = {},
                        isEditing = false,
                        icon = Icons.Default.Work
                    )
                    
                    // Institución/Empresa
                    ProfileField(
                        label = if (user?.profileType == "STUDENT") "Universidad" else "Empresa",
                        value = institution,
                        onValueChange = { institution = it },
                        isEditing = isEditing,
                        icon = Icons.Default.Business,
                        placeholder = "Opcional"
                    )
                    
                    // Carrera/Cargo
                    ProfileField(
                        label = if (user?.profileType == "STUDENT") "Carrera" else "Cargo",
                        value = position,
                        onValueChange = { position = it },
                        isEditing = isEditing,
                        icon = Icons.Default.School,
                        placeholder = "Opcional"
                    )
                    
                    // Bio
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Biografía") },
                        enabled = isEditing,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        placeholder = { Text("Cuéntanos sobre ti (opcional)") },
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricPurple,
                            focusedLabelColor = ElectricPurple,
                            focusedLeadingIconColor = ElectricPurple
                        )
                    )
                }
            }
            
            // Sección de Seguridad
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkGraphite)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Seguridad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ElectricPurple
                    )
                    
                    // Autenticación Biométrica
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometría",
                                tint = ElectricPurple
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Autenticación Biométrica",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = when (biometricHelper.canAuthenticate()) {
                                        BiometricHelper.BiometricStatus.AVAILABLE -> 
                                            if (biometricEnabled) "Activada" else "Disponible"
                                        else -> biometricHelper.getStatusMessage(biometricHelper.canAuthenticate())
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = { enabled ->
                                val status = biometricHelper.canAuthenticate()
                                when {
                                    status == BiometricHelper.BiometricStatus.AVAILABLE && enabled -> {
                                        // Solicitar autenticación para activar
                                        (context as? FragmentActivity)?.let { activity ->
                                            biometricHelper.authenticate(
                                                activity = activity,
                                                title = "Activar Biometría",
                                                subtitle = "Verifica tu identidad para activar la autenticación biométrica",
                                                onSuccess = {
                                                    viewModel.setBiometricEnabled(true)
                                                    biometricErrorMessage = null
                                                },
                                                onError = { code, message ->
                                                    biometricErrorMessage = message
                                                    showBiometricDialog = true
                                                },
                                                onFailed = {
                                                    biometricErrorMessage = "No se pudo verificar tu identidad"
                                                    showBiometricDialog = true
                                                }
                                            )
                                        }
                                    }
                                    !enabled -> {
                                        // Desactivar directamente
                                        viewModel.setBiometricEnabled(false)
                                        biometricErrorMessage = null
                                    }
                                    else -> {
                                        // No disponible
                                        biometricErrorMessage = biometricHelper.getStatusMessage(status)
                                        showBiometricDialog = true
                                    }
                                }
                            },
                            enabled = biometricHelper.canAuthenticate() == BiometricHelper.BiometricStatus.AVAILABLE,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ElectricPurple,
                                checkedTrackColor = ElectricPurple.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
            
            // Botón de cerrar sesión
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable { onLogout() },
                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar Sesión",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
    
    // Diálogo de error biométrico
    if (showBiometricDialog && biometricErrorMessage != null) {
        AlertDialog(
            onDismissRequest = { 
                showBiometricDialog = false
                biometricErrorMessage = null
            },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.Red
                )
            },
            title = { Text("Error de Biometría") },
            text = { Text(biometricErrorMessage ?: "") },
            confirmButton = {
                TextButton(onClick = { 
                    showBiometricDialog = false
                    biometricErrorMessage = null
                }) {
                    Text("Entendido", color = ElectricPurple)
                }
            }
        )
    }
    
    // Diálogo de selección de fuente de imagen
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Seleccionar foto de perfil") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón de galería
                    OutlinedButton(
                        onClick = {
                            showImageSourceDialog = false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            } else {
                                galleryLauncher.launch("image/*")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = ElectricPurple
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Elegir de Galería", color = ElectricPurple)
                    }
                    
                    // Botón de cámara
                    OutlinedButton(
                        onClick = {
                            showImageSourceDialog = false
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = ElectricPurple
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Tomar Foto", color = ElectricPurple)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showImageSourceDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    
    // Diálogo de fecha de nacimiento
    if (showDatePicker) {
        BirthDatePickerDialog(
            selectedDate = birthDate.ifEmpty { "2000-01-01" },
            onDateSelected = { date ->
                birthDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isEditing: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String = "",
    onClick: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = isEditing,
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onClick != null) it.clickable { onClick() } else it },
        singleLine = true,
        placeholder = { Text(placeholder) },
        leadingIcon = {
            Icon(icon, contentDescription = null)
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ElectricPurple,
            focusedLabelColor = ElectricPurple,
            focusedLeadingIconColor = ElectricPurple
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthDatePickerDialog(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    val calendar = Calendar.getInstance()
    try {
        calendar.time = dateFormat.parse(selectedDate) ?: Date()
    } catch (e: Exception) {
        calendar.set(2000, 0, 1)
    }
    
    // Fecha mínima: 100 años atrás
    val minCalendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, -100)
    }
    
    // Fecha máxima: 13 años atrás (edad mínima)
    val maxCalendar = Calendar.getInstance().apply {
        add(Calendar.YEAR, -13)
    }
    
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= minCalendar.timeInMillis && 
                       utcTimeMillis <= maxCalendar.timeInMillis
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
