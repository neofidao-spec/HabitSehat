package com.habitsehat.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitsehat.app.data.db.ExpenseWithCategory
import com.habitsehat.app.data.model.Expense
import com.habitsehat.app.data.model.ExpenseCategory as ModelExpenseCategory
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit,
    expenseToEdit: ExpenseWithCategory? = null
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val isEditing = expenseToEdit != null
    var selectedCategory by remember { mutableStateOf<ModelExpenseCategory?>(expenseToEdit?.expenseCategory) }
    var amount by remember { mutableStateOf(expenseToEdit?.expense?.amount?.toString() ?: "") }
    var note by remember { mutableStateOf(expenseToEdit?.expense?.note ?: "") }
    var selectedDate by remember { mutableStateOf(expenseToEdit?.expense?.date ?: LocalDate.now()) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val categoryOptions = uiState.categories
    val displayFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id", "ID"))

    val quickAmounts = listOf(10000L, 25000L, 50000L, 100000L, 200000L, 500000L)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Pengeluaran" else "Tambah Pengeluaran", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category selector
            Text("Kategori", fontWeight = FontWeight.Medium)
            Surface(
                onClick = { showCategoryPicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (selectedCategory != null) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                                    .background(com.habitsehat.app.ui.screens.parseColorSafe(selectedCategory!!.colorHex).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(selectedCategory!!.icon, fontSize = 16.sp)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(selectedCategory!!.name, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        } else {
                            Text("Pilih kategori", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (selectedCategory == null) {
                Text("Wajib dipilih", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
            }

            // Date selector
            Text("Tanggal", fontWeight = FontWeight.Medium)
            Surface(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHighest
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedDate.format(displayFmt), fontSize = 14.sp)
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Amount input
            Text("Jumlah (Rp)", fontWeight = FontWeight.Medium, fontSize = 16.sp)
            OutlinedTextField(
                value = if (amount.isEmpty()) "" else "Rp${com.habitsehat.app.ui.screens.formatRupiah(amount.toLongOrNull() ?: 0L)}",
                onValueChange = { v ->
                    amount = v.filter { it.isDigit() || it == ' ' }.trim()
                    error = null
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = error != null,
                supportingText = { if (error != null) { Text(error!!) } },
                placeholder = { Text("Rp0", style = MaterialTheme.typography.headlineSmall) }
            )

            // Quick amount buttons — 2 rows of 3
            Text("Nominal Cepat", fontWeight = FontWeight.SemiBold, fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                quickAmounts.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { amt ->
                            val isActive = amount.toLongOrNull() == amt
                            if (isActive) {
                                FilledTonalButton(
                                    onClick = { amount = amt.toString() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(formatRupiahShort(amt), fontSize = 12.sp)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { amount = amt.toString() },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(formatRupiahShort(amt), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Save button — right after amount input
            Button(
                onClick = {
                    if (selectedCategory == null) {
                        error = "Kategori wajib dipilih"
                        return@Button
                    }
                    val amt = amount.replace(",", "").toLongOrNull() ?: 0
                    if (amt <= 0) {
                        error = "Jumlah harus > 0"
                        return@Button
                    }

                    val expense = Expense(
                        id = expenseToEdit?.expense?.id ?: 0,
                        categoryId = selectedCategory!!.id,
                        date = selectedDate,
                        amount = amt,
                        note = note.trim(),
                        createdAt = expenseToEdit?.expense?.createdAt ?: System.currentTimeMillis()
                    )
                    scope.launch {
                        if (isEditing) {
                            viewModel.updateExpense(expense)
                        } else {
                            viewModel.addExpense(expense)
                        }
                    }
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (isEditing) "Simpan Perubahan" else "Konfirmasi Pengeluaran", fontSize = 16.sp)
            }

            // Note — optional, below
            Text("Catatan (opsional)", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Tambahkan catatan") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
        }
    }

    // Category picker dialog
    if (showCategoryPicker) {
        val catTitle: @Composable () -> Unit = { Text("Pilih Kategori") }
        val catDismiss: @Composable () -> Unit = {
            TextButton(onClick = { showCategoryPicker = false }) { Text("Batal") }
        }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showCategoryPicker = false },
            title = catTitle,
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp)
                ) {
                    items(categoryOptions) { cat: ModelExpenseCategory ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable {
                                    selectedCategory = cat
                                    showCategoryPicker = false
                                }
                                .background(
                                    if (selectedCategory?.id == cat.id) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(12.dp)
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(com.habitsehat.app.ui.screens.parseColorSafe(cat.colorHex).copy(alpha = 0.2f))
                            ) {
                                Text(cat.icon, fontSize = 20.sp, modifier = Modifier.align(Alignment.Center))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(cat.name, fontSize = 16.sp)
                            if (cat.isDefault) {
                                Spacer(Modifier.width(8.dp))
                                Text("Default", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showCategoryPicker = false }) {
                    Text("OK", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = catDismiss
        )
    }

    // Date picker dialog
    if (showDatePicker) {
        val dateTitle: @Composable () -> Unit = { Text("Pilih Tanggal") }
        val dateConfirm: @Composable () -> Unit = {
            Button(onClick = { showDatePicker = false }) {
                Text("OK", fontWeight = FontWeight.SemiBold)
            }
        }
        val dateDismiss: @Composable () -> Unit = {
            TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
        }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = dateTitle,
            text = {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tanggal: ${selectedDate.format(displayFmt)}")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                            Icon(Icons.Filled.ChevronLeft, contentDescription = "Kemarin")
                        }
                        Text(selectedDate.format(displayFmt), fontSize = 18.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 16.dp))
                        IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                            Icon(Icons.Filled.ChevronRight, contentDescription = "Besok")
                        }
                    }
                }
            },
            confirmButton = dateConfirm,
            dismissButton = dateDismiss
        )
    }
}

fun formatRupiahShort(amount: Long): String {
    return when {
        amount >= 1_000_000 -> "${amount / 1_000_000}Jt"
        amount >= 1_000 -> "${amount / 1_000}Rb"
        else -> amount.toString()
    }
}
