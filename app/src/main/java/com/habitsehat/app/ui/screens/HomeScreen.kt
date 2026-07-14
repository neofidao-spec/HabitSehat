package com.habitsehat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.ui.components.HabitItem
import com.habitsehat.app.ui.components.StreakBar
import com.habitsehat.app.ui.components.WaterCard

data class HabitWithState(
    val habit: Habit,
    val isCheckedToday: Boolean = false,
    val todayCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddHabit: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("HabitSehat", fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah habit")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Greeting + date
                item {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Halo!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        java.time.LocalDate.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Water Card
                item {
                    WaterCard(
                        total = state.waterTotal,
                        goal = state.waterGoal,
                        onAdd = { viewModel.addWater(it) },
                        onUndo = { viewModel.undoWater() }
                    )
                }

                // Streak bar
                item {
                    StreakBar(done = state.habitsDone, total = state.habitsTotal)
                }

                // Section header
                item {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Kebiasaan Hari Ini",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Empty state
                if (state.habits.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Outlined.FavoriteBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "Belum ada kebiasaan",
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Tekan + untuk tambah kebiasaan baru",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                // Habit list
                items(state.habits, key = { it.id }) { habit ->
                    HabitItem(
                        habit = habit,
                        isChecked = false,
                        currentCount = 0,
                        onCheck = { viewModel.checkHabit(habit.id) },
                        onArchive = { viewModel.archiveHabit(habit.id) }
                    )
                }
            }
        }
    }
}
