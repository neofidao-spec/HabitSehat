package com.habitsehat.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
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
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Custom top bar
        TopAppBar(
            title = { Text("Studio Tema", fontWeight = FontWeight.SemiBold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Kembali")
                }
            }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // FREE themes section header
            item(span = { GridItemSpan(2) }) {
                Text(
                    "Tema Gratis",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(AppTheme.FREE_THEMES) { theme ->
                ThemeCard(
                    theme = theme,
                    isSelected = theme.id == currentTheme.id,
                    isLocked = false,
                    onSelect = { onSelectTheme(theme) }
                )
            }

            // PREMIUM themes section header
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Tema Premium",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.Filled.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFFFD700)
                    )
                }
            }

            items(AppTheme.PREMIUM_THEMES) { theme ->
                ThemeCard(
                    theme = theme,
                    isSelected = theme.id == currentTheme.id && isPremium,
                    isLocked = !isPremium,
                    onSelect = {
                        if (isPremium) {
                            onSelectTheme(theme)
                        } else {
                            onUpgrade()
                        }
                    }
                )
            }
        }
    }

    // Mini preview at bottom when a theme is selected
    if (isPremium || AppTheme.FREE_THEMES.any { it.id == currentTheme.id }) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = currentTheme.lightPrimary.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color dots
                Column {
                    Text(currentTheme.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Dot(currentTheme.lightPrimary)
                        Dot(currentTheme.lightSecondary)
                        Dot(currentTheme.lightTertiary)
                        if (isSystemInDarkTheme()) {
                            Dot(currentTheme.darkPrimary)
                            Dot(currentTheme.darkSecondary)
                            Dot(currentTheme.darkTertiary)
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                Text(currentTheme.emoji, fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun Dot(color: Color) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun ThemeCard(
    theme: AppTheme,
    isSelected: Boolean,
    isLocked: Boolean,
    onSelect: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) theme.lightPrimary else Color.Transparent,
        animationSpec = tween(200),
        label = "border"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSelected) Modifier.border(2.dp, borderColor, RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable { onSelect() },
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
            // Preview row: 3 color dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Dot(theme.lightPrimary)
                Dot(theme.lightSecondary)
                Dot(theme.lightTertiary)
            }

            Spacer(Modifier.height(8.dp))

            Text(theme.emoji, fontSize = 24.sp)

            Spacer(Modifier.height(4.dp))

            Text(
                theme.name,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            if (isLocked) {
                Spacer(Modifier.height(2.dp))
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = "Premium",
                    modifier = Modifier.size(12.dp),
                    tint = Color(0xFFFFD700)
                )
            }

            if (isSelected && !isLocked) {
                Spacer(Modifier.height(2.dp))
                Text("Aktif", fontSize = 10.sp, color = theme.lightPrimary, fontWeight = FontWeight.Medium)
            }
        }
    }
}
