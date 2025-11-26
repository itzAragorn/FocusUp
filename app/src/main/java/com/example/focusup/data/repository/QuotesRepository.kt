package com.example.focusup.data.repository

import com.example.focusup.data.api.models.Quote
import com.example.focusup.data.api.models.QuoteCategory
import com.example.focusup.data.api.models.QuotesResponse
import com.example.focusup.data.api.network.NetworkModule
import com.example.focusup.data.api.network.NetworkResult
import com.example.focusup.data.api.network.safeApiCall
import com.example.focusup.data.api.service.QuotesApiService
import com.example.focusup.data.local.LocalQuotesProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repository para gestionar las citas motivacionales
 * Actúa como intermediario entre la API externa y la UI
 */
class QuotesRepository(
    private val apiService: QuotesApiService = NetworkModule.quotesApiService
) {
    
    /**
     * Obtener una cita aleatoria con fallback local
     */
    suspend fun getRandomQuote(): NetworkResult<Quote> {
        return try {
            val result = safeApiCall { apiService.getRandomQuote() }
            if (result is NetworkResult.Success) {
                result
            } else {
                // Fallback a cita local
                NetworkResult.Success(LocalQuotesProvider.getRandomQuote())
            }
        } catch (e: Exception) {
            // En caso de cualquier error, usar cita local
            NetworkResult.Success(LocalQuotesProvider.getRandomQuote())
        }
    }
    
    /**
     * Obtener cita aleatoria por categoría
     */
    suspend fun getRandomQuoteByCategory(category: QuoteCategory): NetworkResult<Quote> {
        return safeApiCall { apiService.getRandomQuoteByCategory(category.tag) }
    }
    
    /**
     * Obtener la cita del día con fallback local
     */
    suspend fun getQuoteOfTheDay(): NetworkResult<Quote> {
        return try {
            val result = safeApiCall { 
                val response = apiService.getQuoteOfTheDay()
                if (response.isSuccessful && response.body()?.results?.isNotEmpty() == true) {
                    // Convertir QuotesResponse a Quote individual
                    retrofit2.Response.success(response.body()!!.results.first())
                } else {
                    // Fallback a cita aleatoria motivacional
                    apiService.getRandomQuoteByCategory(QuotesApiService.TAG_MOTIVATIONAL)
                }
            }
            
            if (result is NetworkResult.Success) {
                result
            } else {
                // Fallback a cita local del día
                NetworkResult.Success(LocalQuotesProvider.getQuoteOfTheDay())
            }
        } catch (e: Exception) {
            // En caso de cualquier error, usar cita local del día
            NetworkResult.Success(LocalQuotesProvider.getQuoteOfTheDay())
        }
    }
    
    /**
     * Obtener citas para motivación durante Pomodoro
     */
    suspend fun getPomodoroMotivationQuotes(): NetworkResult<List<Quote>> {
        return safeApiCall {
            val response = apiService.getQuotes(
                tags = "${QuotesApiService.TAG_MOTIVATIONAL},${QuotesApiService.TAG_SUCCESS}",
                limit = 5
            )
            if (response.isSuccessful) {
                retrofit2.Response.success(response.body()?.results ?: emptyList())
            } else {
                response as retrofit2.Response<List<Quote>>
            }
        }
    }
    
    /**
     * Obtener citas para celebrar logros
     */
    suspend fun getAchievementCelebrationQuotes(): NetworkResult<List<Quote>> {
        return safeApiCall {
            val response = apiService.getQuotes(
                tags = "${QuotesApiService.TAG_SUCCESS},${QuotesApiService.TAG_HAPPINESS}",
                limit = 3
            )
            if (response.isSuccessful) {
                retrofit2.Response.success(response.body()?.results ?: emptyList())
            } else {
                response as retrofit2.Response<List<Quote>>
            }
        }
    }
    
    /**
     * Buscar citas por palabra clave
     */
    suspend fun searchQuotes(query: String): NetworkResult<List<Quote>> {
        return safeApiCall {
            val response = apiService.searchQuotes(query)
            if (response.isSuccessful) {
                retrofit2.Response.success(response.body()?.results ?: emptyList())
            } else {
                response as retrofit2.Response<List<Quote>>
            }
        }
    }
    
    /**
     * Flow para obtener cita del día con cache simple
     * Se actualiza una vez por día
     */
    fun getQuoteOfTheDayFlow(): Flow<NetworkResult<Quote>> = flow {
        emit(NetworkResult.Loading())
        
        try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            // Aquí podrías implementar cache local si quieres
            // Por ahora, simplemente obtenemos una cita
            val result = getQuoteOfTheDay()
            emit(result)
            
        } catch (e: Exception) {
            emit(NetworkResult.Error("Error obteniendo cita del día: ${e.localizedMessage}", e))
        }
    }
    
    /**
     * Obtener cita apropiada para el contexto actual
     */
    suspend fun getContextualQuote(context: QuoteContext): NetworkResult<Quote> {
        return when (context) {
            QuoteContext.MORNING_MOTIVATION -> getRandomQuoteByCategory(QuoteCategory.MOTIVATIONAL)
            QuoteContext.POMODORO_BREAK -> getRandomQuoteByCategory(QuoteCategory.WISDOM)
            QuoteContext.TASK_COMPLETION -> getRandomQuoteByCategory(QuoteCategory.SUCCESS)
            QuoteContext.ACHIEVEMENT_UNLOCK -> getRandomQuoteByCategory(QuoteCategory.HAPPINESS)
            QuoteContext.DAILY_INSPIRATION -> getQuoteOfTheDay()
            QuoteContext.GENERAL -> getRandomQuote()
        }
    }
}

/**
 * Contextos para obtener citas apropiadas
 */
enum class QuoteContext {
    MORNING_MOTIVATION,
    POMODORO_BREAK,
    TASK_COMPLETION,
    ACHIEVEMENT_UNLOCK,
    DAILY_INSPIRATION,
    GENERAL
}