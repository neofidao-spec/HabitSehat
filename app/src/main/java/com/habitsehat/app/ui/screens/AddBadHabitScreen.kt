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
import com.habitsehat.app.data.model.BadHabit
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBadHabitScreen(
    repository: HabitRepository,
    onBack: () -> Unit,
    badHabitToEdit: BadHabit? = null
) {
    var name by remember { mutableStateOf(badHabitToEdit?.name ?: "") }
    var emoji by remember { mutableStateOf(badHabitToEdit?.emoji ?: "🚬") }
    var colorHex by remember { mutableStateOf(badHabitToEdit?.colorHex ?: "#F44336") }
    var costPerOccurrence by remember { mutableStateOf(badHabitToEdit?.costPerOccurrence ?: 20000) }
    var frequencyPerDay by remember { mutableStateOf(badHabitToEdit?.frequencyPerDay ?: 1) }
    var healthImpact by remember { mutableStateOf(badHabitToEdit?.healthImpact ?: "") }
    var trigger by remember { mutableStateOf(badHabitToEdit?.trigger ?: "") }
    var replacementHabit by remember { mutableStateOf(badHabitToEdit?.replacementHabit ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val emojiOptions = listOf("🚬", "☕", "🍺", "🍔", "🍟", "🍕", "🍩", "🍪", "📱", "🎮", "🛏️", "💊", "💸", "🎰", "🔞")
    val colorOptions = listOf("#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#00BCD4", "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722", "#795548", "#607D8B")

    val isEditing = badHabitToEdit != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Kebiasaan Buruk" else "Tambah Kebiasaan Buruk", fontWeight = FontWeight.SemiBold) },
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
            // Nama kebiasaan
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; error = null },
                label = { Text("Nama kebiasaan buruk") },
                placeholder = { Text("Mis: Merokok, Kopi berlebih, Sosmed malam...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                supportingText = if (error != null) {{ Text(error!!) }} else null
            )

            // Emoji picker
            Text("Ikon", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = emoji,
                onValueChange = { showEmojiPicker = true },
                label = { Text("Pilih ikon") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = { Icon(Icons.Filled.EmojiEmotions, contentDescription = null) }
            )

            // Color picker
            Text("Warna", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = colorHex,
                onValueChange = { showColorPicker = true },
                label = { Text("Pilih warna") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(android.graphics.Color.parseColor(colorHex)))
                    )
                }
            )

            // Biaya per kejadian
            Text("Biaya per kejadian (Rp)", fontWeight = FontWeight.Medium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = { if (costPerOccurrence >= 1000) costPerOccurrence -= 1000 },
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Icon(Icons.Filled.Remove, contentDescription = "Kurangi biaya", tint = MaterialTheme.colorScheme.onSurface) }
                Text("Rp $costPerOccurrence", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                FilledTonalButton(
                    onClick = { costPerOccurrence += 1000 },
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Icon(Icons.Filled.Add, contentDescription = "Tambah biaya", tint = MaterialTheme.colorScheme.onSurface) }
            }

            // Frekuensi per hari
            Text("Frekuensi per hari", fontWeight = FontWeight.Medium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = { if (frequencyPerDay > 1) frequencyPerDay-- },
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Icon(Icons.Filled.Remove, contentDescription = "Kurangi frekuensi", tint = MaterialTheme.colorScheme.onSurface) }
                Text(
                    "$frequencyPerDay",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(56.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                FilledTonalButton(
                    onClick = { if (frequencyPerDay < 50) frequencyPerDay++ },
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Icon(Icons.Filled.Add, contentDescription = "Tambah frekuensi", tint = MaterialTheme.colorScheme.onSurface) }
                Text("kali/hari", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Dampak kesehatan
            Text("Dampak kesehatan (opsional)", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = healthImpact,
                onValueChange = { healthImpact = it },
                label = { Text("Contoh: Paru bersih dalam 3 bulan, risiko jantung turun 50%") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            // Trigger (pemicu)
            Text("Pemicu (opsional)", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = trigger,
                onValueChange = { trigger = it },
                label = { Text("Contoh: Stres, teman ngajak, lihat iklan") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            // Kebiasaan pengganti
            Text("Kebiasaan pengganti (opsional)", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = replacementHabit,
                onValueChange = { replacementHabit = it },
                label = { Text("Contoh: Minum air putih, tarik napas dalam, push up 10x") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )

            Spacer(Modifier.weight(1f))

            // Save button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        error = "Nama kebiasaan harus diisi"
                        return@Button
                    }
                    val habit = BadHabit(
                        id = badHabitToEdit?.id ?: 0,
                        name = name.trim(),
                        emoji = emoji,
                        colorHex = colorHex,
                        costPerOccurrence = costPerOccurrence,
                        frequencyPerDay = frequencyPerDay,
                        healthImpact = healthImpact.trim(),
                        trigger = trigger.trim(),
                        replacementHabit = replacementHabit.trim(),
                        isActive = true,
                        startDate = java.time.LocalDate.now(),
                        createdAt = System.currentTimeMillis()
                    )
                    scope.launch {
                        if (isEditing) {
                            repository.updateBadHabit(habit)
                        } else {
                            repository.addBadHabit(habit)
                        }
                    }
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(if (isEditing) "Simpan Perubahan" else "Simpan Kebiasaan Buruk", fontSize = 16.sp)
            }
        }
    }

    // Emoji picker dialog
    if (showEmojiPicker) {
        AlertDialog(
            onDismissRequest = { showEmojiPicker = false },
            title = { Text("Pilih Ikon") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp)
                ) {
                    items(emojiOptions) { selectedEmoji ->
                        Text(
                            text = selectedEmoji,
                            fontSize = 32.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable { emoji = selectedEmoji; showEmojiPicker = false }
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showEmojiPicker = false }) {
                    Text("OK", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmojiPicker = false }) { Text("Batal") }
            }
        )
    }

    // Color picker dialog
    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text("Pilih Warna") },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp)
                ) {
                    items(colorOptions) { selectedColor ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable { colorHex = selectedColor; showColorPicker = false }
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(android.graphics.Color.parseColor(selectedColor)))
                            )
                            Spacer(Modifier.width(16.dp))
                            Text(selectedColor, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showColorPicker = false }) {
                    Text("OK", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showColorPicker = false }) { Text("Batal") }
            }
        )
    }
}