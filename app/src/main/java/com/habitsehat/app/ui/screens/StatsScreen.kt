package com.habitsehat.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitsehat.app.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val primary = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistik", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.totalHabitsCreated == 0 && state.weekData.isEmpty()) {
            // ── EMPTY STATE ──
            val infiniteTransition = rememberInfiniteTransition(label = "statsFloat")
            val floatY by infiniteTransition.animateFloat(
                initialValue = -4f,
                targetValue = 4f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "floatY"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(primary.copy(alpha = 0.08f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.graphicsLayer { translationY = floatY }) {
                            Icon(
                                Icons.Outlined.BarChart,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = primary.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Belum ada data statistik",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Statistik akan muncul setelah kamu\nmemulai kebiasaan pertamamu",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Summary cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Best streak
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = StreakOrange.copy(alpha = 0.15f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = StreakOrange, modifier = Modifier.size(28.dp))
                                Spacer(Modifier.height(4.dp))
                                Text("${state.bestStreak}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = StreakOrange)
                                Text("hari streak", fontSize = 12.sp, color = StreakOrange)
                            }
                        }

                        // Total habits
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = primary.copy(alpha = 0.1f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Outlined.FavoriteBorder, contentDescription = null, tint = primary, modifier = Modifier.size(28.dp))
                                Spacer(Modifier.height(4.dp))
                                Text("${state.totalHabitsCreated}", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = primary)
                                Text("kebiasaan", fontSize = 12.sp, color = primary)
                            }
                        }

                        // Water
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = WaterBlue.copy(alpha = 0.1f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Outlined.WaterDrop, contentDescription = null, tint = WaterBlue, modifier = Modifier.size(28.dp))
                                Spacer(Modifier.height(4.dp))
                                val avgWater = if (state.weekData.isNotEmpty()) state.weekData.map { it.waterMl }.average().toInt() else 0
                                Text("${avgWater}ml", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = WaterBlue)
                                Text("rata-rata", fontSize = 12.sp, color = WaterBlue)
                            }
                        }
                    }
                }

                // Minggu ini chart
                if (state.weekData.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("Minggu Ini", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    state.weekData.forEach { day ->
                                        val date = day.date
                                        val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("id", "ID"))
                                        Text(dayName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                                    }
                                }

                                Spacer(Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    state.weekData.forEach { day ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            val pct = if (day.habitsTotal > 0) day.habitsDone.toFloat() / day.habitsTotal else 0f
                                            val barHeight = (pct * 60).dp.coerceAtLeast(4.dp)
                                            val color = when {
                                                pct >= 1f -> StreakGreen
                                                pct >= 0.5f -> StreakOrange
                                                pct > 0f -> StreakRed
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .width(24.dp)
                                                    .height(64.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                                contentAlignment = Alignment.BottomCenter
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .width(24.dp)
                                                        .height(barHeight)
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(color)
                                                )
                                            }
                                            Spacer(Modifier.height(4.dp))
                                            Text("${day.habitsDone}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }

                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider()
                                Spacer(Modifier.height(8.dp))

                                Text("Air (ml)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    state.weekData.forEach { day ->
                                        val pct = (day.waterMl.toFloat() / 2500f).coerceIn(0f, 1f)
                                        Box(
                                            modifier = Modifier
                                                .width(24.dp)
                                                .height(32.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant),
                                            contentAlignment = Alignment.BottomCenter
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .width(24.dp)
                                                    .height((pct * 32).dp.coerceAtLeast(2.dp))
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(WaterBlue)
                                            )
                                        }
                                    }
                                }

                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    LegendItem(color = StreakGreen, label = "Selesai")
                                    LegendItem(color = StreakOrange, label = "Setengah")
                                    LegendItem(color = StreakRed, label = "Sedikit")
                                    LegendItem(color = WaterBlue, label = "Air")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
