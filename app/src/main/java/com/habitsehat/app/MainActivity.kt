package com.habitsehat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.habitsehat.app.data.db.AppDatabase
import com.habitsehat.app.data.repository.HabitRepository
import com.habitsehat.app.ui.navigation.Screen
import com.habitsehat.app.ui.screens.AddHabitScreen
import com.habitsehat.app.ui.screens.HomeScreen
import com.habitsehat.app.ui.screens.HomeViewModel
import com.habitsehat.app.ui.theme.HabitSehatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val repository = HabitRepository(db.habitDao(), db.habitLogDao(), db.waterLogDao())
        val viewModel = HomeViewModel(repository)

        setContent {
            HabitSehatTheme {
                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(homeViewModel: HomeViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onAddHabit = { navController.navigate(Screen.AddHabit.route) }
            )
        }
        composable(Screen.AddHabit.route) {
            AddHabitScreen(
                onSave = { habit ->
                    // TODO: save via viewModel
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
