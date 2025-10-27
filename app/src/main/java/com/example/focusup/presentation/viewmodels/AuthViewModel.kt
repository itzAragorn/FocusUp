package com.example.focusup.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusup.data.database.entities.User
import com.example.focusup.data.repository.UserRepository
import com.example.focusup.utils.UserPreferencesManager
import com.example.focusup.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val currentUser: User? = null
)

class AuthViewModel(
    private val userRepository: UserRepository,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    init {
        checkLoginStatus()
    }
    
    private fun checkLoginStatus() {
        viewModelScope.launch {
            userPreferencesManager.isLoggedInFlow.collect { isLoggedIn ->
                try {
                    if (isLoggedIn) {
                        val userId = userPreferencesManager.userIdFlow.first()
                        if (userId > 0) {
                            val user = userRepository.getUserById(userId)
                            _uiState.value = _uiState.value.copy(
                                isLoggedIn = true,
                                currentUser = user
                            )
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoggedIn = false,
                            currentUser = null
                        )
                    }
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        isLoggedIn = false,
                        currentUser = null,
                        errorMessage = "Error al verificar el estado de la sesión."
                    )
                }
            }
        }
    }
    
    fun login(email: String, password: String) {
        val emailError = ValidationUtils.getEmailErrorMessage(email)
        val passwordError = ValidationUtils.getPasswordErrorMessage(password)
        
        if (emailError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = emailError)
            return
        }
        
        if (passwordError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = passwordError)
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val user = userRepository.loginUser(email, password)
                if (user != null) {
                    userPreferencesManager.saveUserSession(user.id, user.email)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Credenciales incorrectas"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al iniciar sesión: ${e.message}"
                )
            }
        }
    }
    
    fun register(name: String, email: String, password: String, profileType: String) {
        val nameError = ValidationUtils.getNameErrorMessage(name)
        val emailError = ValidationUtils.getEmailErrorMessage(email)
        val passwordError = ValidationUtils.getPasswordErrorMessage(password)
        
        if (nameError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = nameError)
            return
        }
        
        if (emailError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = emailError)
            return
        }
        
        if (passwordError != null) {
            _uiState.value = _uiState.value.copy(errorMessage = passwordError)
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val user = User(
                    name = name,
                    email = email,
                    password = password, // En una app real, hashear la contraseña
                    profileType = profileType
                )
                
                val result = userRepository.registerUser(user)
                result.fold(
                    onSuccess = { userId ->
                        val newUser = user.copy(id = userId)
                        userPreferencesManager.saveUserSession(userId, email)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            currentUser = newUser,
                            errorMessage = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error al registrar usuario"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al registrar: ${e.message}"
                )
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            userPreferencesManager.clearUserSession()
            _uiState.value = AuthUiState()
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}