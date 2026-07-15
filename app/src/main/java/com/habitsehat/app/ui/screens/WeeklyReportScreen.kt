package com.habitsehat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.repository.WeeklyReport
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReportScreen(
    viewModel: WeeklyReportViewModel,
    onBack: () -> Unit,
    onShare: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Mingguan", fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali") } },
                actions = {
                    if (state.report != null) {
                        IconButton(onClick = onShare) {
                            Icon(Icons.Filled.Share, contentDescription = "Bagikan")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.report == null) {
            EmptyState(padding)
        } else {
            ReportContent(state.report!!, padding)
        }
    }
}

@Composable
private fun EmptyState(padding: androidx.compose.foundation.layout.PaddingValues) {
    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📊", fontSize = 64.sp)
            Spacer(Modifier.height(16.dp))
            Text("Belum ada data minggu ini", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text("Catat kebiasaan setiap hari untuk melihat laporan", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ReportContent(report: WeeklyReport, padding: androidx.compose.foundation.layout.PaddingValues) {
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("id", "ID"))
    val startDate = try { LocalDate.parse(report.weekStart).format(dateFormatter) } catch (e: Exception) { report.weekStart }
    val endDate = try { LocalDate.parse(report.weekEnd).format(dateFormatter) } catch (e: Exception) { report.weekEnd }
    val consistencyPct = if (report.totalPossibleDays > 0) {
        (report.totalDoneDays * 100 / report.totalPossibleDays)
    } else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Ringkasan Mingguan", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.height(4.dp))
                Text("$startDate — $endDate", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Spacer(Modifier.height(16.dp))
                Text("$consistencyPct%", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("Konsistensi", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
            }
        }

        // Consistency breakdown
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Konsistensi Harian", fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    val days = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")
                    val today = LocalDate.now()
                    val monday = today.with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    for (i in 0..6) {
                        val date = monday.plusDays(i.toLong())
                        val dateStr = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(days[i], fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            val filled = if (date.isBefore(today.plusDays(1))) true else false
                            Box(
                                modifier = Modifier
                                    .size(if (filled) 28.dp else 24.dp)
                                    .padding(2.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(if (filled) 24.dp else 20.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                ) {}
                            }
                        }
                    }
                }
            }
        }

        // Stats grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = "🔥",
                title = "Streak",
                value = "${report.bestStreak} hari"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = "💧",
                title = "Rata Air",
                value = "${report.averageWaterMl.toInt()} ml"
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = "🍅",
                title = "Fokus",
                value = "${report.totalWeeklyFocusSeconds / 60} menit"
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = "💰",
                title = "Terhemat",
                value = "Rp${report.totalMoneySavedThisWeek}"
            )
        }

        // Habit breakdown
        if (report.habitStats.isNotEmpty()) {
            Card(shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Per Kebiasaan", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    report.habitStats.forEach { stat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stat.name, modifier = Modifier.weight(1f), fontSize = 14.sp)
                            Text("${stat.doneDays}/${stat.totalDays} hari", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                        }
                        if (stat != report.habitStats.last()) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        }
                    }
                }
            }
        }

        // Best & worst day
        val dayFormatter = DateTimeFormatter.ofPattern("EEEE, d MMM", Locale("id", "ID"))
        val repBestDay = try { LocalDate.parse(report.bestDay).format(dayFormatter) } catch (e: Exception) { report.bestDay }
        val repWorstDay = try { LocalDate.parse(report.worstDay).format(dayFormatter) } catch (e: Exception) { report.worstDay }
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Terkuat: ${repBestDay}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.TrendingDown, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Terlemah: ${repWorstDay}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, icon: String, title: String, value: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
