package com.example.focusup.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.focusup.data.db.DatabaseProvider
import com.example.focusup.data.model.UserEntity
import com.example.focusup.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository: UserRepository
    private val _currentUser = MutableLiveData<UserEntity?>()
    val currentUser: LiveData<UserEntity?> get() = _currentUser

    init {
        val db = DatabaseProvider.getDatabase(application)
        userRepository = UserRepository(db.userDao())
    }

    fun registerUser(user: UserEntity) {
        viewModelScope.launch {
            userRepository.registerUser(user)
        }
    }

    fun login(correo: String, password: String) {
        viewModelScope.launch {
            val user = userRepository.loginUser(correo, password)
            _currentUser.postValue(user)
        }
    }

    fun logout() {
        _currentUser.postValue(null)
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            userRepository.updateUser(user)
        }
    }

    fun getUserByEmail(correo: String) = liveData {
        emit(userRepository.getUserByEmail(correo))
    }
}