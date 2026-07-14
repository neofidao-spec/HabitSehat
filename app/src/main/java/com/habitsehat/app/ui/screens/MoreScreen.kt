package com.habitsehat.app.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitsehat.app.data.preferences.SettingsManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    settingsManager: SettingsManager,
    isPremium: Boolean,
    darkModeSetting: String,
    onThemeClick: () -> Unit,
    onPremiumClick: () -> Unit,
    onPomodoroClick: () -> Unit = {},
    onWeeklyReportClick: () -> Unit = {},
    onChallengesClick: () -> Unit = {},
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Lainnya", fontWeight = FontWeight.SemiBold) })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Premium card
            if (!isPremium) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    onClick = onPremiumClick
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🌟", fontSize = 28.sp)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Upgrade ke Premium", fontWeight = FontWeight.SemiBold)
                            Text("Buka semua fitur eksklusif", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null)
                    }
                }
            }

            // Main menu
            Card(shape = RoundedCornerShape(16.dp)) {
                Column {
                    MenuItem(Icons.Outlined.Assessment, "Laporan Mingguan", "Ringkasan progres minggu ini") { onWeeklyReportClick() }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    MenuItem(Icons.Outlined.EmojiEvents, "Tantangan", "Bangun kebiasaan 7/21/30 hari") { onChallengesClick() }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    MenuItem(Icons.Outlined.Timer, "Fokus (Pomodoro)", "Timer fokus 25/50/90 menit") { onPomodoroClick() }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    MenuItem(Icons.Outlined.Palette, "Studio Tema", "20+ tema premium") { onThemeClick() }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    MenuItem(Icons.Outlined.DarkMode, "Mode Gelap") {
                        val next = when (darkModeSetting) {
                            "system" -> "dark"
                            "dark" -> "light"
                            else -> "system"
                        }
                        scope.launch { settingsManager.setDarkMode(next) }
                    }
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    MenuItem(Icons.Outlined.Info, "Tentang Aplikasi", "v1.0.0") {}
                }
            }

            // Premium status
            if (isPremium) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👑", fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Premium Aktif", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                            Text("Nikmati semua fitur eksklusif", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Dark mode indicator
            Card(shape = RoundedCornerShape(16.dp)) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mode saat ini:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        when (darkModeSetting) {
                            "dark" -> "Gelap"
                            "light" -> "Terang"
                            else -> "Ikuti sistem"
                        },
                        fontWeight = FontWeight.Medium, fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MenuItem(icon: ImageVector, title: String, subtitle: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            if (subtitle != null) Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
    }
}
