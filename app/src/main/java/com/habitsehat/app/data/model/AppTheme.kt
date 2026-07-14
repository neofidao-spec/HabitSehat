package com.habitsehat.app.data.model

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

data class AppTheme(
    val id: String,
    val name: String,
    val isPremium: Boolean,
    val lightPrimary: Color,
    val lightSecondary: Color,
    val lightTertiary: Color,
    val darkPrimary: Color,
    val darkSecondary: Color,
    val darkTertiary: Color,
    val emoji: String
) {
    fun toLightColorScheme() = lightColorScheme(
        primary = lightPrimary,
        onPrimary = Color.White,
        primaryContainer = lightPrimary.copy(alpha = 0.15f),
        onPrimaryContainer = lightPrimary,
        secondary = lightSecondary,
        onSecondary = Color.White,
        secondaryContainer = lightSecondary.copy(alpha = 0.15f),
        onSecondaryContainer = lightSecondary,
        tertiary = lightTertiary,
        onTertiary = Color.White,
        tertiaryContainer = lightTertiary.copy(alpha = 0.15f),
        onTertiaryContainer = lightTertiary,
        background = Color(0xFFFFFBFE),
        surface = Color(0xFFFFFBFE),
        surfaceVariant = Color(0xFFF0F0F0),
        onBackground = Color(0xFF1C1B1F),
        onSurface = Color(0xFF1C1B1F),
        onSurfaceVariant = Color(0xFF49454F),
        error = Color(0xFFBA1A1A),
        outline = Color(0xFF79747E)
    )

    fun toDarkColorScheme() = darkColorScheme(
        primary = darkPrimary,
        onPrimary = Color(0xFF00331F),
        primaryContainer = darkPrimary.copy(alpha = 0.3f),
        onPrimaryContainer = darkPrimary,
        secondary = darkSecondary,
        onSecondary = Color(0xFF001F2A),
        secondaryContainer = darkSecondary.copy(alpha = 0.3f),
        onSecondaryContainer = darkSecondary,
        tertiary = darkTertiary,
        onTertiary = Color(0xFF2E1600),
        tertiaryContainer = darkTertiary.copy(alpha = 0.3f),
        onTertiaryContainer = darkTertiary,
        background = Color(0xFF121212),
        surface = Color(0xFF1C1B1F),
        surfaceVariant = Color(0xFF2C2C2C),
        onBackground = Color(0xFFE6E1E5),
        onSurface = Color(0xFFE6E1E5),
        onSurfaceVariant = Color(0xFFCAC4D0),
        error = Color(0xFFFFB4AB),
        outline = Color(0xFF938F99)
    )

    companion object {
        val ALL_THEMES = listOf(
            // FREE themes (5)
            AppTheme("mint", "Mint Segar", false,
                Color(0xFF2E7D32), Color(0xFF42A5F5), Color(0xFFFF9800),
                Color(0xFF66BB6A), Color(0xFF64B5F6), Color(0xFFFFB74D), "🌿"),
            AppTheme("ocean", "Ocean Blue", false,
                Color(0xFF1565C0), Color(0xFF00ACC1), Color(0xFF7E57C2),
                Color(0xFF42A5F5), Color(0xFF26C6DA), Color(0xFFB39DDB), "🌊"),
            AppTheme("rose", "Rose Pink", false,
                Color(0xFFC62828), Color(0xFFEC407A), Color(0xFFAB47BC),
                Color(0xFFEF5350), Color(0xFFF06292), Color(0xFFCE93D8), "🌹"),
            AppTheme("sunset", "Sunset", false,
                Color(0xFFE65100), Color(0xFFFF6F00), Color(0xFFD50000),
                Color(0xFFFF9800), Color(0xFFFFA726), Color(0xFFEF5350), "🌅"),
            AppTheme("lavender", "Lavender", false,
                Color(0xFF5E35B1), Color(0xFF7B1FA2), Color(0xFFD81B60),
                Color(0xFF9575CD), Color(0xFFBA68C8), Color(0xFFF06292), "💜"),

            // PREMIUM themes (15)
            AppTheme("midnight", "Midnight Dark", true,
                Color(0xFF37474F), Color(0xFF546E7A), Color(0xFF607D8B),
                Color(0xFF90A4AE), Color(0xFFB0BEC5), Color(0xFFCFD8DC), "🌙"),
            AppTheme("nature", "Forest Nature", true,
                Color(0xFF1B5E20), Color(0xFF33691E), Color(0xFF827717),
                Color(0xFF4CAF50), Color(0xFF689F38), Color(0xFF9E9D24), "🌲"),
            AppTheme("sakura", "Sakura", true,
                Color(0xFFAD1457), Color(0xFF6A1B9A), Color(0xFFD81B60),
                Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFFFF4081), "🌸"),
            AppTheme("coffee", "Coffee Break", true,
                Color(0xFF4E342E), Color(0xFF6D4C41), Color(0xFF8D6E63),
                Color(0xFFA1887F), Color(0xFFBCAAA4), Color(0xFFD7CCC8), "☕"),
            AppTheme("cyber", "Cyberpunk", true,
                Color(0xFF00E5FF), Color(0xFFD500F9), Color(0xFFFFEA00),
                Color(0xFF18FFFF), Color(0xFFE040FB), Color(0xFFFFEA00), "🤖"),
            AppTheme("aurora", "Aurora", true,
                Color(0xFF00C853), Color(0xFF2979FF), Color(0xFFD500F9),
                Color(0xFF69F0AE), Color(0xFF448AFF), Color(0xFFE040FB), "🌌"),
            AppTheme("earth", "Earth Tone", true,
                Color(0xFF795548), Color(0xFF558B2F), Color(0xFFF57F17),
                Color(0xFFA1887F), Color(0xFF7CB342), Color(0xFFFBC02D), "🌍"),
            AppTheme("classic", "Classic White", true,
                Color(0xFF1976D2), Color(0xFF388E3C), Color(0xFFF57C00),
                Color(0xFF42A5F5), Color(0xFF66BB6A), Color(0xFFFFB74D), "📘"),
            AppTheme("mono", "Monochrome", true,
                Color(0xFF424242), Color(0xFF616161), Color(0xFF757575),
                Color(0xFFBDBDBD), Color(0xFF9E9E9E), Color(0xFFE0E0E0), "⚫"),
            AppTheme("candy", "Candy Pop", true,
                Color(0xFFFF1744), Color(0xFF00E676), Color(0xFF2979FF),
                Color(0xFFFF5252), Color(0xFF69F0AE), Color(0xFF448AFF), "🍬"),
            AppTheme("neon", "Neon Night", true,
                Color(0xFF00BCD4), Color(0xFFE91E63), Color(0xFFFFEB3B),
                Color(0xFF00E5FF), Color(0xFFF06292), Color(0xFFFFF176), "💡"),
            AppTheme("ocean-deep", "Ocean Deep", true,
                Color(0xFF0D47A1), Color(0xFF00695C), Color(0xFF1A237E),
                Color(0xFF1976D2), Color(0xFF00897B), Color(0xFF3949AB), "🐋"),
            AppTheme("candle", "Candle Light", true,
                Color(0xFFFF6F00), Color(0xFFBF360C), Color(0xFF4E342E),
                Color(0xFFFFAB00), Color(0xFFFF6D00), Color(0xFF6D4C41), "🕯️"),
            AppTheme("snow", "Snow White", true,
                Color(0xFF0277BD), Color(0xFF00838F), Color(0xFF4A148C),
                Color(0xFF03A9F4), Color(0xFF00BCD4), Color(0xFF7C4DFF), "❄️"),
            AppTheme("berry", "Berry Bliss", true,
                Color(0xFF880E4F), Color(0xFF4A148C), Color(0xFF311B92),
                Color(0xFFC2185B), Color(0xFF7B1FA2), Color(0xFF536DFE), "🍇")
        )

        val FREE_THEMES = ALL_THEMES.filter { !it.isPremium }
        val PREMIUM_THEMES = ALL_THEMES.filter { it.isPremium }

        fun getThemeById(id: String): AppTheme = ALL_THEMES.find { it.id == id } ?: ALL_THEMES[0]
    }
}
