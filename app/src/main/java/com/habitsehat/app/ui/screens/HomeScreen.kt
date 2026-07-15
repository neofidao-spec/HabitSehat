package com.habitsehat.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import com.habitsehat.app.ui.components.HabitItem
import com.habitsehat.app.ui.components.StreakBar
import com.habitsehat.app.ui.components.WaterCard
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onAddHabit: () -> Unit,
    onEditHabit: (Long) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val checkedStates by viewModel.checkedStates.collectAsStateWithLifecycle()
    val habitCounts by viewModel.habitCounts.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    // Dynamic greeting
    val hour = java.time.LocalTime.now().hour
    val greeting = when {
        hour < 10 -> "Selamat Pagi ☀️"
        hour < 15 -> "Selamat Siang 🌤"
        hour < 18 -> "Selamat Sore 🌅"
        else -> "Selamat Malam 🌙"
    }

    // Motivational quote
    val quotes = listOf(
        "Kecil konsisten, besar hasilnya 💪",
        "Hari ini lebih baik dari kemarin 📈",
        "Satu langkah lebih dekat ke tujuan 🎯",
        "Kebiasaan baik adalah investasi diri 🌱",
        "Kamu sudah sejauh ini, jangan berhenti 🔥",
        "Sukses dimulai dari kebiasaan kecil ✨",
        "Jangan tunda, lakukan sekarang ⚡",
        "Progress > Perfection 🚀"
    )

    // Rotating quote
    var quoteIndex by remember { mutableIntStateOf(0) }
    val visibleQuote by animateIntAsState(
        targetValue = quoteIndex,
        animationSpec = tween(400),
        label = "quoteAnim"
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(6000)
            quoteIndex = if (quoteIndex + 1 < quotes.size) quoteIndex + 1 else 0
        }
    }

    // Animated empty state icon
    val infiniteTransition = rememberInfiniteTransition(label = "emptyFloat")
    val floatY by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("HabitSehat", fontWeight = FontWeight.Bold)
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
                // Greeting + date + quote
                item {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        greeting,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy")),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))

                    // Animated rotating quote
                    KeyedContainer(
                        key = visibleQuote,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.FormatQuote,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                quotes[visibleQuote],
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
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

                // Empty state (animated)
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
                                    .padding(40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                            shape = androidx.compose.foundation.shape.CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier.graphicsLayer {
                                            translationY = floatY
                                        }
                                    ) {
                                        Icon(
                                            Icons.Outlined.FavoriteBorder,
                                            contentDescription = null,
                                            modifier = Modifier.size(36.dp),
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "Belum ada kebiasaan",
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Tekan + untuk tambah kebiasaan baru",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }

                items(state.habits, key = { it.id }) { habit ->
                    HabitItem(
                        habit = habit,
                        isChecked = checkedStates[habit.id] == true,
                        currentCount = habitCounts[habit.id] ?: 0,
                        onCheck = { viewModel.toggleHabit(habit.id) },
                        onArchive = { viewModel.archiveHabit(habit.id) },
                        onEdit = { onEditHabit(habit.id) },
                        onDelete = { viewModel.deleteHabit(habit.id) }
                    )
                }
            }
        }
    }
}

// Simple animated container for quote transitions
@Composable
private fun KeyedContainer(
    key: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedContent(
        targetState = key,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "quote"
    ) { _ ->
        Box(modifier = modifier) {
            content()
        }
    }
}
