package com.example.focusup.data.api.network

import com.example.focusup.data.api.service.QuotesApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Configuración de red para Retrofit
 * Maneja la creación del cliente HTTP y la instancia de Retrofit
 */
object NetworkModule {
    
    /**
     * Cliente HTTP simple y robusto
     */
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    /**
     * Instancia de Retrofit configurada para la API de Quotable
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(QuotesApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * Servicio de la API de citas
     */
    val quotesApiService: QuotesApiService by lazy {
        retrofit.create(QuotesApiService::class.java)
    }
}

/**
 * Resultado de operaciones de red
 */
sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String, val exception: Throwable? = null) : NetworkResult<T>()
    class Loading<T> : NetworkResult<T>()
}

/**
 * Extensión para manejo seguro de llamadas a la API
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> retrofit2.Response<T>
): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let { body ->
                NetworkResult.Success(body)
            } ?: NetworkResult.Error("Respuesta vacía del servidor")
        } else {
            val errorMessage = when (response.code()) {
                404 -> "Servicio no encontrado"
                500 -> "Error interno del servidor"
                503 -> "Servicio no disponible"
                else -> "Error ${response.code()}: ${response.message()}"
            }
            NetworkResult.Error(errorMessage)
        }
    } catch (e: Exception) {
        val errorMessage = when {
            e.message?.contains("Chain validation failed") == true -> 
                "Error de certificado SSL. Intenta conectarte a WiFi diferente."
            e.message?.contains("timeout") == true -> 
                "Tiempo de espera agotado. Verifica tu conexión."
            e.message?.contains("UnknownHostException") == true -> 
                "No se puede conectar al servidor. Verifica tu internet."
            e.message?.contains("SSLException") == true -> 
                "Error de seguridad SSL. Intenta con conexión diferente."
            else -> "Error de conexión: ${e.localizedMessage ?: "Problema de red"}"
        }
        NetworkResult.Error(errorMessage, e)
    }
}