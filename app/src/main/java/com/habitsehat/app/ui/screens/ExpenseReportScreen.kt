package com.habitsehat.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitsehat.app.data.model.WeeklyExpenseReport
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseReportScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val shortFmt = DateTimeFormatter.ofPattern("d MMM", Locale("id", "ID"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan Pengeluaran", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) { // LazyListScope
            val report = uiState.weeklyReport

            if (report != null) {
                // Weekly summary card
                item {
                    val start = try { LocalDate.parse(report.weekStart.toString()).format(shortFmt) } catch (e: Exception) { report.weekStart.toString() }
                    val end = try { LocalDate.parse(report.weekEnd.toString()).format(shortFmt) } catch (e: Exception) { report.weekEnd.toString() }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Rekap Mingguan", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.height(4.dp))
                            Text("$start — $end", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.height(16.dp))
                            Text(formatRupiahReport(report.weeklyTotal), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("Total Pengeluaran", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        }
                    }
                }

                // Daily totals
                if (report.dailyTotals.isNotEmpty()) {
                    item { Text("Per Hari", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
                    report.dailyTotals.forEach { daily ->
                        item {
                            val d = try { daily.date.format(shortFmt) } catch (e: Exception) { daily.date.toString() }
                            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(d, fontSize = 14.sp)
                                    Text(formatRupiahReport(daily.total), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }

                // Category breakdown
                if (report.categoryTotals.isNotEmpty()) {
                    item { Spacer(Modifier.height(8.dp)); Text("Per Kategori", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
                    items(report.categoryTotals) { cat ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(parseColorSafeReport(cat.categoryColorHex).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) { Text(cat.categoryIcon, fontSize = 16.sp) }
                                    Spacer(Modifier.width(8.dp))
                                    Text(cat.categoryName, fontSize = 14.sp)
                                }
                                Text(formatRupiahReport(cat.total), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            } else {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(16.dp))
                            Text("Memuat laporan...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun parseColorSafeReport(colorHex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }
}

@Composable
private fun formatRupiahReport(amount: Long): String {
    return "Rp ${java.text.NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount)}"
}