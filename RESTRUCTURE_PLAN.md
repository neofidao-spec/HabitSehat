# 🎯 RESTRUCTURE PLAN: HabitSehat — Google Play Store Ready

**Status**: ⚠️ Critical Review — Production-Grade Overhaul  
**Target**: 5★ Rating (High Quality, Performance, UX Standards)  
**Timeline**: Sprint 1 (Core), Sprint 2 (Polish), Sprint 3 (Store Release)

---

## 📋 EXECUTIVE SUMMARY

### Current State
✅ **Foundation**: Modern stack (Jetpack Compose, Material3, Room, WorkManager)  
✅ **Architecture**: Clean separation (data, ui)  
✅ **Documentation**: Comprehensive (PLAY_STORE_LISTING.md, etc)  

❌ **Critical Issues**:
1. **UI/UX**: Minimal theme definition - no professional color system
2. **Code Structure**: Limited modularity (missing view models, use cases layer)
3. **Error Handling**: No visible error UI states
4. **Testing**: Test dependencies added but no actual tests
5. **Performance**: No optimization checks (APK size, memory, startup time)
6. **Localization**: UI ready but strings need expansion
7. **Accessibility**: No a11y considerations mentioned
8. **Build/Release**: Workflow solid but missing CI quality gates

---

## 🏗️ PHASE 1: ARCHITECTURE RESTRUCTURE

### 1.1 Package Structure (NEW)
```
app/src/main/java/com/habitsehat/app/
├── MainActivity.kt                          # Entry point
├── HabitSehatApp.kt                         # App class
│
├── data/                                    # Data Layer
│   ├── local/
│   │   ├── database/
│   │   │   ├── HabitDatabase.kt
│   │   │   ├── dao/
│   │   │   │   ├── HabitDao.kt
│   │   │   │   ├── WaterLogDao.kt
│   │   │   │   └── StreakDao.kt
│   │   │   └── entity/
│   │   │       ├── HabitEntity.kt
│   │   │       ├── WaterLogEntity.kt
│   │   │       └── StreakEntity.kt
│   │   └── preferences/
│   │       ├── UserPreferences.kt
│   │       └── ThemePreferences.kt
│   ├── repository/
│   │   ├── HabitRepository.kt
│   │   ├── WaterRepository.kt
│   │   ├── StreakRepository.kt
│   │   └── ThemeRepository.kt
│   └── mapper/
│       ├── HabitMapper.kt
│       └── WaterMapper.kt
│
├── domain/                                  # Domain Layer (Logic)
│   ├── model/
│   │   ├── Habit.kt
│   │   ├── Water.kt
│   │   ├── Streak.kt
│   │   └── ThemeConfig.kt
│   ├── usecase/
│   │   ├── habit/
│   │   │   ├── GetHabitsUseCase.kt
│   │   │   ├── AddHabitUseCase.kt
│   │   │   ├── UpdateHabitUseCase.kt
│   │   │   ├── DeleteHabitUseCase.kt
│   │   │   └── LogHabitUseCase.kt
│   │   ├── water/
│   │   │   ├── LogWaterUseCase.kt
│   │   │   ├── GetWaterHistoryUseCase.kt
│   │   │   └── CalculateStreakUseCase.kt
│   │   └── theme/
│   │       ├── GetThemeUseCase.kt
│   │       └── SetThemeUseCase.kt
│   └── validator/
│       ├── HabitValidator.kt
│       └── InputValidator.kt
│
├── ui/                                      # UI Layer
│   ├── theme/
│   │   ├── Theme.kt                         # Material3 colors & typography
│   │   ├── Color.kt                         # Complete color palette
│   │   ├── Type.kt                          # Typography system
│   │   └── Shape.kt                         # Corner radius system
│   ├── navigation/
│   │   ├── NavGraph.kt
│   │   ├── Screen.kt                        # Screen routing
│   │   └── NavHost.kt
│   ├── screen/
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   ├── HomeViewModel.kt
│   │   │   └── HomeUiState.kt
│   │   ├── habit/
│   │   │   ├── HabitListScreen.kt
│   │   │   ├── AddHabitScreen.kt
│   │   │   ├── HabitViewModel.kt
│   │   │   └── HabitUiState.kt
│   │   ├── water/
│   │   │   ├── WaterScreen.kt
│   │   │   ├── WaterViewModel.kt
│   │   │   └── WaterUiState.kt
│   │   ├── heatmap/
│   │   │   ├── HeatmapScreen.kt
│   │   │   ├── HeatmapViewModel.kt
│   │   │   └── HeatmapUiState.kt
│   │   ├── settings/
│   │   │   ├── SettingsScreen.kt
│   │   │   ├── ThemeSettingsScreen.kt
│   │   │   └── SettingsViewModel.kt
│   │   └── splash/
│   │       └── SplashScreen.kt
│   ├── component/
│   │   ├── common/
│   │   │   ├── AppTopBar.kt
│   │   │   ├── AppBottomBar.kt
│   │   │   ├── AppButton.kt
│   │   │   ├── AppCard.kt
│   │   │   ├── LoadingIndicator.kt
│   │   │   ├── EmptyState.kt
│   │   │   └── ErrorState.kt
│   │   ├── habit/
│   │   │   ├── HabitCard.kt
│   │   │   ├── HabitForm.kt
│   │   │   └── StreakBadge.kt
│   │   └── water/
│   │       ├── WaterProgressRing.kt
│   │       ├── WaterQuickAdd.kt
│   │       └── HydrationChart.kt
│   └── widgets/                             # App Widgets
│       ├── HabitWidgetProvider.kt
│       ├── HabitWidgetMediumProvider.kt
│       ├── HabitWidgetLargeProvider.kt
│       └── glance/
│           ├── MiniWidget.kt
│           ├── MediumWidget.kt
│           └── LargeWidget.kt
│
├── util/                                    # Utilities
│   ├── constant/
│   │   ├── Constants.kt
│   │   ├── ErrorMessages.kt
│   │   └── AppDefaults.kt
│   ├── extension/
│   │   ├── DateExtensions.kt
│   │   ├── StringExtensions.kt
│   │   └── FlowExtensions.kt
│   ├── logger/
│   │   └── AppLogger.kt
│   └── permission/
│       └── PermissionHelper.kt
│
├── di/                                      # Dependency Injection (Future: Hilt)
│   └── AppModule.kt
│
└── worker/                                  # Background Work
    ├── HabitReminderWorker.kt
    └── WidgetUpdateWorker.kt

app/src/main/res/
├── values/
│   ├── colors.xml                           # ✅ NEW: Complete palette
│   ├── strings.xml                          # ✅ EXPANDED: All strings
│   ├── themes.xml                           # ✅ NEW: Material3 theme
│   ├── dimens.xml                           # ✅ NEW: Spacing system
│   └── styles.xml                           # ✅ NEW: Reusable styles
├── xml/
│   ├── widget_mini_info.xml
│   ├── widget_medium_info.xml
│   └── widget_large_info.xml
└── drawable/
    └── ic_launcher_background.xml
```

---

## 🎨 PHASE 2: UI/UX PROFESSIONAL REDESIGN

### 2.1 Color System (CRITICAL)

#### Primary Palette (Health/Wellness Theme)
```xml
<!-- app/src/main/res/values/colors.xml -->

<!-- PRIMARY: Emerald Green (Health/Growth) -->
<color name="md_theme_light_primary">#2D8A61</color>        <!-- Main green -->
<color name="md_theme_light_onPrimary">#FFFFFF</color>
<color name="md_theme_light_primaryContainer">#B0F0D9</color> <!-- Light green -->
<color name="md_theme_light_onPrimaryContainer">#002114</color>

<!-- SECONDARY: Ocean Blue (Hydration) -->
<color name="md_theme_light_secondary">#0066CC</color>
<color name="md_theme_light_onSecondary">#FFFFFF</color>
<color name="md_theme_light_secondaryContainer">#CCE5FF</color>
<color name="md_theme_light_onSecondaryContainer">#001C47</color>

<!-- TERTIARY: Warm Amber (Energy) -->
<color name="md_theme_light_tertiary">#FF9500</color>
<color name="md_theme_light_onTertiary">#FFFFFF</color>
<color name="md_theme_light_tertiaryContainer">#FFE4CC</color>
<color name="md_theme_light_onTertiaryContainer">#331E00</color>

<!-- ERROR: Red -->
<color name="md_theme_light_error">#D00000</color>
<color name="md_theme_light_onError">#FFFFFF</color>
<color name="md_theme_light_errorContainer">#FFCCCC</color>
<color name="md_theme_light_onErrorContainer">#330000</color>

<!-- SURFACE & NEUTRAL -->
<color name="md_theme_light_surface">#FFFBFE</color>
<color name="md_theme_light_onSurface">#1A1A1A</color>
<color name="md_theme_light_surfaceVariant">#F0E4EA</color>
<color name="md_theme_light_onSurfaceVariant">#49454E</color>
<color name="md_theme_light_outline">#7C747E</color>
<color name="md_theme_light_outlineVariant">#C8C1C9</color>
<color name="md_theme_light_background">#FFFBFE</color>
<color name="md_theme_light_onBackground">#1A1A1A</color>

<!-- DARK MODE -->
<color name="md_theme_dark_primary">#81D9B1</color>
<color name="md_theme_dark_onPrimary">#003D25</color>
<color name="md_theme_dark_primaryContainer">#005A3C</color>
<color name="md_theme_dark_onPrimaryContainer">#B0F0D9</color>

<!-- ... Dark mode secondary, tertiary, error, surface ... -->

<!-- STATUS COLORS -->
<color name="status_success">#2D8A61</color>
<color name="status_warning">#FF9500</color>
<color name="status_error">#D00000</color>
<color name="status_info">#0066CC</color>

<!-- HEATMAP COLORS -->
<color name="heatmap_level_0">#E0E0E0</color>        <!-- Empty -->
<color name="heatmap_level_1">#C3E9DC</color>        <!-- 1-25% -->
<color name="heatmap_level_2">#78D9B1</color>        <!-- 26-50% -->
<color name="heatmap_level_3">#2D8A61</color>        <!-- 51-75% -->
<color name="heatmap_level_4">#004D2C</color>        <!-- 76-100% -->

<!-- STREAK COLORS -->
<color name="streak_flame">#FF6B35</color>           <!-- Active streak -->
<color name="streak_standard">#2D8A61</color>        <!-- Normal -->
<color name="streak_broken">#9E9E9E</color>          <!-- Broken -->

<!-- WATER COLORS -->
<color name="water_primary">#0066CC</color>
<color name="water_light">#E3F2FD</color>
<color name="water_gradient_start">#0066CC</color>
<color name="water_gradient_end">#81D9B1</color>

<!-- HABIT CATEGORY COLORS -->
<color name="habit_exercise">#FF6B35</color>        <!-- Orange: Energy -->
<color name="habit_reading">#8B6BB1</color>          <!-- Purple: Mind -->
<color name="habit_meditation">#2D8A61</color>       <!-- Green: Calm -->
<color name="habit_nutrition">#FFA500</color>        <!-- Gold: Health -->
<color name="habit_sleep">#4A5569</color>            <!-- Navy: Rest -->
<color name="habit_custom">#0066CC</color>           <!-- Blue: Default -->
```

### 2.2 Typography System

```kotlin
// app/src/main/java/com/habitsehat/app/ui/theme/Type.kt

val HabitSehatTypography = Typography(
    // Headlines
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    
    // Headings
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.5.sp
    ),
    
    // Body Text
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    
    // Labels
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

### 2.3 Spacing System (Material 3)

```kotlin
// app/src/main/java/com/habitsehat/app/ui/theme/Spacing.kt

object AppSpacing {
    val xs = 4.dp      // Minimal spacing
    val sm = 8.dp      // Small elements
    val md = 16.dp     // Default spacing (MOST COMMON)
    val lg = 24.dp     // Large sections
    val xl = 32.dp     // Major sections
    val xxl = 48.dp    // Screen-level spacing
    
    // Component heights
    val buttonHeight = 48.dp
    val topBarHeight = 64.dp
    val bottomBarHeight = 80.dp
}

object AppCornerRadius {
    val small = 4.dp       // Buttons, small components
    val medium = 8.dp      // Cards, dialogs
    val large = 12.dp      // Large cards, modal sheets
    val extraLarge = 16.dp // Full-screen sheets
}

object AppElevation {
    val level0 = 0.dp
    val level1 = 2.dp      // Subtle elevation
    val level2 = 4.dp      // Cards
    val level3 = 6.dp      // Floating buttons
    val level4 = 8.dp      // Modals
    val level5 = 12.dp     // Top app bar
}
```

### 2.4 Shape System

```kotlin
// app/src/main/java/com/habitsehat/app/ui/theme/Shape.kt

val HabitSehatShapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp)
)
```

### 2.5 Material3 Theme Integration

```kotlin
// app/src/main/java/com/habitsehat/app/ui/theme/Theme.kt

@Composable
fun HabitSehatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme(
            primary = Color(0x81D9B1),
            onPrimary = Color(0x003D25),
            primaryContainer = Color(0x005A3C),
            onPrimaryContainer = Color(0xB0F0D9),
            secondary = Color(0x81C5FF),
            onSecondary = Color(0x003459),
            secondaryContainer = Color(0x004C7D),
            onSecondaryContainer = Color(0xCCE5FF),
            tertiary = Color(0xFFB84D),
            onTertiary = Color(0x4D2600),
            tertiaryContainer = Color(0x6D3D00),
            onTertiaryContainer = Color(0xFFE4CC),
            error = Color(0xFFB4AB),
            onError = Color(0x5C0002),
            errorContainer = Color(0x93000A),
            onErrorContainer = Color(0xFFDAD6),
            background = Color(0x1A1A1A),
            onBackground = Color(0xE5E5E5),
            surface = Color(0x121212),
            onSurface = Color(0xE5E5E5),
            surfaceVariant = Color(0x49454E),
            onSurfaceVariant = Color(0xC8C1C9),
            outline = Color(0x92909B),
            outlineVariant = Color(0x49454E),
            scrim = Color(0x000000)
        )
        else -> lightColorScheme(
            primary = Color(0x2D8A61),
            onPrimary = Color(0xFFFFFF),
            primaryContainer = Color(0xB0F0D9),
            onPrimaryContainer = Color(0x002114),
            secondary = Color(0x0066CC),
            onSecondary = Color(0xFFFFFF),
            secondaryContainer = Color(0xCCE5FF),
            onSecondaryContainer = Color(0x001C47),
            tertiary = Color(0xFF9500),
            onTertiary = Color(0xFFFFFF),
            tertiaryContainer = Color(0xFFE4CC),
            onTertiaryContainer = Color(0x331E00),
            error = Color(0xD00000),
            onError = Color(0xFFFFFF),
            errorContainer = Color(0xFFCCCC),
            onErrorContainer = Color(0x330000),
            background = Color(0xFFFBFE),
            onBackground = Color(0x1A1A1A),
            surface = Color(0xFFFBFE),
            onSurface = Color(0x1A1A1A),
            surfaceVariant = Color(0xF0E4EA),
            onSurfaceVariant = Color(0x49454E),
            outline = Color(0x7C747E),
            outlineVariant = Color(0xC8C1C9),
            scrim = Color(0x000000)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HabitSehatTypography,
        shapes = HabitSehatShapes,
        content = content
    )
}
```

### 2.6 Comprehensive Strings Resource

```xml
<!-- app/src/main/res/values/strings.xml -->
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- App -->
    <string name="app_name">HabitSehat</string>
    <string name="app_version">1.0.0</string>
    <string name="app_description">Aplikasi Android untuk membangun kebiasaan sehat dan melacak konsumsi air minum</string>

    <!-- Navigation -->
    <string name="nav_home">Beranda</string>
    <string name="nav_habits">Kebiasaan</string>
    <string name="nav_water">Air Minum</string>
    <string name="nav_heatmap">Kalender</string>
    <string name="nav_settings">Pengaturan</string>

    <!-- Home Screen -->
    <string name="home_title">Selamat Pagi</string>
    <string name="home_subtitle">Mari bangun kebiasaan sehat hari ini</string>
    <string name="home_habits_today">Kebiasaan Hari Ini</string>
    <string name="home_completion_rate">Tingkat Penyelesaian</string>
    <string name="home_best_streak">Streak Terbaik</string>
    <string name="home_empty_habits">Belum ada kebiasaan. Mulai dengan menambahkan kebiasaan baru!</string>

    <!-- Habit Tracker -->
    <string name="habit_tracker">Pelacak Kebiasaan</string>
    <string name="habit_add">Tambah Kebiasaan</string>
    <string name="habit_name">Nama Kebiasaan</string>
    <string name="habit_category">Kategori</string>
    <string name="habit_goal">Target (hari per minggu)</string>
    <string name="habit_reminder">Pengingat</string>
    <string name="habit_color">Warna</string>
    <string name="habit_icon">Ikon</string>
    <string name="habit_description">Deskripsi (opsional)</string>
    <string name="habit_delete">Hapus Kebiasaan</string>
    <string name="habit_delete_confirm">Yakin ingin menghapus kebiasaan ini? Data akan hilang permanen.</string>
    <string name="habit_edit">Edit Kebiasaan</string>
    <string name="habit_log">Catat Hari Ini</string>
    <string name="habit_unlog">Batalkan</string>
    <string name="habit_streak">Streak: %d hari</string>
    <string name="habit_best_streak">Best: %d hari</string>
    <string name="habit_completion">Penyelesaian: %d%%</string>

    <!-- Habit Categories -->
    <string name="habit_category_exercise">Olahraga</string>
    <string name="habit_category_reading">Membaca</string>
    <string name="habit_category_meditation">Meditasi</string>
    <string name="habit_category_nutrition">Nutrisi</string>
    <string name="habit_category_sleep">Tidur</string>
    <string name="habit_category_custom">Custom</string>

    <!-- Water Tracker -->
    <string name="water_tracker">Pelacak Air Minum</string>
    <string name="water_goal">Target Harian</string>
    <string name="water_goal_default">2500 ml</string>
    <string name="water_current">Hari Ini: %d ml</string>
    <string name="water_remaining">Sisa: %d ml</string>
    <string name="water_progress">Progres: %d%%</string>
    <string name="water_add">Tambah Air</string>
    <string name="water_quick_add_water">Air Putih</string>
    <string name="water_quick_add_coffee">Kopi</string>
    <string name="water_quick_add_tea">Teh</string>
    <string name="water_quick_add_juice">Jus</string>
    <string name="water_log_success">Air berhasil dicatat!</string>
    <string name="water_log_exceeded">Anda sudah mencapai target harian!</string>
    <string name="water_history">Riwayat Minum</string>
    <string name="water_weekly">Mingguan</string>
    <string name="water_monthly">Bulanan</string>

    <!-- Streak & Heatmap -->
    <string name="streak_title">Streak Anda</string>
    <string name="streak_current">Streak Saat Ini</string>
    <string name="streak_best">Streak Terbaik</string>
    <string name="streak_days">%d hari</string>
    <string name="heatmap_calendar">Kalender Konsistensi</string>
    <string name="heatmap_contribution">Kontribusi</string>
    <string name="heatmap_year">Tahun: %d</string>
    <string name="heatmap_empty">Belum ada data</string>
    <string name="heatmap_incomplete">Tidak lengkap</string>
    <string name="heatmap_complete">Lengkap</string>

    <!-- Settings -->
    <string name="settings">Pengaturan</string>
    <string name="settings_appearance">Tampilan</string>
    <string name="settings_theme">Tema</string>
    <string name="settings_dark_mode">Mode Gelap</string>
    <string name="settings_language">Bahasa</string>
    <string name="settings_notifications">Notifikasi</string>
    <string name="settings_notification_enabled">Aktifkan Notifikasi</string>
    <string name="settings_backup">Backup & Restore</string>
    <string name="settings_about">Tentang</string>
    <string name="settings_privacy_policy">Kebijakan Privasi</string>
    <string name="settings_version">Versi: %s</string>
    <string name="settings_developed_by">Dikembangkan oleh Neofidao</string>

    <!-- Common Actions -->
    <string name="action_save">Simpan</string>
    <string name="action_cancel">Batal</string>
    <string name="action_delete">Hapus</string>
    <string name="action_edit">Edit</string>
    <string name="action_add">Tambah</string>
    <string name="action_done">Selesai</string>
    <string name="action_close">Tutup</string>
    <string name="action_ok">OK</string>
    <string name="action_yes">Ya</string>
    <string name="action_no">Tidak</string>
    <string name="action_retry">Coba Lagi</string>
    <string name="action_share">Bagikan</string>
    <string name="action_settings">Pengaturan</string>

    <!-- Error Messages -->
    <string name="error_generic">Terjadi kesalahan. Silakan coba lagi.</string>
    <string name="error_empty_field">Bidang tidak boleh kosong</string>
    <string name="error_invalid_name">Nama kebiasaan tidak valid</string>
    <string name="error_invalid_goal">Target harus antara 1-7</string>
    <string name="error_database">Gagal mengakses database</string>
    <string name="error_permission">Izin tidak diberikan</string>
    <string name="error_invalid_input">Input tidak valid</string>

    <!-- Success Messages -->
    <string name="success_habit_added">Kebiasaan berhasil ditambahkan</string>
    <string name="success_habit_updated">Kebiasaan berhasil diperbarui</string>
    <string name="success_habit_deleted">Kebiasaan berhasil dihapus</string>
    <string name="success_logged">Berhasil dicatat untuk hari ini</string>

    <!-- Premium Features -->
    <string name="premium_title">Premium</string>
    <string name="premium_unlock">Buka Semua Fitur</string>
    <string name="premium_features_list">20+ Tema, Weekly Report, Pomodoro Timer, Challenges &amp; Badges, Widget Premium, Backup ke Cloud</string>
    <string name="premium_monthly">Rp19.000/bulan</string>
    <string name="premium_lifetime">Rp199.000 (Seumur Hidup)</string>

    <!-- Widget -->
    <string name="widget_mini_desc">Mini widget — streak &amp; progress</string>
    <string name="widget_medium_desc">Medium widget — daftar kebiasaan</string>
    <string name="widget_large_desc">Large widget — daftar lengkap &amp; air minum</string>
    <string name="widget_update">Update widget</string>
    <string name="widget_tap_to_log">Tap untuk mencatat</string>

    <!-- Accessibility -->
    <string name="a11y_habit_card">Kebiasaan %s</string>
    <string name="a11y_streak_badge">Streak %d hari</string>
    <string name="a11y_water_progress">Progres air minum %d persen</string>
    <string name="a11y_button_add">Tombol tambah</string>
    <string name="a11y_button_delete">Tombol hapus</string>
    <string name="a11y_heatmap_cell">Sel kalender %s dengan aktivitas %s</string>
</resources>
```

---

## 🏆 PHASE 3: CODE QUALITY & BEST PRACTICES

### 3.1 ViewModel Pattern Implementation

```kotlin
// app/src/main/java/com/habitsehat/app/ui/screen/habit/HabitUiState.kt

sealed class HabitUiState {
    object Loading : HabitUiState()
    data class Success(
        val habits: List<HabitUiModel>,
        val todayCount: Int,
        val completionRate: Float
    ) : HabitUiState()
    data class Error(val message: String) : HabitUiState()
}

data class HabitUiModel(
    val id: String,
    val name: String,
    val category: String,
    val streak: Int,
    val bestStreak: Int,
    val isCompletedToday: Boolean,
    val color: Long,
    val icon: String
)
```

```kotlin
// app/src/main/java/com/habitsehat/app/ui/screen/habit/HabitViewModel.kt

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val logHabitUseCase: LogHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HabitUiState>(HabitUiState.Loading)
    val uiState: StateFlow<HabitUiState> = _uiState.asStateFlow()
    
    init {
        loadHabits()
    }
    
    fun loadHabits() {
        viewModelScope.launch {
            try {
                _uiState.value = HabitUiState.Loading
                getHabitsUseCase().collect { habits ->
                    _uiState.value = HabitUiState.Success(
                        habits = habits.map { it.toUiModel() },
                        todayCount = habits.count { it.isCompletedToday },
                        completionRate = calculateCompletion(habits)
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HabitUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun logHabit(habitId: String) {
        viewModelScope.launch {
            try {
                logHabitUseCase(habitId)
            } catch (e: Exception) {
                _uiState.value = HabitUiState.Error(e.message ?: "Failed to log habit")
            }
        }
    }
    
    private fun calculateCompletion(habits: List<Habit>): Float {
        if (habits.isEmpty()) return 0f
        return habits.count { it.isCompletedToday } / habits.size.toFloat()
    }
}
```

### 3.2 Error Handling UI Components

```kotlin
// app/src/main/java/com/habitsehat/app/ui/component/ErrorState.kt

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = android.R.drawable.ic_dialog_alert),
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(AppSpacing.md))
        
        Text(
            text = "Oops!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(AppSpacing.lg))
        
        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .height(AppSpacing.buttonHeight)
        ) {
            Text("Coba Lagi")
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    description: String,
    icon: Int? = null,
    onAction: (() -> Unit)? = null,
    actionText: String = "Tambah",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .alpha(0.5f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(AppSpacing.md))
        }
        
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        
        if (onAction != null) {
            Spacer(modifier = Modifier.height(AppSpacing.lg))
            Button(
                onClick = onAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppSpacing.buttonHeight)
            ) {
                Text(actionText)
            }
        }
    }
}
```

### 3.3 Loading State Component

```kotlin
// app/src/main/java/com/habitsehat/app/ui/component/LoadingIndicator.kt

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "Loading..."
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppSpacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp,
            modifier = Modifier.size(48.dp)
        )
        
        Spacer(modifier = Modifier.height(AppSpacing.md))
        
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SkeletonLoader(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(modifier = modifier.fillMaxSize()) {
        repeat(itemCount) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(AppSpacing.md)
                    .clip(RoundedCornerShape(AppCornerRadius.medium)),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {}
            Spacer(modifier = Modifier.height(AppSpacing.sm))
        }
    }
}
```

---

## 📱 PHASE 4: PERFORMANCE OPTIMIZATION

### 4.1 APK Size Optimization

**Target**: < 5MB  
**Checklist**:
- ✅ Enable ProGuard/R8 minification
- ✅ Enable resource shrinking
- ✅ Vector drawables only (no PNG assets where possible)
- ✅ Bundle split by ABI/density
- ✅ Remove unused dependencies
- ✅ Image optimization (WebP format for assets)

**Build Configuration** (app/build.gradle.kts):
```kotlin
release {
    isMinifyEnabled = true           // ProGuard
    isShrinkResources = true         // Unused resource removal
    
    proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}

bundle {
    abi { enableSplit = true }       // Split by CPU arch
    density { enableSplit = true }   // Split by screen density
    language { enableSplit = true }  // Split by language
}
```

### 4.2 Memory & Performance

```kotlin
// Enable strict mode in debug
if (BuildConfig.DEBUG) {
    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy.Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .penaltyLog()
            .build()
    )
}
```

### 4.3 Startup Time Optimization

- Use `@HiltViewModel` lazy injection
- Defer heavy initialization to background threads
- Implement App Startup library for provider initialization

---

## ♿ PHASE 5: ACCESSIBILITY (A11y)

### 5.1 Mandatory Accessibility Features

```kotlin
// All interactive elements need contentDescription
Button(
    onClick = { /* ... */ },
    modifier = Modifier.semantics { contentDescription = "Add new habit button" }
) { }

// Use Modifier.semantics for custom components
Surface(
    modifier = Modifier.semantics { 
        contentDescription = "Habit card for ${habit.name}"
        onClick(label = "Mark complete") { logHabit() }
    }
) { }

// Text contrast minimum: WCAG AA (4.5:1 for body text)
Text(
    text = "Important text",
    color = MaterialTheme.colorScheme.onSurface  // Sufficient contrast
)

// Touch target minimum: 48dp x 48dp
Button(
    modifier = Modifier.size(48.dp, 48.dp)  // Min size
) { }
```

### 5.2 Strings for Screen Readers

```xml
<!-- values/strings.xml -->
<string name="a11y_habit_completed">Kebiasaan %s selesai hari ini</string>
<string name="a11y_streak_badge">Streak %d hari untuk %s</string>
<string name="a11y_water_progress">Anda telah minum %d dari %d mL air</string>
```

---

## 🧪 PHASE 6: TESTING FOUNDATION

### 6.1 Unit Tests (Essential)

```kotlin
// app/src/test/java/com/habitsehat/app/domain/usecase/LogHabitUseCaseTest.kt

@ExperimentalCoroutinesApi
class LogHabitUseCaseTest {
    
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var habitRepository: FakeHabitRepository
    private lateinit var logHabitUseCase: LogHabitUseCase
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        habitRepository = FakeHabitRepository()
        logHabitUseCase = LogHabitUseCase(habitRepository)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun logHabit_shouldUpdateStreak() = runTest {
        val habitId = "test-habit"
        val habit = Habit(id = habitId, name = "Exercise", streak = 5)
        habitRepository.addHabit(habit)
        
        logHabitUseCase(habitId)
        
        val updated = habitRepository.getHabit(habitId)
        assertTrue(updated.isCompletedToday)
    }
    
    @Test
    fun logHabit_withBrokenStreak_shouldReset() = runTest {
        val habitId = "test-habit"
        val habit = Habit(
            id = habitId,
            name = "Exercise",
            streak = 5,
            lastLogDate = 3.days.ago
        )
        habitRepository.addHabit(habit)
        
        logHabitUseCase(habitId)
        
        val updated = habitRepository.getHabit(habitId)
        assertEquals(1, updated.streak)  // Streak should reset
    }
}
```

### 6.2 UI Tests (Snapshot Testing)

```kotlin
// app/src/androidTest/java/com/habitsehat/app/ui/screen/habit/HabitScreenTest.kt

@RunWith(AndroidJUnit4::class)
class HabitScreenTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun habitScreen_displaysHabits() {
        val habits = listOf(
            HabitUiModel("1", "Exercise", "exercise", 5, 10, true, 0xFFFF9500, "dumbbell"),
            HabitUiModel("2", "Reading", "reading", 3, 8, false, 0xFF8B6BB1, "book")
        )
        
        composeTestRule.setContent {
            HabitSehatTheme {
                HabitScreen(
                    uiState = HabitUiState.Success(habits, 1, 0.5f),
                    onHabitClick = {},
                    onAddClick = {},
                    onLogClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Exercise").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reading").assertIsDisplayed()
    }
    
    @Test
    fun habitScreen_showsEmptyState_whenNoHabits() {
        composeTestRule.setContent {
            HabitSehatTheme {
                HabitScreen(
                    uiState = HabitUiState.Success(emptyList(), 0, 0f),
                    onHabitClick = {},
                    onAddClick = {},
                    onLogClick = {}
                )
            }
        }
        
        composeTestRule.onNodeWithText("Belum ada kebiasaan").assertIsDisplayed()
    }
}
```

---

## 🔐 PHASE 7: SECURITY & PRIVACY

### 7.1 Data Protection

- ✅ Room Database encryption (optional, for premium features)
- ✅ DataStore uses encrypted SharedPreferences
- ✅ No sensitive data in logs
- ✅ Validate all user inputs
- ✅ No hardcoded credentials

### 7.2 Permissions

```xml
<!-- AndroidManifest.xml - Minimize requested permissions -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
<uses-permission android:name="android.permission.VIBRATE" />
<!-- NO internet, camera, location, contacts, etc -->
```

### 7.3 Runtime Permissions

```kotlin
// Check before requesting
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
}
```

---

## 🔄 PHASE 8: BUILD & RELEASE PIPELINE

### 8.1 Improved Build Workflow

```yaml
# .github/workflows/build.yml - Enhanced version

name: Build and Release APK

on:
  push:
    branches: [main]
    tags: ['v*']
  pull_request:
    branches: [main]

env:
  GRADLE_CACHE_KEY: gradle-${{ runner.os }}-${{ hashFiles('**/*.gradle.kts') }}

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Cache Gradle
        uses: actions/cache@v4
        with:
          path: ~/.gradle/caches
          key: ${{ env.GRADLE_CACHE_KEY }}
          restore-keys: gradle-${{ runner.os }}-

      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v4

      - name: Run unit tests
        run: ./gradlew testDebugUnitTest --no-daemon

      - name: Run lint checks
        run: ./gradlew lint --no-daemon

  build:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Grant execute permission
        run: chmod +x gradlew

      - name: Build Debug APK
        run: ./gradlew assembleDebug --no-daemon

      - name: Upload Debug APK
        uses: actions/upload-artifact@v4
        with:
          name: HabitSehat-Debug-${{ github.run_number }}
          path: app/build/outputs/apk/debug/app-debug.apk
          retention-days: 7

  release:
    if: startsWith(github.ref, 'refs/tags/v')
    needs: [test, build]
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Grant execute permission
        run: chmod +x gradlew

      - name: Build Release APK
        run: ./gradlew assembleRelease --no-daemon
        env:
          KEYSTORE_PATH: ${{ runner.temp }}/habitsehat.keystore
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}

      - name: Build AAB (Android App Bundle)
        run: ./gradlew bundleRelease --no-daemon
        env:
          KEYSTORE_PATH: ${{ runner.temp }}/habitsehat.keystore
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}

      - name: Extract version
        id: version
        run: echo "version=${GITHUB_REF#refs/tags/}" >> $GITHUB_OUTPUT

      - name: Create Release Notes
        run: |
          echo "## HabitSehat ${{ steps.version.outputs.version }}" > release.md
          echo "" >> release.md
          echo "### What's New" >> release.md
          echo "- Improved UI/UX with Material Design 3" >> release.md
          echo "- Performance optimizations" >> release.md
          echo "- Bug fixes and stability improvements" >> release.md

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v2
        with:
          files: |
            app/build/outputs/apk/release/app-release.apk
            app/build/outputs/bundle/release/app-release.aab
          body_path: release.md
        env:
          GITHUB_TOKEN: ${{ secrets.GH_PAT }}

      - name: Upload to Play Store (Internal Testing)
        run: |
          echo "Configure Play Store upload with Gradle Play Publisher"
          ./gradlew bundleRelease publishBundle --no-daemon
        env:
          KEYSTORE_PATH: ${{ runner.temp }}/habitsehat.keystore
          KEYSTORE_PASSWORD: ${{ secrets.KEYSTORE_PASSWORD }}
          KEY_ALIAS: ${{ secrets.KEY_ALIAS }}
          KEY_PASSWORD: ${{ secrets.KEY_PASSWORD }}

  quality-gate:
    if: always()
    needs: [test, build]
    runs-on: ubuntu-latest
    steps:
      - name: Check Build Status
        run: |
          if [ "${{ needs.test.result }}" == "failure" ] || [ "${{ needs.build.result }}" == "failure" ]; then
            echo "❌ Build failed quality checks"
            exit 1
          fi
          echo "✅ All quality checks passed"
```

---

## 📊 PHASE 9: STORE OPTIMIZATION

### 9.1 App Store Listing Checklist

- ✅ **Icon**: 512×512 PNG (existing)
- ✅ **Feature Graphic**: 1024×500 PNG (existing)
- ✅ **Screenshots**: 6× 1080×1920 (need to generate)
- ✅ **Short Description**: 80 chars max ✓
- ✅ **Full Description**: Compelling, SEO-optimized ✓
- ✅ **Category**: Health & Fitness / Productivity
- ✅ **Content Rating**: Fill out questionnaire
- ✅ **Privacy Policy**: Published ✓
- ✅ **Permissions**: Minimal & justified
- ✅ **Supported Devices**: Phones, Tablets
- ✅ **Languages**: Indonesian, English (add)

### 9.2 App Store Screenshots Generator

```python
# generate_store_screenshots.py - Automated screenshot generation

from PIL import Image, ImageDraw, ImageFont
import os

screenshots = [
    {
        "title": "Habit Tracker",
        "subtitle": "Track daily habits with ease",
        "color": "#2D8A61",
        "image": "screenshot-1-home.png"
    },
    {
        "title": "Water Tracker",
        "subtitle": "Stay hydrated throughout the day",
        "color": "#0066CC",
        "image": "screenshot-2-water.png"
    },
    {
        "title": "Heatmap Calendar",
        "subtitle": "Visualize your consistency",
        "color": "#2D8A61",
        "image": "screenshot-3-heatmap.png"
    },
    {
        "title": "Premium Themes",
        "subtitle": "20+ beautiful themes to choose",
        "color": "#FF9500",
        "image": "screenshot-4-themes.png"
    },
    {
        "title": "HabitStop",
        "subtitle": "Break bad habits, save money",
        "color": "#D00000",
        "image": "screenshot-5-habitstop.png"
    },
    {
        "title": "Pomodoro Timer",
        "subtitle": "Focus sessions with white noise",
        "color": "#8B6BB1",
        "image": "screenshot-6-pomodoro.png"
    }
]

for i, screen in enumerate(screenshots):
    # Generate 1080x1920 screenshot
    img = Image.new('RGB', (1080, 1920), color='white')
    draw = ImageDraw.Draw(img)
    
    # Add gradient background (simplified)
    title_y = 200
    draw.text((54, title_y), screen["title"], fill=screen["color"], font=ImageFont.load_default())
    draw.text((54, title_y + 100), screen["subtitle"], fill="#666666", font=ImageFont.load_default())
    
    # Save
    output_path = f"play-store-assets/{screen['image']}"
    img.save(output_path)
    print(f"✅ Generated {output_path}")
```

---

## ✅ QUALITY CHECKLIST — BEFORE RELEASE

### Code Quality
- [ ] All classes have proper documentation
- [ ] No compiler warnings
- [ ] ProGuard successfully minifies release build
- [ ] Unit tests coverage > 70%
- [ ] No hardcoded strings (all in strings.xml)
- [ ] Proper error handling on all network/DB operations
- [ ] No memory leaks (checked with Profiler)

### UI/UX
- [ ] Material Design 3 colors applied consistently
- [ ] All touch targets >= 48dp
- [ ] Text contrast >= WCAG AA (4.5:1)
- [ ] All screens support dark mode
- [ ] No flickering or jank on navigation
- [ ] Loading states visible on all async operations
- [ ] Error states display helpful messages

### Performance
- [ ] APK size < 5MB
- [ ] App startup < 2 seconds
- [ ] No ANR (Application Not Responding) issues
- [ ] Memory usage < 100MB (normal usage)
- [ ] Battery drain < 2% per hour (idle)

### Functionality
- [ ] All habits CRUD operations work
- [ ] Water tracker logs correctly
- [ ] Streak calculation accurate
- [ ] Heatmap renders properly
- [ ] Settings persist after app restart
- [ ] Notifications trigger on time
- [ ] Widgets update correctly

### Security & Privacy
- [ ] No sensitive data in logs
- [ ] No hardcoded API keys/credentials
- [ ] Permissions requested at runtime
- [ ] Privacy policy accurate and accessible
- [ ] No analytics tracking (privacy-first)
- [ ] Data deletion works on app uninstall

### Store Readiness
- [ ] App icon > 192px and proper formatting
- [ ] Feature graphic present (1024x500)
- [ ] 6 screenshots prepared
- [ ] Description compelling and SEO-optimized
- [ ] Changelog updated
- [ ] Version code > previous version
- [ ] Target SDK = 35 (Android 15)
- [ ] Min SDK = 26 (Android 8.0)

### Localization
- [ ] All strings translated to Indonesian
- [ ] English strings added as fallback
- [ ] RTL support tested (if needed)
- [ ] Date/time formats localized

---

## 📈 POST-RELEASE MONITORING

### 7-Day Launch Plan

**Day 1**: Release to Internal Testing track
- Validate install
- Check crash reports
- Test all core features

**Day 2-3**: Beta testing (invite 25 testers)
- Collect feedback
- Monitor crash rates
- Fix critical bugs

**Day 4-6**: Production release
- Release to Open testing first (1-2 days)
- Monitor ratings & reviews
- Respond to feedback
- Prepare patch if needed

**Day 7**: Full public release
- Expand to all users
- Monitor analytics
- Plan next update

### Key Metrics to Track

1. **Installation Rate**: Target > 100/day
2. **Crash Rate**: Target < 0.5%
3. **ANR Rate**: Target = 0%
4. **App Rating**: Target = 4.5+ stars
5. **Retention**: Day 1 = 40%, Day 7 = 20%, Day 30 = 10%
6. **Uninstall Rate**: Target < 5%

---

## 🚀 SUMMARY & PRIORITIES

### CRITICAL (Sprint 1 — 1 week)
1. ✅ Refactor package structure (data, domain, ui layers)
2. ✅ Implement complete Material3 theme + color system
3. ✅ Add ViewModel pattern to all screens
4. ✅ Implement error + empty + loading states UI
5. ✅ Expand strings.xml with all UI text
6. ✅ Add accessibility (a11y) basics

### HIGH (Sprint 2 — 1 week)
7. Implement all CRUD use cases (domain layer)
8. Write unit tests for core logic (> 70% coverage)
9. Optimize APK size (ProGuard, R8)
10. Add proper logging (no sensitive data)
11. Performance profiling & optimization
12. Update build pipeline with quality gates

### MEDIUM (Sprint 3 — 3-5 days)
13. Generate store screenshots
14. Polish UI animations
15. Dark mode testing
16. Localization review (Indonesian/English)
17. Security audit
18. Privacy policy verification

### NICE TO HAVE (Post-launch)
19. Widget implementation
20. Pomodoro timer
21. Premium features infrastructure
22. Cloud backup feature

---

## 📞 NEXT STEPS

1. **Create feature branches** per phase
2. **Assign team members** to each component
3. **Setup CI/CD pipeline** with GitHub Actions
4. **Schedule code reviews** (2 reviewers minimum)
5. **Plan testing strategy** (manual + automated)
6. **Prepare Play Store account** (screenshots, description ready)
7. **Setup crash reporting** (Firebase Crashlytics)
8. **Plan launch marketing** (social media, beta testers)

---

**Document Status**: Final Review Ready  
**Last Updated**: 2026-07-18  
**Senior Developer**: Ready for implementation
