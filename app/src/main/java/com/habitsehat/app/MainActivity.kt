package com.habitsehat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.habitsehat.app.data.db.AppDatabase
import com.habitsehat.app.data.repository.HabitRepository
import com.habitsehat.app.ui.navigation.Screen
import com.habitsehat.app.ui.screens.*
import com.habitsehat.app.ui.theme.HabitSehatTheme

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
        val repository = HabitRepository(db.habitDao(), db.habitLogDao(), db.waterLogDao())
        val homeViewModel = HomeViewModel(repository)
        val statsViewModel = StatsViewModel(repository)

        setContent {
            HabitSehatTheme {
                MainApp(homeViewModel, statsViewModel, repository)
            }
        }
    }
}

@Composable
fun MainApp(
    homeViewModel: HomeViewModel,
    statsViewModel: StatsViewModel,
    repository: HabitRepository
) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem("Beranda", Icons.Filled.Home, Icons.Outlined.Home, Screen.Home.route),
        BottomNavItem("Statistik", Icons.Filled.BarChart, Icons.Outlined.BarChart, Screen.Stats.route),
        BottomNavItem("Pengaturan", Icons.Filled.Settings, Icons.Outlined.Settings, Screen.Settings.route)
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
                            label = { Text(item.label, fontSize = androidx.compose.ui.unit.sp(11)) }
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
            composable(Screen.Settings.route) {
                SettingsScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
