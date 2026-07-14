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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.habitsehat.app.data.db.AppDatabase
import com.habitsehat.app.data.model.AppTheme
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
        val repository = HabitRepository(
            db.habitDao(), db.habitLogDao(), db.waterLogDao(),
            db.badHabitDao(), db.badHabitLogDao(), db.pomodoroDao(),
            db.challengeDao(), db.challengeProgressDao()
        )
        val settingsManager = SettingsManager(applicationContext)
        val premiumManager = PremiumManager(settingsManager)
        val homeViewModel = HomeViewModel(repository)
        val statsViewModel = StatsViewModel(repository)
        val badHabitViewModel = BadHabitViewModel(repository)
        val pomodoroViewModel = PomodoroViewModel(repository)
        val weeklyReportViewModel = WeeklyReportViewModel(repository)
        val challengesViewModel = ChallengesViewModel(repository)

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

@Composable
fun MainApp(
    homeViewModel: HomeViewModel,
    statsViewModel: StatsViewModel,
    badHabitViewModel: BadHabitViewModel,
    pomodoroViewModel: PomodoroViewModel,
    weeklyReportViewModel: WeeklyReportViewModel,
    challengesViewModel: ChallengesViewModel,
    repository: HabitRepository,
    settingsManager: SettingsManager,
    premiumManager: PremiumManager,
    isPremium: Boolean,
    currentTheme: AppTheme,
    darkModeSetting: String
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val bottomNavItems = listOf(
        BottomNavItem("Beranda", Icons.Filled.Home, Icons.Outlined.Home, Screen.Home.route),
        BottomNavItem("Statistik", Icons.Filled.BarChart, Icons.Outlined.BarChart, Screen.Stats.route),
        BottomNavItem("HabitStop", Icons.Filled.Block, Icons.Outlined.Block, Screen.HabitStop.route),
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
                    onAddHabit = { navController.navigate(Screen.AddHabit.route) }
                )
            }
            composable(Screen.AddHabit.route) {
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
                    onUpgrade = { navController.navigate(Screen.Premium.route) },
                    onAddBadHabit = { navController.navigate(Screen.AddBadHabit.route) },
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
                    onUpgrade = { navController.navigate(Screen.Premium.route) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Premium.route) {
                PremiumScreen(
                    onUpgrade = {
                        scope.launch { premiumManager.unlockPremium() }
                        navController.popBackStack()
                    },
                    onRestore = { /* TODO */ },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.More.route) {
                MoreScreen(
                    settingsManager = settingsManager,
                    isPremium = isPremium,
                    darkModeSetting = darkModeSetting,
                    onThemeClick = { navController.navigate(Screen.Theme.route) },
                    onPremiumClick = { navController.navigate(Screen.Premium.route) },
                    onPomodoroClick = { navController.navigate(Screen.Pomodoro.route) },
                    onWeeklyReportClick = { navController.navigate(Screen.WeeklyReport.route) },
                    onChallengesClick = { navController.navigate(Screen.Challenges.route) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    repository = repository,
                    settingsManager = settingsManager,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Pomodoro.route) {
                PomodoroScreen(
                    viewModel = pomodoroViewModel,
                    isPremium = isPremium,
                    onUpgrade = { navController.navigate(Screen.Premium.route) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.WeeklyReport.route) {
                WeeklyReportScreen(
                    viewModel = weeklyReportViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Challenges.route) {
                ChallengesScreen(
                    viewModel = challengesViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AddBadHabit.route) {
                AddBadHabitScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
