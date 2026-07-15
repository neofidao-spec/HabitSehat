package com.habitsehat.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.habitsehat.app.data.model.Expense
import com.habitsehat.app.data.model.ExpenseCategory as ModelExpenseCategory
import com.habitsehat.app.data.db.ExpenseWithCategory
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
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(expenseToEdit?.expenseCategory) }
    var amount by remember { mutableStateOf(expenseToEdit?.expense.amount.toString() ?: "") }
    var note by remember { mutableStateOf(expenseToEdit?.expense.note ?: "") }
    var selectedDate by remember { mutableStateOf(expenseToEdit?.expense.date ?: LocalDate.now()) }
    var showCategoryPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val categoryOptions = uiState.categories
    val displayFmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id", "ID"))
    val isoFmt = DateTimeFormatter.ISO_LOCAL_DATE

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category selector
            Text("Kategori", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = selectedCategory?.name ?: "Pilih kategori",
                onValueChange = { showCategoryPicker = true },
                label = { Text("Kategori pengeluaran") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Filled.Category, contentDescription = null)
                },
                isError = selectedCategory == null,
                supportingText = { if (selectedCategory == null) { Text("Wajib dipilih") } }
            )

            // Date selector
            Text("Tanggal", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = selectedDate.format(displayFmt),
                onValueChange = { showDatePicker = true },
                label = { Text("Tanggal") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) }
            )

            // Amount input
            Text("Jumlah (Rp)", fontWeight = FontWeight.Medium)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it.filter { it.isDigit() }; error = null },
                    label = { Text("Jumlah") },
                    placeholder = { Text("0") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    singleLine = true,
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    isError = error != null,
                    supportingText = { if (error != null) { Text(error!!) } }
                )
            }

            // Quick amount buttons
            Text("Cepat:", fontWeight = FontWeight.Medium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickAmounts.forEach { amt ->
                    OutlinedButton(
                        onClick = { amount = amt.toString() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(formatRupiahShort(amt), fontSize = 12.sp)
                    }
                }
            }

            // Note
            Text("Catatan (opsional)", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Catatan tambahan") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(Modifier.weight(1f))

            // Save button
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
                        id = expenseToEdit?.expense.id ?: 0,
                        categoryId = selectedCategory!!.id,
                        date = selectedDate,
                        amount = amt,
                        note = note.trim(),
                        createdAt = expenseToEdit?.expense.createdAt ?: System.currentTimeMillis()
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
                Text(if (isEditing) "Simpan Perubahan" else "Simpan Pengeluaran", fontSize = 16.sp)
            }
        }
    }

    // Category picker dialog
    if (showCategoryPicker) {
        AlertDialog(
            onDismissRequest = { showCategoryPicker = false },
            title = { Text("Pilih Kategori") },
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
                                .clickable { selectedCategory = c; showCategoryPicker = false }
                                .background(
                                    if (selectedCategory?.id == c.id) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(12.dp)
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(android.graphics.Color.parseColor(c.colorHex)).copy(alpha = 0.2f))
                            ) {
                                Text(c.icon, fontSize = 20.sp, modifier = Modifier.align(Alignment.Center))
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(c.name, fontSize = 16.sp)
                            if (c.isDefault) {
                                Spacer(Modifier.width(8.dp))
                                Text("Default", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showCategoryPicker = false }) { Text("Batal") }
            }
        )
    }

    // Date picker dialog
    if (showDatePicker) {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = java.util.Date.from(selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())
        
        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            title = { Text("Pilih Tanggal") },
            text = {
                androidx.compose.material3.DatePicker(
                    selectedDate = selectedDate,
                    onDateChange = { selectedDate = it },
                    modifier = Modifier.padding(16.dp)
                )
            },
            confirmButton = {
                Button(onClick = { showDatePicker = false }) {
                    Text("OK", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Batal") }
            }
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