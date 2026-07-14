package com.habitsehat.app.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.habitsehat.app.data.model.AppTheme

// Default fallback colors — also used by other components directly
val StreakGreen = Color(0xFF4CAF50)
val StreakOrange = Color(0xFFFF9800)
val StreakRed = Color(0xFFF44336)
val WaterBlue = Color(0xFF2196F3)
val ProgressBg = Color(0xFFE0E0E0)

@Composable
fun HabitSehatTheme(
    appTheme: AppTheme = AppTheme.getThemeById("mint"),
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) appTheme.toDarkColorScheme() else appTheme.toLightColorScheme()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
