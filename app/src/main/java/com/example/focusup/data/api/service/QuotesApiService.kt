package com.example.focusup.data.api.service

import com.example.focusup.data.api.models.Quote
import com.example.focusup.data.api.models.QuotesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * API Service para consumir la API de Quotable
 * Documentación: https://github.com/lukePeavey/quotable
 */
interface QuotesApiService {
    
    /**
     * Obtener una cita aleatoria
     */
    @GET("random")
    suspend fun getRandomQuote(): Response<Quote>
    
    /**
     * Obtener cita aleatoria por categoría/tags
     */
    @GET("random")
    suspend fun getRandomQuoteByCategory(
        @Query("tags") tags: String
    ): Response<Quote>
    
    /**
     * Obtener múltiples citas
     */
    @GET("quotes")
    suspend fun getQuotes(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("tags") tags: String? = null,
        @Query("author") author: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("order") order: String? = null
    ): Response<QuotesResponse>
    
    /**
     * Obtener cita del día (siempre la misma para una fecha específica)
     */
    @GET("quotes")
    suspend fun getQuoteOfTheDay(
        @Query("tags") tags: String = "motivational",
        @Query("limit") limit: Int = 1,
        @Query("sortBy") sortBy: String = "dateAdded",
        @Query("order") order: String = "asc"
    ): Response<QuotesResponse>
    
    /**
     * Buscar citas por contenido
     */
    @GET("quotes")
    suspend fun searchQuotes(
        @Query("query") query: String,
        @Query("limit") limit: Int = 10
    ): Response<QuotesResponse>
    
    companion object {
        const val BASE_URL = "https://api.quotable.io/"
        
        // Tags más útiles para la app de productividad
        const val TAG_MOTIVATIONAL = "motivational"
        const val TAG_SUCCESS = "success"
        const val TAG_WISDOM = "wisdom"
        const val TAG_INSPIRATIONAL = "inspirational"
        const val TAG_HAPPINESS = "happiness"
        const val TAG_LIFE = "life"
    }
}