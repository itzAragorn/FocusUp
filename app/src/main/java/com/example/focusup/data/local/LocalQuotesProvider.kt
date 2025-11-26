package com.example.focusup.data.local

import com.example.focusup.data.api.models.Quote

/**
 * Proveedor de citas locales como fallback cuando la API externa falla
 * Contiene citas motivacionales en español para productividad
 */
object LocalQuotesProvider {
    
    private val motivationalQuotes = listOf(
        Quote(
            id = "local-1",
            content = "El éxito es la suma de pequeños esfuerzos repetidos día tras día.",
            author = "Robert Collier",
            tags = listOf("motivational", "success"),
            authorSlug = "robert-collier",
            length = 65,
            dateAdded = "2024-01-01",
            dateModified = "2024-01-01"
        ),
        Quote(
            id = "local-2",
            content = "La concentración es la clave que abre todas las puertas del éxito.",
            author = "Proverbio",
            tags = listOf("focus", "success"),
            authorSlug = "proverbio",
            length = 68,
            dateAdded = "2024-01-01",
            dateModified = "2024-01-01"
        ),
        Quote(
            id = "local-3",
            content = "No esperes el momento perfecto, toma el momento y hazlo perfecto.",
            author = "Zoey Sayward",
            tags = listOf("motivational", "action"),
            authorSlug = "zoey-sayward",
            length = 66,
            dateAdded = "2024-01-01",
            dateModified = "2024-01-01"
        ),
        Quote(
            id = "local-4",
            content = "La productividad no es hacer más cosas, sino hacer las cosas correctas.",
            author = "Tim Ferriss",
            tags = listOf("productivity", "wisdom"),
            authorSlug = "tim-ferriss",
            length = 72,
            dateAdded = "2024-01-01",
            dateModified = "2024-01-01"
        ),
        Quote(
            id = "local-5",
            content = "Un objetivo sin un plan es solo un deseo.",
            author = "Antoine de Saint-Exupéry",
            tags = listOf("planning", "goals"),
            authorSlug = "antoine-de-saint-exupery",
            length = 40,
            dateAdded = "2024-01-01",
            dateModified = "2024-01-01"
        ),
        Quote(
            id = "local-6",
            content = "El tiempo es el recurso más valioso que tenemos, úsalo sabiamente.",
            author = "Steve Jobs",
            tags = listOf("time-management", "wisdom"),
            authorSlug = "steve-jobs",
            length = 66,
            dateAdded = "2024-01-01",
            dateModified = "2024-01-01"
        ),
        Quote(
            id = "local-7",
            content = "El único modo de hacer un gran trabajo es amar lo que haces.",
            author = "Steve Jobs",
            tags = listOf("passion", "work"),
            authorSlug = "steve-jobs",
            length = 58,
            dateAdded = "2024-01-01",
            dateModified = "2024-01-01"
        ),
        Quote(
            id = "local-8",
            content = "La disciplina es elegir entre lo que quieres ahora y lo que quieres más.",
            author = "Abraham Lincoln",
            tags = listOf("discipline", "choices"),
            authorSlug = "abraham-lincoln",
            length = 73,
            dateAdded = "2024-01-01",
            dateModified = "2024-01-01"
        ),
        Quote(
            id = "local-9",
            content = "Enfócate en ser productivo en lugar de estar ocupado.",
            author = "Tim Ferriss",
            tags = listOf("focus", "productivity"),
            authorSlug = "tim-ferriss",
            length = 53,
            dateAdded = "2024-01-01",
            dateModified = "2024-01-01"
        ),
        Quote(
            id = "local-10",
            content = "El progreso, no la perfección, es el objetivo.",
            author = "Winston Churchill",
            tags = listOf("progress", "mindset"),
            authorSlug = "winston-churchill",
            length = 45,
            dateAdded = "2024-01-01",
            dateModified = "2024-01-01"
        )
    )
    
    /**
     * Obtiene una cita aleatoria de las citas locales
     */
    fun getRandomQuote(): Quote {
        return motivationalQuotes.random()
    }
    
    /**
     * Obtiene la "cita del día" basada en la fecha actual
     * Usa el día del año para mantener consistencia
     */
    fun getQuoteOfTheDay(): Quote {
        val dayOfYear = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
        val index = dayOfYear % motivationalQuotes.size
        return motivationalQuotes[index]
    }
    
    /**
     * Obtiene citas por categoría/tags
     */
    fun getQuotesByTag(tag: String): List<Quote> {
        return motivationalQuotes.filter { quote ->
            quote.tags.any { it.contains(tag, ignoreCase = true) }
        }
    }
    
    /**
     * Obtiene todas las citas locales
     */
    fun getAllQuotes(): List<Quote> {
        return motivationalQuotes
    }
}
