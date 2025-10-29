package com.example.focusup.presentation.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.focusup.presentation.screens.LoginScreen
import com.example.focusup.presentation.screens.RegisterScreen
import com.example.focusup.presentation.screens.BiometricLockScreen
import com.example.focusup.presentation.screens.HomeScreen
import com.example.focusup.presentation.screens.ScheduleScreen
import com.example.focusup.presentation.screens.CalendarScreen
import com.example.focusup.presentation.screens.AddTaskScreen
import com.example.focusup.presentation.screens.AddScheduleBlockScreen
import com.example.focusup.presentation.screens.SplashScreen
import com.example.focusup.presentation.screens.PomodoroScreen
import com.example.focusup.presentation.screens.StatsScreen
import com.example.focusup.presentation.screens.TaskListScreen
import com.example.focusup.presentation.screens.ProfileScreen
import com.example.focusup.presentation.screens.DashboardScreen
import com.example.focusup.presentation.screens.AchievementScreen
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
import com.example.focusup.utils.BiometricHelper
import com.example.focusup.utils.UserPreferencesManager
import androidx.compose.ui.platform.LocalContext

@Composable
fun FocusUpNavigation(
    authViewModel: AuthViewModel,
    taskViewModel: TaskViewModel,
    scheduleViewModel: ScheduleViewModel,
    scheduleScreenViewModel: ScheduleScreenViewModel,
    calendarScreenViewModel: CalendarScreenViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    pomodoroViewModel: PomodoroViewModel,
    statsViewModel: StatsViewModel,
    dashboardViewModel: DashboardViewModel,
    gamificationViewModel: GamificationViewModel,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val authUiState by authViewModel.uiState.collectAsState()
    val preferencesManager = remember { UserPreferencesManager(context) }
    val biometricEnabled by preferencesManager.biometricEnabledFlow.collectAsState(initial = false)
    val biometricHelper = remember { BiometricHelper(context) }
    var showSplash by remember { mutableStateOf(true) }
    
    // Determinar la pantalla inicial basada en el estado de autenticación
    val startDestination = if (showSplash) {
        Screen.Splash.route
    } else if (authUiState.isLoggedIn) {
        // Si está logueado y tiene biometría activada, mostrar pantalla de bloqueo
        if (biometricEnabled && biometricHelper.canAuthenticate() == BiometricHelper.BiometricStatus.AVAILABLE) {
            Screen.BiometricLock.route
        } else {
            Screen.Home.route
        }
    } else {
        Screen.Login.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    showSplash = false
                    val destination = if (authUiState.isLoggedIn) {
                        // Si está logueado y tiene biometría activada, ir a pantalla de bloqueo
                        if (biometricEnabled && biometricHelper.canAuthenticate() == BiometricHelper.BiometricStatus.AVAILABLE) {
                            Screen.BiometricLock.route
                        } else {
                            Screen.Home.route
                        }
                    } else {
                        Screen.Login.route
                    }
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
        composable(Screen.BiometricLock.route) {
            BiometricLockScreen(
                userName = authUiState.currentUser?.name ?: "Usuario",
                onAuthenticated = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.BiometricLock.route) {
                            inclusive = true
                        }
                    }
                },
                onUsePassword = {
                    // Cerrar sesión y volver al login
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                    }
                },
                isLoading = authUiState.isLoading,
                errorMessage = authUiState.errorMessage
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegister = { name, email, password, profileType ->
                    authViewModel.register(name, email, password, profileType)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                isLoading = authUiState.isLoading,
                errorMessage = authUiState.errorMessage
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                user = authUiState.currentUser,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToSchedule = {
                    navController.navigate(Screen.Schedule.route)
                },
                onNavigateToCalendar = {
                    navController.navigate(Screen.Calendar.route)
                },
                onNavigateToPomodoro = {
                    navController.navigate(Screen.Pomodoro.route)
                },
                onNavigateToStats = {
                    navController.navigate(Screen.Stats.route)
                },
                onNavigateToTaskList = {
                    navController.navigate(Screen.TaskList.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route)
                },
                homeScreenViewModel = homeScreenViewModel
            )
        }
        
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                user = authUiState.currentUser,
                dashboardViewModel = dashboardViewModel,
                gamificationViewModel = gamificationViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToPomodoro = {
                    navController.navigate(Screen.Pomodoro.route)
                },
                onNavigateToTasks = {
                    navController.navigate(Screen.TaskList.route)
                },
                onNavigateToSchedule = {
                    navController.navigate(Screen.Schedule.route)
                },
                onNavigateToAchievement = {
                    navController.navigate(Screen.Achievement.route)
                }
            )
        }
        
        composable(Screen.Schedule.route) {
            ScheduleScreen(
                user = authUiState.currentUser,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToCalendar = {
                    navController.navigate(Screen.Calendar.route)
                },
                onAddScheduleBlock = { selectedDay ->
                    navController.navigate(Screen.AddScheduleBlock.createRoute(selectedDay))
                },
                scheduleScreenViewModel = scheduleScreenViewModel,
                scheduleViewModel = scheduleViewModel
            )
        }
        
        composable(Screen.Calendar.route) {
            CalendarScreen(
                user = authUiState.currentUser,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToSchedule = {
                    navController.navigate(Screen.Schedule.route)
                },
                onAddTask = { selectedDate ->
                    navController.navigate(Screen.AddTask.createRoute(selectedDate))
                },
                calendarScreenViewModel = calendarScreenViewModel,
                taskViewModel = taskViewModel
            )
        }
        
        composable(
            route = Screen.AddTask.route,
            arguments = listOf(
                navArgument("selectedDate") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            authUiState.currentUser?.let { user ->
                val selectedDate = backStackEntry.arguments?.getString("selectedDate")
                AddTaskScreen(
                    userId = user.id,
                    selectedDate = selectedDate?.takeIf { it.isNotBlank() },
                    onTaskCreated = {
                        taskViewModel.clearState()
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        taskViewModel.clearState()
                        navController.popBackStack()
                    },
                    taskViewModel = taskViewModel
                )
            }
        }
        
        composable(
            route = Screen.AddScheduleBlock.route,
            arguments = listOf(
                navArgument("selectedDay") {
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ) { backStackEntry ->
            val selectedDay = backStackEntry.arguments?.getInt("selectedDay") ?: 1
            AddScheduleBlockScreen(
                user = authUiState.currentUser,
                selectedDay = selectedDay,
                onScheduleBlockCreated = {
                    scheduleViewModel.clearState()
                    navController.popBackStack()
                },
                onNavigateBack = {
                    scheduleViewModel.clearState()
                    navController.popBackStack()
                },
                scheduleViewModel = scheduleViewModel
            )
        }
        
        composable(Screen.Pomodoro.route) {
            authUiState.currentUser?.let { user ->
                PomodoroScreen(
                    pomodoroViewModel = pomodoroViewModel,
                    userId = user.id,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable(Screen.Stats.route) {
            authUiState.currentUser?.let { user ->
                StatsScreen(
                    statsViewModel = statsViewModel,
                    userId = user.id,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable(Screen.TaskList.route) {
            authUiState.currentUser?.let { user ->
                TaskListScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onAddTask = {
                        navController.navigate(Screen.AddTask.createRoute())
                    },
                    calendarScreenViewModel = calendarScreenViewModel,
                    taskViewModel = taskViewModel,
                    userId = user.id
                )
            }
        }
        
        composable(Screen.Profile.route) {
            authUiState.currentUser?.let { user ->
                ProfileScreen(
                    userId = user.id,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
        
        composable(Screen.Achievement.route) {
            authUiState.currentUser?.let { user ->
                AchievementScreen(
                    user = user,
                    gamificationViewModel = gamificationViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
    
    // Observer para navegación automática después del login/registro
    LaunchedEffect(authUiState.isLoggedIn) {
        if (authUiState.isLoggedIn && navController.currentDestination?.route != Screen.Home.route) {
            navController.navigate(Screen.Home.route) {
                popUpTo(0) {
                    inclusive = true
                }
            }
        }
    }
    
    // Limpiar errores cuando se navega
    LaunchedEffect(navController.currentDestination) {
        authViewModel.clearError()
    }
}