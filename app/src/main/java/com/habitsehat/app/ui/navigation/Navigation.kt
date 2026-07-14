package com.habitsehat.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddHabit : Screen("add_habit")
    data object Stats : Screen("stats")
    data object Settings : Screen("settings")
    data object EditHabit : Screen("edit_habit/{habitId}") {
        fun createRoute(habitId: Long) = "edit_habit/$habitId"
    }
}
