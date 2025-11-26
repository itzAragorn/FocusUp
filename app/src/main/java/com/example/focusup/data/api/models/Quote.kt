package com.example.focusup.data.api.models

import com.google.gson.annotations.SerializedName

/**
 * Modelo de datos para una cita motivacional de la API Quotable
 */
data class Quote(
    @SerializedName("_id")
    val id: String,
    
    @SerializedName("content")
    val content: String,
    
    @SerializedName("author")
    val author: String,
    
    @SerializedName("tags")
    val tags: List<String>,
    
    @SerializedName("authorSlug")
    val authorSlug: String? = null,
    
    @SerializedName("length")
    val length: Int = 0,
    
    @SerializedName("dateAdded")
    val dateAdded: String? = null,
    
    @SerializedName("dateModified")
    val dateModified: String? = null
)

/**
 * Respuesta de la API para múltiples citas
 */
data class QuotesResponse(
    @SerializedName("count")
    val count: Int,
    
    @SerializedName("totalCount")
    val totalCount: Int,
    
    @SerializedName("page")
    val page: Int,
    
    @SerializedName("totalPages")
    val totalPages: Int,
    
    @SerializedName("results")
    val results: List<Quote>
)

/**
 * Estado de carga para las citas
 */
sealed class QuoteState {
    object Loading : QuoteState()
    data class Success(val quote: Quote) : QuoteState()
    data class Error(val message: String) : QuoteState()
}

/**
 * Categorías de citas disponibles
 */
enum class QuoteCategory(val tag: String, val displayName: String) {
    MOTIVATIONAL("motivational", "Motivacional"),
    SUCCESS("success", "Éxito"),
    WISDOM("wisdom", "Sabiduría"),
    INSPIRATIONAL("inspirational", "Inspiracional"),
    FAMOUS_QUOTES("famous-quotes", "Citas Famosas"),
    HAPPINESS("happiness", "Felicidad"),
    LIFE("life", "Vida"),
    FRIENDSHIP("friendship", "Amistad")
}