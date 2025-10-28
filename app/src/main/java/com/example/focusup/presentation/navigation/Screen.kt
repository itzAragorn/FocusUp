package com.example.focusup.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object BiometricLock : Screen("biometric_lock")
    object Login : Screen("login")
    object Register : Screen("register")
    object ProfileSetup : Screen("profile_setup")
    object Home : Screen("home")
    object Schedule : Screen("schedule")
    object Calendar : Screen("calendar")
    object AddScheduleBlock : Screen("add_schedule_block?selectedDay={selectedDay}") {
        fun createRoute(selectedDay: Int = 1) = "add_schedule_block?selectedDay=$selectedDay"
    }
    object AddTask : Screen("add_task?selectedDate={selectedDate}") {
        fun createRoute(selectedDate: String = "") = "add_task?selectedDate=$selectedDate"
    }
    object TaskDetail : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Long) = "task_detail/$taskId"
    }
    object ScheduleBlockDetail : Screen("schedule_block_detail/{blockId}") {
        fun createRoute(blockId: Long) = "schedule_block_detail/$blockId"
    }
    object Pomodoro : Screen("pomodoro")
    object Stats : Screen("stats")
    object TaskList : Screen("task_list")
    object Profile : Screen("profile")
}