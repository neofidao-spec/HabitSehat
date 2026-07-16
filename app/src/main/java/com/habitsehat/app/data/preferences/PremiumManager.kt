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

    /**
     * DEVELOPMENT-ONLY: langsung set premium tanpa validasi pembayaran.
     * TODO: Ganti dengan Play Billing purchase flow sebelum production.
     */
    suspend fun unlockPremium() {
        settingsManager.setPremium(true)
    }

    /**
     * DEVELOPMENT-ONLY: pulihkan status premium tanpa verifikasi receipt.
     * TODO: Implementasi receipt validation via Play Billing (purchase token).
     * Untuk sekarang: cek apakah pernah ada history pembelian tersimpan,
     * lalu restore premium. Karena belum ada billing nyata, langsung unlock.
     */
    suspend fun restorePurchases(): Boolean {
        // TODO: cek purchase history / receipt dari Google Play
        // val purchases = billingClient.queryPurchasesAsync(...)
        // if (purchases.isNotEmpty()) { settingsManager.setPremium(true) }
        settingsManager.setPremium(true)
        return true
    }

    /**
     * DEVELOPMENT-ONLY: langsung lock tanpa validasi.
     * TODO: Integrasi dengan cancellation/expiry flow.
     */
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
                "Lihat saja",
                "Pantau + kelola semua kebiasaan buruk + money saved",
                emoji = "🚫"
            ),
            PremiumFeature(
                "Tema Eksklusif",
                "5 tema dasar",
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
                "Ringkasan dasar",
                "Laporan full + insight otomatis + statistik habit",
                emoji = "📊"
            ),
            PremiumFeature(
                "Tantangan (Challenge)",
                "Challenge gratis terbatas",
                "Semua challenge + badge completion + streak leaderboard",
                emoji = "🏆"
            ),
            PremiumFeature(
                "Pomodoro Timer",
                "25/50 menit",
                "Timer 90 menit + white noise + statistik harian",
                emoji = "🍅"
            ),
            PremiumFeature(
                "Pencatatan Pengeluaran",
                "Catat pengeluaran",
                "Kategori kustom + rekap mingguan + export data",
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