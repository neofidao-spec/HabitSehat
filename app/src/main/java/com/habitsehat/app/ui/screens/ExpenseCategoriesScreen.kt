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
    val showAddCategory = remember { mutableStateOf(false) }
    val newCategoryName = remember { mutableStateOf("") }
    val newCategoryIcon = remember { mutableStateOf("💰") }
    val newCategoryColor = remember { mutableStateOf("#4CAF50") }

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
                    IconButton(onClick = { showAddCategory.value = true }) {
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
                        Text("Tambah kategori untuk mulai mencatat pengeluaran", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        Button(onClick = { showAddCategory.value = true }) {
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
                            onEdit = { /* TODO: edit */ },
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
    if (showAddCategory.value) {
        AlertDialog(
            onDismissRequest = { 
                showAddCategory.value = false
                newCategoryName.value = ""
                newCategoryIcon.value = "💰"
                newCategoryColor.value = "#4CAF50"
            },
            title = { Text("Tambah Kategori Baru") },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = newCategoryName.value,
                        onValueChange = { newCategoryName.value = it },
                        label = { Text("Nama Kategori") },
                        placeholder = { Text("Contoh: Makan, Transport, Hobi...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Text("Ikon", fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = newCategoryIcon.value,
                        onValueChange = { showIconPicker.value = true },
                        label = { Text("Pilih Ikon") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Box(
                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                                    .background(Color(android.graphics.Color.parseInt(newCategoryColor.value.replace("#", "0xFF"))).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(newCategoryIcon.value, fontSize = 20.sp)
                            }
                        }
                    )
                    
                    Text("Warna", fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = newCategoryColor.value,
                        onValueChange = { showColorPicker.value = true },
                        label = { Text("Pilih Warna") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Box(
                                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                                    .background(Color(android.graphics.Color.parseInt(newCategoryColor.value.replace("#", "0xFF"))))
                            )
                        }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newCategoryName.value.isNotBlank()) {
                        val category = ExpenseCategory(
                            name = newCategoryName.value.trim(),
                            icon = newCategoryIcon.value,
                            colorHex = newCategoryColor.value,
                            isDefault = false,
                            sortOrder = uiState.categories.size
                        )
                        scope.launch { viewModel.addCategory(category) }
                        showAddCategory.value = false
                        newCategoryName.value = ""
                    }
                }) {
                    Text("Simpan", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategory.value = false }) { Text("Batal") }
            }
        )
    }

    // Icon picker
    var showIconPicker by remember { mutableStateOf(false) }
    if (showIconPicker) {
        AlertDialog(
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
                                .clickable { newCategoryIcon.value = icon; showIconPicker.value = false }
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        )
                    }
                }
            },
            dismissButton = { TextButton(onClick = { showIconPicker.value = false }) { Text("Batal") } }
        )
    }

    // Color picker
    var showColorPicker by remember { mutableStateOf(false) }
    if (showColorPicker) {
        AlertDialog(
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
                                .clickable { newCategoryColor.value = color; showColorPicker.value = false }
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(android.graphics.Color.parseInt(color.replace("#", "0xFF"))))
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(color, fontSize = 16.sp)
                        }
                    }
                }
            },
            dismissButton = { TextButton(onClick = { showColorPicker.value = false }) { Text("Batal") } }
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
                        .background(Color(android.graphics.Color.parseInt(category.colorHex.replace("#", "0xFF"))).copy(alpha = 0.2f))
                ) {
                    Text(category.icon, fontSize = 20.sp, modifier = Modifier.align(Alignment.Center))
                }
                Spacer(Modifier.width(12.dp))
                Column(crossAxisAlignment = CrossAxisAlignment.Start) {
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

val iconOptions = listOf(
    "🍽️", "🚌", "🛍️", "🎮", "💊", "📚", "🎓", "💰", "📦",
    "☕", "🍔", "🚗", "👕", "🎬", "🏥", "📖", "💸", "🎁",
    "🏠", "✈️", "👟", "📱", "💇", "🐾", "🎨", "💳", "🏷️"
)

val colorOptions = listOf(
    "#FF9800", "#2196F3", "#9C27B0", "#E91E63", "#4CAF50",
    "#673AB7", "#3F51B5", "#00BCD4", "#009688", "#8BC34A",
    "#CDDC39", "#FFEB3B", "#FFC107", "#FF5722", "#795548",
    "#607D8B", "#FF6B6B", "#4ECDC4", "#45B7D1", "#96CEB4"
)