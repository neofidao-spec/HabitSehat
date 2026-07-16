package com.habitsehat.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.habitsehat.app.data.model.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.prefs: DataStore<Preferences> by preferencesDataStore(name = "habitsehat")

class SettingsManager(private val context: Context) {

    companion object {
        private val KEY_THEME = stringPreferencesKey("selected_theme")
        private val KEY_IS_PREMIUM = booleanPreferencesKey("is_premium")
        private val KEY_DARK_MODE = stringPreferencesKey("dark_mode")
        private val KEY_WATER_GOAL = intPreferencesKey("water_goal")
        private val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        private val KEY_LAST_WEEKLY_REPORT = stringPreferencesKey("last_weekly_report")
    }

    val currentThemeId: Flow<String> = context.prefs.data.map { prefs ->
        prefs[KEY_THEME] ?: "mint"
    }

    val currentTheme: Flow<AppTheme> = currentThemeId.map { id ->
        AppTheme.getThemeById(id)
    }

    suspend fun setTheme(themeId: String) {
        context.prefs.edit { it[KEY_THEME] = themeId }
    }

    val isPremium: Flow<Boolean> = context.prefs.data.map { prefs ->
        prefs[KEY_IS_PREMIUM] ?: true  // Default to true for premium feel
    }

    suspend fun setPremium(enabled: Boolean) {
        context.prefs.edit { it[KEY_IS_PREMIUM] = enabled }
    }

    val darkModeSetting: Flow<String> = context.prefs.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: "system"
    }

    suspend fun setDarkMode(mode: String) {
        context.prefs.edit { it[KEY_DARK_MODE] = mode }
    }

    val waterGoal: Flow<Int> = context.prefs.data.map { prefs ->
        prefs[KEY_WATER_GOAL] ?: 2500
    }

    suspend fun setWaterGoal(ml: Int) {
        context.prefs.edit { it[KEY_WATER_GOAL] = ml }
    }

    val lastWeeklyReportDate: Flow<String?> = context.prefs.data.map { prefs ->
        prefs[KEY_LAST_WEEKLY_REPORT]
    }

    suspend fun setLastWeeklyReport(date: String) {
        context.prefs.edit { it[KEY_LAST_WEEKLY_REPORT] = date }
    }

    val isFirstLaunch: Flow<Boolean> = context.prefs.data.map { prefs ->
        prefs[KEY_FIRST_LAUNCH] ?: true
    }

    suspend fun setFirstLaunchComplete() {
        context.prefs.edit { it[KEY_FIRST_LAUNCH] = false }
    }
}
