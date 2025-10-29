package com.example.focusup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.focusup.notifications.NotificationHelper
import com.example.focusup.workers.RecurrenceWorker
import com.example.focusup.ui.theme.FocusUpTheme
import com.example.focusup.utils.UserPreferencesManager

class MainActivity : ComponentActivity() {
    
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        try {
            // Inicializar dependencias
            val database = FocusUpDatabase.getDatabase(this)
            val userRepository = UserRepository(database.userDao())
            val taskRepository = TaskRepository(database.taskDao())
            val scheduleRepository = ScheduleRepository(database.scheduleBlockDao())
            val statsRepository = ProductivityStatsRepository(database.productivityStatsDao())
            val dailyStatsRepository = DailyStatsRepository(database.dailyStatsDao())
            val achievementRepository = AchievementRepository(database.achievementDao())
            val userProgressRepository = UserProgressRepository(database.userProgressDao())
            val notificationHelper = NotificationHelper(this)
            val userPreferencesManager = UserPreferencesManager(this)
            
            // Crear ViewModels manualmente (en un proyecto real usarías Hilt/Dagger)
            authViewModel = AuthViewModel(userRepository, userPreferencesManager)
            
            // *** CREAR GAMIFICATION VIEWMODEL PRIMERO ***
            gamificationViewModel = GamificationViewModel(
                userId = 1L, // TODO: Get from current user
                achievementRepository = achievementRepository,
                userProgressRepository = userProgressRepository,
                notificationHelper = notificationHelper
            )
            
            // *** CREAR OTROS VIEWMODELS CON GAMIFICACIÓN INTEGRADA ***
            taskViewModel = TaskViewModel(taskRepository, dailyStatsRepository, 1L, gamificationViewModel)
            scheduleViewModel = ScheduleViewModel(scheduleRepository)
            scheduleScreenViewModel = ScheduleScreenViewModel(scheduleRepository)
            calendarScreenViewModel = CalendarScreenViewModel(taskRepository)
            homeScreenViewModel = HomeScreenViewModel(taskRepository, scheduleRepository)
            pomodoroViewModel = PomodoroViewModel(notificationHelper, statsRepository, dailyStatsRepository, 1L, gamificationViewModel)
            statsViewModel = StatsViewModel(statsRepository)
            dashboardViewModel = DashboardViewModel(
                userId = 1L, // TODO: Get from current user
                dailyStatsRepository = dailyStatsRepository,
                taskRepository = taskRepository
            )
            
            // Programar RecurrenceWorker para verificar tareas recurrentes diariamente
            scheduleRecurrenceWorker()
            
            setContent {
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
                        gamificationViewModel = gamificationViewModel
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
}