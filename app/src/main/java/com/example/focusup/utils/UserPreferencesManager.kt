package com.example.focusup.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesManager(private val context: Context) {
    
    companion object {
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val IS_LOGGED_IN_KEY = stringPreferencesKey("is_logged_in")
        private val BIOMETRIC_ENABLED_KEY = booleanPreferencesKey("biometric_enabled")
    }
    
    val userIdFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[USER_ID_KEY] ?: 0L
    }
    
    val userEmailFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL_KEY] ?: ""
    }
    
    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] == "true"
    }
    
    val biometricEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[BIOMETRIC_ENABLED_KEY] ?: false
    }
    
    suspend fun saveUserSession(userId: Long, email: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email
            preferences[IS_LOGGED_IN_KEY] = "true"
        }
    }
    
    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED_KEY] = enabled
        }
    }
}