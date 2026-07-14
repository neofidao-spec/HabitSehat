package com.habitsehat.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitsehat.app.data.model.Habit
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroScreen(
    viewModel: PomodoroViewModel,
    isPremium: Boolean,
    onUpgrade: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(isPremium) {
        viewModel.setPremium(isPremium)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fokus", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // Today's stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TodayStat("Sesi Hari Ini", "${state.sessionCountToday} sesi")
                TodayStat("Total Fokus", formatDuration(state.totalTodaySeconds))
            }

            Spacer(Modifier.height(32.dp))

            // Circular timer
            Box(
                modifier = Modifier.size(260.dp),
                contentAlignment = Alignment.Center
            ) {
                val progress = if (state.selectedMinutes > 0) {
                    1f - (state.remainingSeconds.toFloat() / (state.selectedMinutes * 60))
                } else 0f

                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 8.dp,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color = if (state.isFinished) MaterialTheme.colorScheme.primary
                    else if (state.isRunning) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatTimer(state.remainingSeconds),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.isFinished) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface
                    )

                    if (state.isFinished) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Sesi Selesai! 🎉",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Duration selector
            if (!state.isRunning && !state.isPaused) {
                Text("Durasi Fokus", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DurationChip(
                        label = "25 menit",
                        selected = state.selectedMinutes == 25,
                        onClick = { viewModel.selectDuration(25) },
                        enabled = true
                    )
                    DurationChip(
                        label = "50 menit",
                        selected = state.selectedMinutes == 50,
                        onClick = { viewModel.selectDuration(50) },
                        enabled = true
                    )
                    if (isPremium) {
                        DurationChip(
                            label = "90 menit",
                            selected = state.selectedMinutes == 90,
                            onClick = { viewModel.selectDuration(90) },
                            enabled = true
                        )
                    } else {
                        LockedChip(onClick = onUpgrade)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            // Controls
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.isFinished) {
                    Button(
                        onClick = { viewModel.reset() },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Ulangi", fontWeight = FontWeight.SemiBold)
                    }
                } else if (state.isRunning) {
                    FilledTonalButton(
                        onClick = { viewModel.pause() },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(Icons.Filled.Pause, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Jeda", fontWeight = FontWeight.SemiBold)
                    }
                } else if (state.isPaused) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { viewModel.resume() },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Lanjut", fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { viewModel.reset() },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Icon(Icons.Filled.Stop, contentDescription = null, modifier = Modifier.size(24.dp))
                        }
                    }
                } else {
                    Button(
                        onClick = { viewModel.start() },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(56.dp)
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Mulai Fokus", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Habit selector (only when not running)
            if (!state.isRunning && !state.isPaused && state.availableHabits.isNotEmpty()) {
                Text("Sedang Mengerjakan", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.availableHabits) { habit ->
                        HabitChip(
                            habit = habit,
                            selected = state.selectedHabitId == habit.id,
                            onClick = {
                                viewModel.selectHabit(if (state.selectedHabitId == habit.id) null else habit.id)
                            }
                        )
                    }
                }
            }

            // White noise toggle (premium)
            if (state.isRunning && isPremium) {
                Spacer(Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.MusicNote, contentDescription = null, tint = if (state.whiteNoiseEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    Text("White Noise", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = state.whiteNoiseEnabled,
                        onCheckedChange = { viewModel.toggleWhiteNoise() }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Weekly focus stats
            WeeklyFocusCard(viewModel)
        }
    }
}

@Composable
private fun TodayStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DurationChip(label: String, selected: Boolean, onClick: () -> Unit, enabled: Boolean) {
    if (!enabled) return
    AssistChip(
        onClick = onClick,
        label = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            labelColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun LockedChip(onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text("90 menit 🔒", fontWeight = FontWeight.Normal) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun HabitChip(habit: Habit, selected: Boolean, onClick: () -> Unit) {
    val bgColor = if (selected) {
        try { Color(android.graphics.Color.parseColor(habit.colorHex)) } catch (e: Exception) { MaterialTheme.colorScheme.primary }
    } else MaterialTheme.colorScheme.surfaceVariant

    AssistChip(
        onClick = onClick,
        label = { Text(habit.name, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = { Text(habit.icon, fontSize = 14.sp) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = bgColor
        ),
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun WeeklyFocusCard(viewModel: PomodoroViewModel) {
    val state by viewModel.uiState.collectAsState()
    val minutes = state.totalTodaySeconds / 60

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Timeline, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Fokus Hari Ini", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text("$minutes menit fokus • ${state.sessionCountToday} sesi", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

private fun formatTimer(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}

private fun formatDuration(seconds: Int): String {
    val mins = seconds / 60
    val hrs = mins / 60
    return if (hrs > 0) "${hrs}j ${mins % 60}m" else "${mins}m"
}
