package com.example.focusup.utils

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricHelper(private val context: Context) {
    
    private val biometricManager = BiometricManager.from(context)
    
    /**
     * Verifica si el dispositivo puede usar autenticación biométrica
     */
    fun canAuthenticate(): BiometricStatus {
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> 
                BiometricStatus.AVAILABLE
            
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> 
                BiometricStatus.NO_HARDWARE
            
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> 
                BiometricStatus.HARDWARE_UNAVAILABLE
            
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> 
                BiometricStatus.NOT_ENROLLED
            
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                BiometricStatus.SECURITY_UPDATE_REQUIRED
            
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                BiometricStatus.UNSUPPORTED
            
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                BiometricStatus.UNKNOWN
            
            else -> BiometricStatus.UNKNOWN
        }
    }
    
    /**
     * Muestra el diálogo de autenticación biométrica
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String = "Autenticación requerida",
        subtitle: String = "Verifica tu identidad para continuar",
        negativeButtonText: String = "Cancelar",
        onSuccess: () -> Unit,
        onError: (errorCode: Int, errorMessage: String) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(context)
        
        val biometricPrompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }
                
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errorCode, errString.toString())
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailed()
                }
            }
        )
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()
        
        biometricPrompt.authenticate(promptInfo)
    }
    
    /**
     * Obtiene un mensaje descriptivo del estado biométrico
     */
    fun getStatusMessage(status: BiometricStatus): String {
        return when (status) {
            BiometricStatus.AVAILABLE -> 
                "La autenticación biométrica está disponible"
            
            BiometricStatus.NO_HARDWARE -> 
                "Este dispositivo no tiene hardware biométrico"
            
            BiometricStatus.HARDWARE_UNAVAILABLE -> 
                "El hardware biométrico no está disponible en este momento"
            
            BiometricStatus.NOT_ENROLLED -> 
                "No hay biometría registrada. Configúrala en los ajustes del dispositivo"
            
            BiometricStatus.SECURITY_UPDATE_REQUIRED ->
                "Se requiere una actualización de seguridad"
            
            BiometricStatus.UNSUPPORTED ->
                "La autenticación biométrica no es compatible"
            
            BiometricStatus.UNKNOWN ->
                "Estado de biometría desconocido"
        }
    }
    
    enum class BiometricStatus {
        AVAILABLE,
        NO_HARDWARE,
        HARDWARE_UNAVAILABLE,
        NOT_ENROLLED,
        SECURITY_UPDATE_REQUIRED,
        UNSUPPORTED,
        UNKNOWN
    }
}
