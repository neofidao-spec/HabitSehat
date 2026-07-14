package com.habitsehat.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habitsehat.app.data.model.AppTheme
import com.habitsehat.app.data.preferences.SettingsManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(
    settingsManager: SettingsManager,
    currentTheme: AppTheme,
    isPremium: Boolean,
    onSelectTheme: (AppTheme) -> Unit,
    onUpgrade: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Studio Tema", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            // Current theme preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(currentTheme.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ColorDot(currentTheme.lightPrimary)
                            ColorDot(currentTheme.lightSecondary)
                            ColorDot(currentTheme.lightTertiary)
                            if (isSystemInDarkTheme()) {
                                ColorDot(currentTheme.darkPrimary)
                                ColorDot(currentTheme.darkSecondary)
                                ColorDot(currentTheme.darkTertiary)
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Text(currentTheme.emoji, fontSize = 28.sp)
                }
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Text("Tema Gratis", fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp))
            }

            items(AppTheme.FREE_THEMES) { theme ->
                ThemeGridCard(theme, theme.id == currentTheme.id, false) {
                    onSelectTheme(theme)
                }
            }

            item(span = { GridItemSpan(2) }) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)) {
                    Text("Tema Premium", fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Filled.Lock, contentDescription = null,
                        modifier = Modifier.size(14.dp), tint = Color(0xFFFFD700))
                }
            }

            items(AppTheme.PREMIUM_THEMES) { theme ->
                ThemeGridCard(theme, theme.id == currentTheme.id && isPremium, !isPremium) {
                    if (isPremium) onSelectTheme(theme) else onUpgrade()
                }
            }
        }
    }
}

@Composable
private fun ColorDot(color: Color) {
    Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(color))
}

@Composable
private fun ThemeGridCard(
    theme: AppTheme,
    isSelected: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) theme.lightPrimary else Color.Transparent,
        animationSpec = tween(200), label = "border"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSelected) Modifier.border(2.dp, borderColor, RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ColorDot(theme.lightPrimary)
                ColorDot(theme.lightSecondary)
                ColorDot(theme.lightTertiary)
            }
            Spacer(Modifier.height(8.dp))
            Text(theme.emoji, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(theme.name, fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center, maxLines = 1)

            if (isLocked) {
                Icon(Icons.Filled.Lock, contentDescription = "Premium",
                    modifier = Modifier.size(12.dp), tint = Color(0xFFFFD700))
            }
            if (isSelected && !isLocked) {
                Text("Aktif", fontSize = 10.sp, color = theme.lightPrimary,
                    fontWeight = FontWeight.Medium)
            }
        }
    }
}
