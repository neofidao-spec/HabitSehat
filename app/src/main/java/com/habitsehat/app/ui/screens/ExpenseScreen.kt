package com.habitsehat.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavController
import com.habitsehat.app.data.model.Expense
import com.habitsehat.app.data.model.ExpenseCategory
import com.habitsehat.app.data.db.ExpenseWithCategory
import com.habitsehat.app.data.model.WeeklyExpenseReport
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel,
    repository: HabitRepository,
    navController: NavController,
    onNavigateToAdd: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToReport: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val displayFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id", "ID"))
    val shortFmt = DateTimeFormatter.ofPattern("EEE, d MMM", Locale("id", "ID"))

    // Delete confirmation
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    // Show error via snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengeluaran", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = onNavigateToCategories) {
                        Icon(Icons.Filled.Category, contentDescription = "Kategori")
                    }
                    IconButton(onClick = onNavigateToReport) {
                        Icon(Icons.Filled.Assessment, contentDescription = "Laporan Mingguan")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Pengeluaran")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Today total card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Hari Ini", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(formatRupiah(uiState.todayTotal), fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Weekly summary card
            uiState.weeklyReport?.let { report ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Rekap Mingguan", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("${report.weekStart} - ${report.weekEnd}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(formatRupiah(report.weeklyTotal), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(Modifier.height(12.dp))

                        // Category breakdown (use Column + forEach, not nested LazyColumn)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            report.categoryTotals.forEach { cat ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                                                .background(parseColorSafe(cat.categoryColorHex).copy(alpha = 0.2f))
                                        ) {
                                            Text(cat.categoryIcon, fontSize = 16.sp, modifier = Modifier.align(Alignment.Center))
                                        }
                                        Spacer(Modifier.width(8.dp))
                                        Text(cat.categoryName, fontSize = 13.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                    Text(formatRupiah(cat.total), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        TextButton(
                            onClick = onNavigateToReport,
                            modifier = Modifier.align(Alignment.End)
                        ) { Text("Lihat Detail →", color = MaterialTheme.colorScheme.onPrimaryContainer) }
                    }
                }
            }

            // Today's expenses list (use forEach, not nested LazyColumn)
            if (uiState.todayExpenses.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Hari Ini (${shortFmt.format(LocalDate.now())})", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                    uiState.todayExpenses.forEach { expenseWithCat ->
                        ExpenseItemCard(
                            expense = expenseWithCat.expense,
                            category = expenseWithCat.expenseCategory!!,
                            onClick = { navController.navigate("add_expense/${expenseWithCat.expense.id}") },
                            onDelete = { expenseToDelete = expenseWithCat.expense }
                        )
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("💸", fontSize = 48.sp)
                        Text("Belum ada pengeluaran hari ini", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("Tekan tombol + untuk mencatat pengeluaran pertama", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
        }
    }

    // Delete expense confirmation dialog
    expenseToDelete?.let { expense ->
        val cat = uiState.categories.find { it.id == expense.categoryId }
        DeleteExpenseDialog(
            expense = expense,
            categoryName = cat?.name ?: "Kategori tidak ditemukan",
            onConfirm = {
                scope.launch {
                    viewModel.deleteExpense(expense)
                    snackbarHostState.showSnackbar(
                        message = "Pengeluaran dihapus",
                        duration = SnackbarDuration.Short
                    )
                }
                expenseToDelete = null
            },
            onDismiss = { expenseToDelete = null }
        )
    }
}

@Composable
fun ExpenseItemCard(
    expense: Expense,
    category: ExpenseCategory,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(parseColorSafe(category.colorHex).copy(alpha = 0.2f))
                ) {
                    Text(category.icon, fontSize = 20.sp, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(category.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    if (expense.note.isNotBlank()) {
                        Text(expense.note, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("-${formatRupiah(expense.amount)}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

fun formatRupiah(amount: Long): String {
    return "Rp ${java.text.NumberFormat.getNumberInstance(java.util.Locale("id", "ID")).format(amount)}"
}

fun parseColorSafe(hex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        Color.Gray
    }
}

// ──────────────────────────────────────
// DELETE CONFIRMATION DIALOG
// ──────────────────────────────────────
@Composable
fun DeleteExpenseDialog(
    expense: Expense,
    categoryName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hapus Pengeluaran?") },
        text = {
            Column {
                Text("Pengeluaran $categoryName sebesar")
                Text(formatRupiah(expense.amount), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                if (expense.note.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text("\"${expense.note}\"", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(8.dp))
                Text("Tindakan ini tidak bisa dibatalkan.", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
