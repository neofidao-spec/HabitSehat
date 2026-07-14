package com.habitsehat.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitsehat.app.data.model.Habit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddHabitScreen(
    onSave: (Habit) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var targetCount by remember { mutableIntStateOf(1) }
    var reminderEnabled by remember { mutableStateOf(true) }
    var reminderHour by remember { mutableIntStateOf(8) }
    var reminderMinute by remember { mutableIntStateOf(0) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Kebiasaan", fontWeight = FontWeight.SemiBold) },
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
            // Nama habit
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; error = null },
                label = { Text("Nama kebiasaan") },
                placeholder = { Text("Mis: Minum Air, Olahraga, Baca...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = error != null,
                supportingText = if (error != null) {{ Text(error!!) }} else null
            )

            // Target count
            Text("Target harian", fontWeight = FontWeight.Medium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = { if (targetCount > 1) targetCount-- },
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("-", fontSize = 18.sp)
                }
                Text(
                    "$targetCount",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(40.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                FilledTonalButton(
                    onClick = { if (targetCount < 99) targetCount++ },
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("+", fontSize = 18.sp)
                }
                Text("kali/hari", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Reminder
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Pengingat", fontWeight = FontWeight.Medium)
                Switch(checked = reminderEnabled, onCheckedChange = { reminderEnabled = it })
            }

            if (reminderEnabled) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Jam:")
                    ExposedDropdownMenuBox(
                        expanded = false,
                        onExpandedChange = {}
                    ) {
                        OutlinedTextField(
                            value = "%02d:%02d".format(reminderHour, reminderMinute),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.width(100.dp),
                            singleLine = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(false) }
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Save button
            Button(
                onClick = {
                    if (name.isBlank()) {
                        error = "Nama kebiasaan harus diisi"
                        return@Button
                    }
                    val habit = Habit(
                        name = name.trim(),
                        targetCount = targetCount,
                        reminderEnabled = reminderEnabled,
                        reminderHour = reminderHour,
                        reminderMinute = reminderMinute
                    )
                    onSave(habit)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Simpan Kebiasaan", fontSize = 16.sp)
            }
        }
    }
}
