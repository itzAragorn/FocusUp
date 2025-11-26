package com.example.focusup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import java.util.concurrent.TimeUnit
import com.example.focusup.data.database.FocusUpDatabase
import com.example.focusup.data.repository.UserRepository
import com.example.focusup.data.repository.TaskRepository
import com.example.focusup.data.repository.ScheduleRepository
import com.example.focusup.data.repository.ProductivityStatsRepository
import com.example.focusup.data.repository.DailyStatsRepository
import com.example.focusup.data.repository.AchievementRepository
import com.example.focusup.data.repository.UserProgressRepository
import com.example.focusup.presentation.navigation.FocusUpNavigation
import com.example.focusup.presentation.viewmodels.AuthViewModel
import com.example.focusup.presentation.viewmodels.TaskViewModel
import com.example.focusup.presentation.viewmodels.ScheduleViewModel
import com.example.focusup.presentation.viewmodels.ScheduleScreenViewModel
import com.example.focusup.presentation.viewmodels.CalendarScreenViewModel
import com.example.focusup.presentation.viewmodels.HomeScreenViewModel
import com.example.focusup.presentation.viewmodels.PomodoroViewModel
import com.example.focusup.presentation.viewmodels.StatsViewModel
import com.example.focusup.presentation.viewmodels.DashboardViewModel
import com.example.focusup.presentation.viewmodels.GamificationViewModel
import com.example.focusup.presentation.viewmodels.QuotesViewModel
import com.example.focusup.data.repository.QuotesRepository
import com.example.focusup.notifications.NotificationHelper
import com.example.focusup.workers.RecurrenceWorker
import com.example.focusup.ui.theme.FocusUpTheme
import com.example.focusup.utils.UserPreferencesManager

class MainActivity : ComponentActivity() {
    
    // ViewModels
    private lateinit var authViewModel: AuthViewModel
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var scheduleViewModel: ScheduleViewModel
    private lateinit var scheduleScreenViewModel: ScheduleScreenViewModel
    private lateinit var calendarScreenViewModel: CalendarScreenViewModel
    private lateinit var homeScreenViewModel: HomeScreenViewModel
    private lateinit var pomodoroViewModel: PomodoroViewModel
    private lateinit var statsViewModel: StatsViewModel
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var gamificationViewModel: GamificationViewModel
    private lateinit var quotesViewModel: QuotesViewModel
    
    // Repositorios y dependencias compartidas
    private lateinit var database: FocusUpDatabase
    private lateinit var userRepository: UserRepository
    private lateinit var taskRepository: TaskRepository
    private lateinit var scheduleRepository: ScheduleRepository
    private lateinit var achievementRepository: AchievementRepository
    private lateinit var userProgressRepository: UserProgressRepository
    private lateinit var dailyStatsRepository: DailyStatsRepository
    private lateinit var statsRepository: ProductivityStatsRepository
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var userPreferencesManager: UserPreferencesManager
    private lateinit var quotesRepository: QuotesRepository
    
    // Variable para trackear el userId actual y evitar reinicializaciones innecesarias
    private var currentUserId: Long = 1L
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        try {
            // Inicializar dependencias compartidas
            database = FocusUpDatabase.getDatabase(this)
            userRepository = UserRepository(database.userDao())
            taskRepository = TaskRepository(database.taskDao())
            scheduleRepository = ScheduleRepository(database.scheduleBlockDao())
            statsRepository = ProductivityStatsRepository(database.productivityStatsDao())
            dailyStatsRepository = DailyStatsRepository(database.dailyStatsDao())
            achievementRepository = AchievementRepository(database.achievementDao())
            userProgressRepository = UserProgressRepository(database.userProgressDao())
            notificationHelper = NotificationHelper(this)
            userPreferencesManager = UserPreferencesManager(this)
            quotesRepository = QuotesRepository()
            
            // Crear ViewModels que no dependen del usuario
            authViewModel = AuthViewModel(userRepository, userPreferencesManager)
            scheduleViewModel = ScheduleViewModel(scheduleRepository)
            scheduleScreenViewModel = ScheduleScreenViewModel(scheduleRepository)
            quotesViewModel = QuotesViewModel(quotesRepository)
            
            // Inicializar ViewModels que dependen del usuario con valores por defecto
            // Estos se recrearán cuando el usuario esté autenticado
            initializeUserSpecificViewModels(1L)
            
            // Programar RecurrenceWorker para verificar tareas recurrentes diariamente
            scheduleRecurrenceWorker()
            
            setContent {
                val authUiState by authViewModel.uiState.collectAsState()
                
                // Observar cambios en el usuario autenticado y recrear ViewModels específicos
                LaunchedEffect(authUiState.currentUser?.id) {
                    authUiState.currentUser?.let { user ->
                        // Solo reinicializar si el userId ha cambiado realmente
                        if (user.id != currentUserId) {
                            currentUserId = user.id
                            initializeUserSpecificViewModels(user.id)
                        }
                    }
                }
                
                FocusUpTheme(
                    darkTheme = true, // Forzar tema oscuro con nuestros colores
                    dynamicColor = false // Desactivar colores dinámicos del sistema
                ) {
                    FocusUpNavigation(
                        authViewModel = authViewModel,
                        taskViewModel = taskViewModel,
                        scheduleViewModel = scheduleViewModel,
                        scheduleScreenViewModel = scheduleScreenViewModel,
                        calendarScreenViewModel = calendarScreenViewModel,
                        homeScreenViewModel = homeScreenViewModel,
                        pomodoroViewModel = pomodoroViewModel,
                        statsViewModel = statsViewModel,
                        dashboardViewModel = dashboardViewModel,
                        gamificationViewModel = gamificationViewModel,
                        quotesViewModel = quotesViewModel
                    )
                }
            }
        } catch (e: Exception) {
            // Log the error for debugging
            android.util.Log.e("MainActivity", "Error initializing app", e)
            throw e // Re-throw to see the crash in logcat
        }
    }
    
    /**
     * Programa el worker que mantiene actualizada la ventana de tareas recurrentes
     */
    private fun scheduleRecurrenceWorker() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true) // Solo ejecutar si batería no está baja
            .build()
        
        val recurrenceWorkRequest = PeriodicWorkRequestBuilder<RecurrenceWorker>(
            repeatInterval = 1, // Cada 1 día
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.HOURS) // Esperar 1 hora antes de la primera ejecución
            .build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "recurrence_generator",
            ExistingPeriodicWorkPolicy.KEEP, // Mantener el trabajo existente si ya está programado
            recurrenceWorkRequest
        )
    }
    
    /**
     * Inicializa los ViewModels que dependen del userId específico
     */
    private fun initializeUserSpecificViewModels(userId: Long) {
        currentUserId = userId
        
        // *** CREAR GAMIFICATION VIEWMODEL CON USERID ESPECÍFICO ***
        gamificationViewModel = GamificationViewModel(
            userId = userId,
            achievementRepository = achievementRepository,
            userProgressRepository = userProgressRepository,
            notificationHelper = notificationHelper
        )
        
        // *** CREAR OTROS VIEWMODELS CON GAMIFICACIÓN INTEGRADA ***
        taskViewModel = TaskViewModel(taskRepository, dailyStatsRepository, userId, gamificationViewModel)
        calendarScreenViewModel = CalendarScreenViewModel(taskRepository)
        homeScreenViewModel = HomeScreenViewModel(taskRepository, scheduleRepository)
        pomodoroViewModel = PomodoroViewModel(notificationHelper, statsRepository, dailyStatsRepository, userId, gamificationViewModel)
        statsViewModel = StatsViewModel(statsRepository)
        dashboardViewModel = DashboardViewModel(
            userId = userId,
            dailyStatsRepository = dailyStatsRepository,
            taskRepository = taskRepository
        )
    }
}