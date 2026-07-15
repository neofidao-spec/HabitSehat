package com.habitsehat.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddHabit : Screen("add_habit")
    data object EditHabit : Screen("add_habit/")  // dengan argument habitId
    data object AddBadHabit : Screen("add_bad_habit")
    data object Stats : Screen("stats")
    data object Theme : Screen("theme")
    data object Premium : Screen("premium")
    data object More : Screen("more")
    data object Settings : Screen("settings")
    data object HabitStop : Screen("habit_stop")
    data object Pomodoro : Screen("pomodoro")
    data object WeeklyReport : Screen("weekly_report")
    data object Challenges : Screen("challenges")
    data object Expense : Screen("expense")
    data object AddExpense : Screen("add_expense")
    data object EditExpense : Screen("add_expense/")  // dengan argument expenseId
    data object ExpenseCategories : Screen("expense_categories")
    data object ExpenseReport : Screen("expense_report")
}