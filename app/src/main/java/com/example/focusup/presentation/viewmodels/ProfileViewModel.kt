package com.example.focusup.presentation.viewmodels

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.focusup.data.database.FocusUpDatabase
import com.example.focusup.data.database.entities.User
import com.example.focusup.data.repository.UserRepository
import com.example.focusup.utils.UserPreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ProfileViewModel(
    private val userId: Long,
    private val userRepository: UserRepository,
    private val preferencesManager: UserPreferencesManager
) : ViewModel() {
    
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    
    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled
    
    init {
        loadUser()
        loadBiometricSetting()
    }
    
    private fun loadUser() {
        viewModelScope.launch {
            _user.value = userRepository.getUserById(userId)
        }
    }
    
    private fun loadBiometricSetting() {
        viewModelScope.launch {
            preferencesManager.biometricEnabledFlow.collect { enabled ->
                _biometricEnabled.value = enabled
            }
        }
    }
    
    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setBiometricEnabled(enabled)
            _biometricEnabled.value = enabled
            
            // También actualizar en el User entity
            _user.value?.let { currentUser ->
                val updatedUser = currentUser.copy(biometricEnabled = enabled)
                userRepository.updateUser(updatedUser)
                _user.value = updatedUser
            }
        }
    }
    
    fun updateProfile(
        name: String,
        phoneNumber: String?,
        bio: String?,
        institution: String?,
        position: String?,
        birthDate: String?
    ) {
        viewModelScope.launch {
            _user.value?.let { currentUser ->
                val updatedUser = currentUser.copy(
                    name = name,
                    phoneNumber = phoneNumber,
                    bio = bio,
                    institution = institution,
                    position = position,
                    birthDate = birthDate
                )
                userRepository.updateUser(updatedUser)
                _user.value = updatedUser
            }
        }
    }
    
    fun createImageUri(context: Context): Uri {
        val directory = File(context.filesDir, "profile_images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        
        val file = File(directory, "profile_temp_${userId}_${System.currentTimeMillis()}.jpg")
        
        // Usar FileProvider para Android 7.0+
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
    
    fun saveProfileImage(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val directory = File(context.filesDir, "profile_images")
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                
                val fileName = "profile_${userId}.jpg"
                val destinationFile = File(directory, fileName)
                
                // Copiar la imagen al almacenamiento interno
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(destinationFile).use { output ->
                        input.copyTo(output)
                    }
                }
                
                // Actualizar el usuario con la ruta de la imagen
                _user.value?.let { currentUser ->
                    val updatedUser = currentUser.copy(
                        profileImagePath = destinationFile.absolutePath
                    )
                    userRepository.updateUser(updatedUser)
                    _user.value = updatedUser
                }
                
                // Limpiar archivos temporales
                directory.listFiles()?.forEach { file ->
                    if (file.name.startsWith("profile_temp_") && file.name != fileName) {
                        file.delete()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    companion object {
        fun Factory(userId: Long): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val database = FocusUpDatabase.getDatabase(application)
                val repository = UserRepository(database.userDao())
                val preferencesManager = UserPreferencesManager(application)
                
                return ProfileViewModel(userId, repository, preferencesManager) as T
            }
        }
    }
}
