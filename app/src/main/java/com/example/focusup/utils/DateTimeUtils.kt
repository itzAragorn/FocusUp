package com.example.focusup.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    
    private const val DATE_FORMAT = "yyyy-MM-dd"
    private const val TIME_FORMAT = "HH:mm"
    private const val DISPLAY_DATE_FORMAT = "dd/MM/yyyy"
    private const val DISPLAY_TIME_FORMAT = "HH:mm"
    
    private val dateFormatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    private val timeFormatter = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
    private val displayDateFormatter = SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault())
    private val displayTimeFormatter = SimpleDateFormat(DISPLAY_TIME_FORMAT, Locale.getDefault())
    
    fun formatDateForStorage(date: Date): String {
        return dateFormatter.format(date)
    }
    
    fun formatTimeForStorage(date: Date): String {
        return timeFormatter.format(date)
    }
    
    fun formatDateForDisplay(dateString: String): String {
        return try {
            val date = dateFormatter.parse(dateString)
            date?.let { displayDateFormatter.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
    
    fun formatTimeForDisplay(timeString: String): String {
        return try {
            val time = timeFormatter.parse(timeString)
            time?.let { displayTimeFormatter.format(it) } ?: timeString
        } catch (e: Exception) {
            timeString
        }
    }
    
    fun getCurrentDate(): String {
        return formatDateForStorage(Date())
    }
    
    fun getCurrentTime(): String {
        return formatTimeForStorage(Date())
    }
    
    fun parseStoredDate(dateString: String): Date? {
        return try {
            dateFormatter.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    fun parseStoredTime(timeString: String): Date? {
        return try {
            timeFormatter.parse(timeString)
        } catch (e: Exception) {
            null
        }
    }
    
    fun getTodayDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        // Convertir de domingo=1 a lunes=1
        return if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
    }
    
    fun getStartOfWeek(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return formatDateForStorage(calendar.time)
    }
    
    fun getEndOfWeek(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return formatDateForStorage(calendar.time)
    }
    
    fun getStartOfMonth(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return formatDateForStorage(calendar.time)
    }
    
    fun getEndOfMonth(): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return formatDateForStorage(calendar.time)
    }
    
    fun getDayName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1 -> "Lunes"
            2 -> "Martes"
            3 -> "Miércoles"
            4 -> "Jueves"
            5 -> "Viernes"
            6 -> "Sábado"
            7 -> "Domingo"
            else -> ""
        }
    }
    
    fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "Enero"
            2 -> "Febrero"
            3 -> "Marzo"
            4 -> "Abril"
            5 -> "Mayo"
            6 -> "Junio"
            7 -> "Julio"
            8 -> "Agosto"
            9 -> "Septiembre"
            10 -> "Octubre"
            11 -> "Noviembre"
            12 -> "Diciembre"
            else -> ""
        }
    }
}