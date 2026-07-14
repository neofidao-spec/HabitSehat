package com.habitsehat.app.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitsehat.app.data.model.BadHabit
import com.habitsehat.app.ui.theme.StreakGreen
import com.habitsehat.app.ui.theme.StreakOrange
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitStopScreen(
    viewModel: BadHabitViewModel,
    isPremium: Boolean,
    onUpgrade: () -> Unit,
    onAddBadHabit: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HabitStop", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali") } },
                actions = {
                    if (!isPremium) {
                        TextButton(onClick = onUpgrade) {
                            Text("Upgrade", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (isPremium) {
                ExtendedFloatingActionButton(
                    onClick = onAddBadHabit,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text("Tambah", fontWeight = FontWeight.SemiBold) }
                )
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (!isPremium) {
            LockedPremiumView(onUpgrade = onUpgrade, modifier = Modifier.padding(padding))
        } else if (state.badHabits.isEmpty()) {
            EmptyBadHabitView(onAdd = onAddBadHabit, modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    TotalSavedCard(
                        totalSaved = state.badHabits.sumOf { it.totalMoneySaved },
                        totalDaysResisted = state.badHabits.sumOf { it.totalDaysResisted }
                    )
                }

                items(state.badHabits) { habitStat ->
                    BadHabitCard(
                        habitStat = habitStat,
                        onResist = { viewModel.resist(habitStat.badHabit.id) },
                        onGiveIn = { viewModel.giveIn(habitStat.badHabit.id) },
                        currencyFormat = currencyFormat
                    )
                }
            }
        }
    }
}

@Composable
private fun LockedPremiumView(onUpgrade: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(80.dp))
        Spacer(Modifier.height(16.dp))
        Text("Fitur Premium", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(8.dp))
        Text(
            "HabitStop hanya tersedia untuk pengguna Premium.\nBuka akses ke pelacak kebiasaan buruk dengan uang tersimpan & timeline kesehatan.",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onUpgrade) {
            Text("Upgrade ke Premium", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun EmptyBadHabitView(onAdd: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🚫", fontSize = 64.sp)
        Spacer(Modifier.height(16.dp))
        Text("Belum Ada Kebiasaan Buruk", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text("Tambah kebiasaan yang ingin dihentikan\nuntuk melihat uang tersimpan & progress kesehatan",
            fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp))
        Spacer(Modifier.height(24.dp))
        Button(onClick = onAdd) {
            Text("Tambah Kebiasaan Buruk", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TotalSavedCard(totalSaved: Int, totalDaysResisted: Int) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID")).apply { maximumFractionDigits = 0 }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Total Terhemat", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.height(4.dp))
                    Text(currencyFormat.format(totalSaved), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Hari Bertahan", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.height(4.dp))
                    Text("$totalDaysResisted hari", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
            Spacer(Modifier.height(12.dp))
            Text("Setiap kali kamu menolak kebiasaan buruk, uang tersimpan & tubuh lebih sehat!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun BadHabitCard(
    habitStat: BadHabitViewModel.BadHabitStat,
    onResist: () -> Unit,
    onGiveIn: () -> Unit,
    currencyFormat: java.text.NumberFormat
) {
    val habit = habitStat.badHabit
    val primaryColor = Color(android.graphics.Color.parseColor(habit.colorHex))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(habit.emoji, fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(habit.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = primaryColor)
                    Text("${habit.costPerOccurrence} × ${habit.frequencyPerDay}x/hari",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("🔥 Streak", "${habitStat.currentStreak} hari", StreakOrange)
                StatItem("💰 Tersimpan", currencyFormat.format(habitStat.totalMoneySaved), MaterialTheme.colorScheme.primary)
                StatItem("✅ Menolak", "${habitStat.totalOccurrencesResisted}x", MaterialTheme.colorScheme.secondary)
            }

            // Health benefit
            if (habit.healthImpact.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9))
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Filled.Favorite, contentDescription = null, tint = StreakGreen, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(habit.healthImpact, fontSize = 12.sp, color = StreakGreen, modifier = Modifier.weight(1f))
                }
            }

            // Action buttons
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onGiveIn,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                        outlinedBorder = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                    )
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Serah", fontWeight = FontWeight.Medium)
                }

                Button(
                    onClick = onResist,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Tolak", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(2.dp))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}