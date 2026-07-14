package com.habitsehat.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddHabit : Screen("add_habit")
    data object Stats : Screen("stats")
    data object Theme : Screen("theme")
    data object Premium : Screen("premium")
    data object More : Screen("more")
    data object Settings : Screen("settings")
    data object HabitStop : Screen("habit_stop")
    data object Pomodoro : Screen("pomodoro")
}
