package com.habitsehat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.habitsehat.app.data.db.AppDatabase
import com.habitsehat.app.data.db.ExpenseWithCategory
import com.habitsehat.app.data.model.AppTheme
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.preferences.PremiumManager
import com.habitsehat.app.data.preferences.SettingsManager
import com.habitsehat.app.data.repository.HabitRepository
import com.habitsehat.app.ui.navigation.Screen
import com.habitsehat.app.ui.screens.*
import com.habitsehat.app.ui.theme.HabitSehatTheme
import kotlinx.coroutines.launch

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val repository = HabitRepository(db)
        val settingsManager = SettingsManager(applicationContext)
        val premiumManager = PremiumManager(settingsManager)
        val homeViewModel = HomeViewModel(repository)
        val statsViewModel = StatsViewModel(repository)
        val badHabitViewModel = BadHabitViewModel(repository)
        val pomodoroViewModel = PomodoroViewModel(repository)
        val weeklyReportViewModel = WeeklyReportViewModel(repository)
        val challengesViewModel = ChallengesViewModel(repository)
        val expenseViewModel = ExpenseViewModel(repository)

        setContent {
            val currentTheme by settingsManager.currentTheme.collectAsState(initial = AppTheme.getThemeById("mint"))
            val isPremium by settingsManager.isPremium.collectAsState(initial = false)
            val darkModeSetting by settingsManager.darkModeSetting.collectAsState(initial = "system")
            val isDark = when (darkModeSetting) {
                "dark" -> true
                "light" -> false
                else -> isSystemInDarkTheme()
            }

            HabitSehatTheme(
                appTheme = currentTheme,
                darkTheme = isDark
            ) {
                MainApp(
                    homeViewModel = homeViewModel,
                    statsViewModel = statsViewModel,
                    badHabitViewModel = badHabitViewModel,
                    pomodoroViewModel = pomodoroViewModel,
                    weeklyReportViewModel = weeklyReportViewModel,
                    challengesViewModel = challengesViewModel,
                    expenseViewModel = expenseViewModel,
                    repository = repository,
                    settingsManager = settingsManager,
                    premiumManager = premiumManager,
                    isPremium = isPremium,
                    currentTheme = currentTheme,
                    darkModeSetting = darkModeSetting
                )
            }
        }
    }
}

private val screenIn = slideInHorizontally { it / 4 } + fadeIn(animationSpec = tween(300))
private val screenOut = slideOutHorizontally { it / 4 } + fadeOut(animationSpec = tween(300))

@Composable
fun MainApp(
    homeViewModel: HomeViewModel,
    statsViewModel: StatsViewModel,
    badHabitViewModel: BadHabitViewModel,
    pomodoroViewModel: PomodoroViewModel,
    weeklyReportViewModel: WeeklyReportViewModel,
    challengesViewModel: ChallengesViewModel,
    expenseViewModel: ExpenseViewModel,
    repository: HabitRepository,
    settingsManager: SettingsManager,
    premiumManager: PremiumManager,
    isPremium: Boolean,
    currentTheme: AppTheme,
    darkModeSetting: String
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val isFirstLaunch by settingsManager.isFirstLaunch.collectAsState(initial = true)
    var showOnboarding by remember { mutableStateOf(false) }

    LaunchedEffect(isFirstLaunch) {
        showOnboarding = isFirstLaunch
    }

    if (showOnboarding) {
        OnboardingScreen(
            onComplete = {
                scope.launch {
                    settingsManager.setFirstLaunchComplete()
                    showOnboarding = false
                }
            }
        )
    } else {

    val bottomNavItems = listOf(
        BottomNavItem("Beranda", Icons.Filled.Home, Icons.Outlined.Home, Screen.Home.route),
        BottomNavItem("Statistik", Icons.Filled.BarChart, Icons.Outlined.BarChart, Screen.Stats.route),
        BottomNavItem("HabitStop", Icons.Filled.Block, Icons.Outlined.Block, Screen.HabitStop.route),
        BottomNavItem("Pengeluaran", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, Screen.Expense.route),
        BottomNavItem("Tema", Icons.Filled.Palette, Icons.Outlined.Palette, Screen.Theme.route),
        BottomNavItem("Lainnya", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz, Screen.More.route)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    repository = repository,
                    navController = navController,
                    onAddHabit = { navController.navigate(Screen.AddHabit.route) { launchSingleTop = true } }
                )
            }
            composable(Screen.AddHabit.route, enterTransition = { screenIn }, exitTransition = { screenOut }) {
                AddHabitScreen(
                    onSave = { habit ->
                        homeViewModel.saveHabit(habit)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Stats.route) {
                StatsScreen(
                    viewModel = statsViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.HabitStop.route) {
                HabitStopScreen(
                    viewModel = badHabitViewModel,
                    isPremium = isPremium,
                    onUpgrade = { navController.navigate(Screen.Premium.route) { launchSingleTop = true } },
                    onAddBadHabit = { navController.navigate(Screen.AddBadHabit.route) { launchSingleTop = true } },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Theme.route) {
                ThemeScreen(
                    settingsManager = settingsManager,
                    currentTheme = currentTheme,
                    isPremium = isPremium,
                    onSelectTheme = { theme ->
                        scope.launch { settingsManager.setTheme(theme.id) }
                    },
                    onUpgrade = { navController.navigate(Screen.Premium.route) { launchSingleTop = true } },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Premium.route, enterTransition = { screenIn }, exitTransition = { screenOut }) {
                PremiumScreen(
                    onUpgrade = { plan ->
                        scope.launch { premiumManager.unlockPremium() }
                        navController.popBackStack()
                    },
                    onRestore = { scope.launch { premiumManager.restorePurchases(); navController.popBackStack() } },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.More.route) {
                MoreScreen(
                    settingsManager = settingsManager,
                    isPremium = isPremium,
                    darkModeSetting = darkModeSetting,
                    onThemeClick = { navController.navigate(Screen.Theme.route) { launchSingleTop = true } },
                    onPremiumClick = { navController.navigate(Screen.Premium.route) { launchSingleTop = true } },
                    onPomodoroClick = { navController.navigate(Screen.Pomodoro.route) { launchSingleTop = true } },
                    onWeeklyReportClick = { navController.navigate(Screen.WeeklyReport.route) { launchSingleTop = true } },
                    onChallengesClick = { navController.navigate(Screen.Challenges.route) { launchSingleTop = true } },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) { launchSingleTop = true } },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route, enterTransition = { screenIn }, exitTransition = { screenOut }) {
                SettingsScreen(
                    repository = repository,
                    settingsManager = settingsManager,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Pomodoro.route, enterTransition = { screenIn }, exitTransition = { screenOut }) {
                PomodoroScreen(
                    viewModel = pomodoroViewModel,
                    isPremium = isPremium,
                    onUpgrade = { navController.navigate(Screen.Premium.route) { launchSingleTop = true } },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.WeeklyReport.route, enterTransition = { screenIn }, exitTransition = { screenOut }) {
                WeeklyReportScreen(
                    viewModel = weeklyReportViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Challenges.route, enterTransition = { screenIn }, exitTransition = { screenOut }) {
                ChallengesScreen(
                    viewModel = challengesViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AddBadHabit.route, enterTransition = { screenIn }, exitTransition = { screenOut }) {
                AddBadHabitScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "add_habit/{habitId}",
                arguments = listOf(navArgument("habitId") { type = NavType.LongType }),
                enterTransition = { screenIn },
                exitTransition = { screenOut }
            ) { backStackEntry ->
                val habitId = backStackEntry.arguments?.getLong("habitId") ?: 0L
                if (habitId > 0) {
                    val habit by androidx.compose.runtime.produceState<Habit?>(null) {
                        value = repository.getHabitById(habitId)
                    }
                    AddHabitScreen(
                        onSave = { habit ->
                            homeViewModel.saveHabit(habit)
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() },
                        habitToEdit = habit
                    )
                } else {
                    navController.popBackStack()
                }
            }
            composable(Screen.Expense.route) {
                ExpenseScreen(
                    viewModel = expenseViewModel,
                    repository = repository,
                    navController = navController,
                    onNavigateToAdd = { navController.navigate(Screen.AddExpense.route) { launchSingleTop = true } },
                    onNavigateToCategories = { navController.navigate(Screen.ExpenseCategories.route) { launchSingleTop = true } },
                    onNavigateToReport = { navController.navigate("expense_report") { launchSingleTop = true } }
                )
            }
            composable(Screen.AddExpense.route, enterTransition = { screenIn }, exitTransition = { screenOut }) {
                AddExpenseScreen(
                    viewModel = expenseViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "add_expense/{expenseId}",
                arguments = listOf(navArgument("expenseId") { type = NavType.LongType }),
                enterTransition = { screenIn },
                exitTransition = { screenOut }
            ) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getLong("expenseId") ?: 0L
                if (expenseId > 0) {
                    // Load expense using produceState for suspend function
                    val expenseWithCat by androidx.compose.runtime.produceState<ExpenseWithCategory?>(null) {
                        val exp = repository.getExpenseById(expenseId)
                        if (exp != null) {
                            val cat = repository.getExpenseCategoryById(exp.categoryId)
                            value = ExpenseWithCategory(expense = exp, expenseCategory = cat)
                        }
                    }
                    AddExpenseScreen(
                        viewModel = expenseViewModel,
                        expenseToEdit = expenseWithCat,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            composable(Screen.ExpenseCategories.route, enterTransition = { screenIn }, exitTransition = { screenOut }) {
                ExpenseCategoriesScreen(
                    viewModel = expenseViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ExpenseReport.route, enterTransition = { screenIn }, exitTransition = { screenOut }) {
                ExpenseReportScreen(
                    viewModel = expenseViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
    }
}
