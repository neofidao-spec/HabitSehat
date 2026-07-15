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
import com.habitsehat.app.data.db.ExpenseWithCategory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import com.habitsehat.app.data.repository.HabitRepository
import com.habitsehat.app.data.repository.WeeklyExpenseReport
import kotlinx.coroutines.launch
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
        ) {
            val report = uiState.weeklyReport

            if (report != null) {
                ReportContent(report = report, shortFmt = shortFmt)
            } else {
                LoadingContent()
            }
        }
    }
}

@Composable
private fun ReportContent(report: WeeklyExpenseReport, shortFmt: DateTimeFormatter) {
    // Weekly summary card
    item {
        val start = try { LocalDate.parse(report.weekStart).format(shortFmt) } catch (e: Exception) { report.weekStart }
        val end = try { LocalDate.parse(report.weekEnd).format(shortFmt) } catch (e: Exception) { report.weekEnd }
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
                Text(formatRupiahReport(report.totalExpenses), fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text("Total Pengeluaran", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
            }
        }
    }

    // Daily totals
    if (report.dailyTotals.isNotEmpty()) {
        item { Text("Per Hari", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
        report.dailyTotals.entries.sortedBy { it.key }.forEach { (date, total) ->
            item {
                val d = try { LocalDate.parse(date).format(shortFmt) } catch (e: Exception) { date }
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(d, fontSize = 14.sp)
                        Text(formatRupiahReport(total), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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
                                .background(parseColorSafeReport(cat.categoryColor).copy(alpha = 0.2f)),
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

    // Expense items detail
    if (report.expenseItems.isNotEmpty()) {
        item { Spacer(Modifier.height(8.dp)); Text("Detail Pengeluaran", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
        items(report.expenseItems) { item ->
            ExpenseItemCardReport(
                expense = item.expense,
                category = item.expenseCategory!!,
                onClick = {},
                onDelete = {}
            )
        }
    }
}

@Composable
private fun LoadingContent() {
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

@Composable
private fun ExpenseItemCardReport(
    expense: com.habitsehat.app.data.model.Expense,
    category: com.habitsehat.app.data.model.ExpenseCategory,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val displayFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id", "ID"))
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(parseColorSafeReport(category.colorHex).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) { Text(category.icon, fontSize = 20.sp) }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(category.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        try { LocalDate.parse(expense.date.toString()).format(displayFmt) } catch (e: Exception) { expense.date.toString() },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(formatRupiahReport(expense.amount), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                if (expense.note.isNotBlank()) {
                    Text(expense.note, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

// Helper functions inside composable scope so they can access MaterialTheme
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
            }
        }
    }
}

private fun parseColorSafeReport(colorHex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }
}

private fun formatRupiahReport(amount: Long): String {
    return "Rp ${java.text.NumberFormat.getNumberInstance(Locale("id", "ID")).format(amount)}"
}