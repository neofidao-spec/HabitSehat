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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.habitsehat.app.data.model.ExpenseCategory as ModelExpenseCategory
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCategoriesScreen(
    viewModel: ExpenseViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // Shared state for add/edit dialog
    val showDialog = remember { mutableStateOf(false) }
    val dialogTitle = remember { mutableStateOf("Tambah Kategori Baru") }
    val categoryName = remember { mutableStateOf("") }
    val categoryIcon = remember { mutableStateOf("💰") }
    val categoryColor = remember { mutableStateOf("#4CAF50") }
    val showIconPicker = remember { mutableStateOf(false) }
    val showColorPicker = remember { mutableStateOf(false) }
    val editingCategory = remember { mutableStateOf<ModelExpenseCategory?>(null) }

    val iconOptions = remember { listOf("🍔", "🚌", "🎮", "💊", "📚", "🛍️", "💡", "🏠", "🐶", "✈️", "🎁", "💰") }
    val colorOptions = remember { listOf("#4CAF50", "#2196F3", "#FF9800", "#E91E63", "#9C27B0", "#607D8B", "#795548", "#F44336", "#00BCD4", "#8BC34A", "#FFEB3B", "#FF5722") }

    fun openAddCategory() {
        dialogTitle.value = "Tambah Kategori Baru"
        categoryName.value = ""
        categoryIcon.value = "💰"
        categoryColor.value = "#4CAF50"
        editingCategory.value = null
        showDialog.value = true
    }

    fun openEditCategory(cat: ModelExpenseCategory) {
        dialogTitle.value = "Edit Kategori"
        categoryName.value = cat.name
        categoryIcon.value = cat.icon
        categoryColor.value = cat.colorHex
        editingCategory.value = cat
        showDialog.value = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kategori Pengeluaran", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { openAddCategory() }) {
                        Icon(Icons.Filled.Add, contentDescription = "Tambah Kategori")
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
            if (uiState.categories.isEmpty()) {
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
                        Text("📁", fontSize = 48.sp)
                        Text("Belum ada kategori", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(
                            "Tambah kategori untuk mulai mencatat pengeluaran",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Button(onClick = { openAddCategory() }) {
                            Text("Tambah Kategori Pertama")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.categories) { cat: ModelExpenseCategory ->
                        CategoryItem(
                            category = cat,
                            isDefault = cat.isDefault,
                            onEdit = { openEditCategory(cat) },
                            onDelete = {
                                if (!cat.isDefault) {
                                    scope.launch { viewModel.deleteCategory(cat) }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Add/Edit Category Dialog
    if (showDialog.value) {
        val confirm: @Composable () -> Unit = {
            Button(onClick = {
                if (categoryName.value.isNotBlank()) {
                    val cat = editingCategory.value ?: ModelExpenseCategory(
                        id = 0,
                        name = categoryName.value.trim(),
                        icon = categoryIcon.value,
                        colorHex = categoryColor.value,
                        isDefault = false,
                        sortOrder = uiState.categories.size,
                        createdAt = System.currentTimeMillis()
                    )
                    val updated = cat.copy(
                        name = categoryName.value.trim(),
                        icon = categoryIcon.value,
                        colorHex = categoryColor.value
                    )
                    scope.launch {
                        if (editingCategory.value == null) {
                            viewModel.addCategory(updated)
                        } else {
                            viewModel.updateCategory(updated)
                        }
                    }
                    showDialog.value = false
                }
            }) {
                Text(if (editingCategory.value == null) "Simpan" else "Update", fontWeight = FontWeight.SemiBold)
            }
        }
        val dismiss: @Composable () -> Unit = {
            TextButton(onClick = { showDialog.value = false }) { Text("Batal") }
        }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(dialogTitle.value) },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = categoryName.value,
                        onValueChange = { categoryName.value = it },
                        label = { Text("Nama Kategori") },
                        placeholder = { Text("Contoh: Makan, Transport, Hobi...") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Ikon", fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = categoryIcon.value,
                        onValueChange = { showIconPicker.value = true },
                        label = { Text("Pilih Ikon") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(android.graphics.Color.parseColor(categoryColor.value)).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(categoryIcon.value, fontSize = 20.sp)
                            }
                        }
                    )

                    Text("Warna", fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = categoryColor.value,
                        onValueChange = { showColorPicker.value = true },
                        label = { Text("Pilih Warna") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(android.graphics.Color.parseColor(categoryColor.value)))
                            )
                        }
                    )
                }
            },
            confirmButton = confirm,
            dismissButton = dismiss
        )
    }

    // Icon picker
    if (showIconPicker.value) {
        val dismiss: @Composable () -> Unit = {
            TextButton(onClick = { showIconPicker.value = false }) { Text("Batal") }
        }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showIconPicker.value = false },
            title = { Text("Pilih Ikon") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp)
                ) {
                    items(iconOptions) { icon ->
                        Text(
                            text = icon,
                            fontSize = 32.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable { categoryIcon.value = icon; showIconPicker.value = false }
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        )
                    }
                }
            },
            confirmButton = { Button(onClick = { showIconPicker.value = false }) { Text("OK") } },
            dismissButton = dismiss
        )
    }

    // Color picker
    if (showColorPicker.value) {
        val dismiss: @Composable () -> Unit = {
            TextButton(onClick = { showColorPicker.value = false }) { Text("Batal") }
        }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showColorPicker.value = false },
            title = { Text("Pilih Warna") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp)
                ) {
                    items(colorOptions) { color ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable { categoryColor.value = color; showColorPicker.value = false }
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(android.graphics.Color.parseColor(color)))
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(color, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = { Button(onClick = { showColorPicker.value = false }) { Text("OK") } },
            dismissButton = dismiss
        )
    }
}

@Composable
fun CategoryItem(
    category: ModelExpenseCategory,
    isDefault: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                        .background(Color(android.graphics.Color.parseColor(category.colorHex)).copy(alpha = 0.2f))
                ) {
                    Text(category.icon, fontSize = 20.sp, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(Modifier.width(12.dp))
                Column(horizontalAlignment = Alignment.Start) {
                    Text(category.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    if (isDefault) {
                        Text("Default", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isDefault) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}