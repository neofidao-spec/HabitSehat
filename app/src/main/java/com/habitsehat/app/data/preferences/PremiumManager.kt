package com.habitsehat.app.data.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Manages feature gating for premium vs free.
 * Uses SettingsManager internally — this wraps it with feature-level logic.
 */
class PremiumManager(
    private val settingsManager: SettingsManager
) {
    val isPremium: Flow<Boolean> = settingsManager.isPremium

    // Feature availability checks with real-time premium status
    fun canUseTheme(themeId: String): Boolean {
        val theme = com.habitsehat.app.data.model.AppTheme.getThemeById(themeId)
        return !theme.isPremium // free themes always available
        // For premium themes, checked at runtime via isPremium flow
    }

    suspend fun canUseTheme(themeId: String, isPremiumNow: Boolean): Boolean {
        val theme = com.habitsehat.app.data.model.AppTheme.getThemeById(themeId)
        return !theme.isPremium || isPremiumNow
    }

    fun canUseThemeFlow(themeId: String): Flow<Boolean> {
        val theme = com.habitsehat.app.data.model.AppTheme.getThemeById(themeId)
        return if (theme.isPremium) {
            isPremium
        } else {
            flowOf(true)
        }
    }

    suspend fun unlockPremium() {
        settingsManager.setPremium(true)
    }

    suspend fun lockPremium() {
        settingsManager.setPremium(false)
    }

    companion object {
        // Premium feature list
        val FEATURES = listOf(
            PremiumFeature(
                "Habit Tracker",
                "Hingga 5 habit",
                "Unlimited habit & water history penuh",
                emoji = "📋"
            ),
            PremiumFeature(
                "HabitStop",
                "❌ Tidak tersedia",
                "Pantau kebiasaan buruk + money saved tracker + streak",
                emoji = "🚫"
            ),
            PremiumFeature(
                "Tema Eksklusif",
                "3 tema dasar",
                "20+ tema premium (Cyberpunk, Sakura, Aurora dll)",
                emoji = "🎨"
            ),
            PremiumFeature(
                "Widget Layar Utama",
                "Widget mini",
                "Widget medium + besar interaktif (tap langsung centang)",
                emoji = "📱"
            ),
            PremiumFeature(
                "Laporan Mingguan",
                "❌ Tidak tersedia",
                "Ringkasan mingguan + insight otomatis",
                emoji = "📊"
            ),
            PremiumFeature(
                "Tantangan (Challenge)",
                "❌ Tidak tersedia",
                "Challenge 7/21/30 hari + badge",
                emoji = "🏆"
            ),
            PremiumFeature(
                "Pomodoro Timer",
                "❌ Tidak tersedia",
                "Timer fokus 25/50/90 menit + statistik",
                emoji = "🍅"
            ),
            PremiumFeature(
                "Pencatatan Pengeluaran",
                "❌ Tidak tersedia",
                "Catat pengeluaran sehat (seminar, bootcamp) + rekap mingguan",
                emoji = "💰"
            ),
            PremiumFeature(
                "Backup Google Drive",
                "❌ Tidak tersedia",
                "Auto backup harian, restore kapan aja",
                emoji = "☁️"
            )
        )
    }
}

data class PremiumFeature(
    val name: String,
    val freeDescription: String,
    val premiumDescription: String,
    val emoji: String
)