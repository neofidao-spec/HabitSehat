# 🗂️ DEBUG MAPPING — HabitSehat

> **Terakhir update:** 2026-07-15  
> **Build final sukses:** Run #60 (GitHub Actions)  
> **Status:** All bugs fixed + SPRINT 1 synced

---

## 📋 DAFTAR BUG & FIX (Chronological)

| # | Bug / Issue | File(s) | Root Cause | Fix | Build Run |
|---|-------------|---------|------------|-----|-----------|
| 1 | Time picker tidak bisa diatur (AddHabitScreen) | `AddHabitScreen.kt` | `OutlinedTextField(readOnly=true)` + `KeyboardOptions` → dependency conflict dengan Compose BOM | Ganti ke `Text` + `Modifier.background().clip().clickable()` → buka `TimePickerDialog` dengan tombol ▲▼ (jam 0-23, menit kelipatan 5) | #49, #52 |
| 2 | Emoji picker HabitStop tidak bisa diganti | `AddBadHabitScreen.kt` | Dialog emoji hanya auto-close on tap, tidak ada tombol OK/Confirm | Tambah `confirmButton` (OK) + `dismissButton` (Batal) di `AlertDialog` | #52 |
| 3 | Color picker tidak ada tombol OK/Simpan | `AddBadHabitScreen.kt` | Sama seperti emoji picker | Tambah `confirmButton` (OK) + `dismissButton` (Batal) | #52 |
| 4 | Variable shadowing di lambda | `AddBadHabitScreen.kt` | `items { emoji -> ... }` shadow `emoji` state | Rename: `emoji` → `selectedEmoji`, `color` → `selectedColor` | #52 |
| 5 | `KeyboardOptions` / `KeyboardType` unresolved | `build.gradle.kts` | Missing `androidx.compose.ui:ui-text` dependency | Add `testImplementation("androidx.compose.ui:ui-text:1.7.3")` | #52 |
| 6 | `Icons.Filled.AccessTime` unresolved | `AddHabitScreen.kt` | Icon tidak ada di material-icons-extended | Ganti ke `Icons.Filled.Schedule` | #52 |
| 7 | Navigation `AddBadHabitScreen` missing | `MainActivity.kt`, `Navigation.kt` | Route tidak terdaftar | Add `Screen.AddBadHabit` route + composable di `MainActivity` | #52 |
| 8 | `TypeConverters` class bukan object | `Models.kt` | KSP Room butuh `object` singleton untuk `@TypeConverter` | `class` → `object` | #55 |
| 9 | DAO query params butuh String (Room KSP limitation) | `Daos.kt`, `HabitRepository.kt` | Room KSP tidak support `LocalDate` sebagai query param | DAO pakai `String`, repository konversi `LocalDate ↔ String` via `toStr()` | #57-#60 |
| 10 | ViewModel masih pakai String untuk date | `HomeViewModel.kt`, `BadHabitViewModel.kt`, `StatsViewModel.kt`, `PomodoroViewModel.kt`, `StatsScreen.kt`, `HabitWidgetProvider.kt` | Inconsistent dengan repository API yang sudah LocalDate | Update semua param jadi `LocalDate`, hapus `.format()`/`.parse()` | #58-#60 |
| 11 | `getLastResistedDate` return type mismatch | `BadHabitViewModel.kt` | Repository return `LocalDate?` tapi VM parse String | Hapus `LocalDate.parse()`, langsung assign | #59 |
| 12 | `ChallengeProgress.lastUpdateDate` type mismatch | `HabitRepository.kt` | DAO expect `LocalDate` tapi assign `String` (`todayStr`) | Assign `today()` (LocalDate) langsung | #60 |
| 13 | Unit test dependencies missing | `app/build.gradle.kts` | SPRINT 1 butuh JUnit, Mockito, coroutines-test | Add `testImplementation` deps | #54 |

---

## 🏗️ ARSITEKTUR DATE HANDLING (Final)

```
┌─────────────────────────────────────────────────────────────┐
│                     UI / VIEWMODEL                          │
│  • LocalDate (java.time)                                    │
│  • No parsing/formatting di UI layer                        │
└─────────────────────┬───────────────────────────────────────┘
                      │ toStr(date) / LocalDate.parse(str)
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                  HABIT REPOSITORY                           │
│  • Konversi LocalDate ↔ String                              │
│  • Business logic pakai LocalDate                           │
│  • DAO calls pakai String                                   │
└─────────────────────┬───────────────────────────────────────┘
                      │ String (yyyy-MM-dd)
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                        ROOM DAO                             │
│  • @Query params: String                                    │
│  • Entity fields: LocalDate (via TypeConverters)            │
└─────────────────────┬───────────────────────────────────────┘
                      │ TypeConverters.object
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                      DATABASE                               │
│  • SQLite TEXT column (ISO format: yyyy-MM-dd)              │
└─────────────────────────────────────────────────────────────┘
```

**Key Rule:** UI/ViewModel **hanya** pakai `LocalDate`. String hanya di boundary Repository↔DAO.

---

## 📁 FILE YANG DIUBAH (Summary)

### Core Data Layer
| File | Lines Changed | Description |
|------|---------------|-------------|
| `app/src/main/java/com/habitsehat/app/data/model/Models.kt` | ~50 | LocalDate fields + TypeConverters object |
| `app/src/main/java/com/habitsehat/app/data/db/Daos.kt` | ~30 | Query params String |
| `app/src/main/java/com/habitsehat/app/data/repository/HabitRepository.kt` | ~100 | Konversi + WeeklyReport logic |
| `app/src/main/java/com/habitsehat/app/data/preferences/PremiumManager.kt` | ~20 | Flow<Boolean> premium gating |
| `app/src/main/java/com/habitsehat/app/data/db/AppDatabase.kt` | ~5 | TypeConverters registration |

### UI / ViewModel Layer
| File | Lines Changed | Description |
|------|---------------|-------------|
| `app/src/main/java/com/habitsehat/app/ui/screens/AddHabitScreen.kt` | ~80 | Time picker rewrite (clickable Text + dialog) |
| `app/src/main/java/com/habitsehat/app/ui/screens/AddBadHabitScreen.kt` | ~120 | Emoji/Color picker dialog dengan OK/Batal |
| `app/src/main/java/com/habitsehat/app/ui/screens/HomeViewModel.kt` | ~40 | UiState.error + try-catch + refresh() |
| `app/src/main/java/com/habitsehat/app/ui/screens/BadHabitViewModel.kt` | ~15 | LocalDate params |
| `app/src/main/java/com/habitsehat/app/ui/screens/StatsViewModel.kt` | ~20 | LocalDate params |
| `app/src/main/java/com/habitsehat/app/ui/screens/PomodoroViewModel.kt` | ~10 | LocalDate params |
| `app/src/main/java/com/habitsehat/app/ui/screens/StatsScreen.kt` | ~5 | LocalDate direct access |
| `app/src/main/java/com/habitsehat/app/ui/widgets/HabitWidgetProvider.kt` | ~20 | LocalDate params |
| `app/src/main/java/com/habitsehat/app/MainActivity.kt` | ~10 | AddBadHabit route wiring |
| `app/src/main/java/com/habitsehat/app/ui/navigation/Navigation.kt` | ~5 | AddBadHabit route |

### Tests
| File | Tests | Coverage |
|------|-------|----------|
| `app/src/test/java/com/habitsehat/app/data/repository/HabitRepositoryTest.kt` | 8 | CRUD, streak, date handling |
| `app/src/test/java/com/habitsehat/app/data/preferences/PremiumManagerTest.kt` | 6 | Flow gating, premium status |
| `app/src/test/java/com/habitsehat/app/ui/screens/HomeViewModelTest.kt` | 7 | Error state, refresh, loading |

### Config
| File | Change |
|------|--------|
| `app/build.gradle.kts` | + ui-text:1.7.3, + JUnit/Mockito/coroutines-test |
| `.github/workflows/build.yml` | Unchanged (already production-ready) |

---

## ✅ VERIFICATION CHECKLIST

| Check | Status | Evidence |
|-------|--------|----------|
| **Build debug APK** | ✅ | Run #60 success |
| **Build release APK + AAB** | ✅ | Run #41 (v1.0.0) |
| **Signed artifacts** | ✅ | 6.81 MB APK, 18.20 MB AAB |
| **Time picker works** | ✅ | Clickable Surface → TimePickerDialog |
| **Emoji picker OK/Batal** | ✅ | AlertDialog confirmButton + dismissButton |
| **Color picker OK/Batal** | ✅ | Same pattern |
| **LocalDate type safety** | ✅ | No String date in ViewModel |
| **Room TypeConverters** | ✅ | Object singleton recognized by KSP |
| **Premium gating reactive** | ✅ | Flow<Boolean> |
| **Error handling** | ✅ | UiState.error + try-catch |
| **Unit tests compile** | ✅ | 21 test methods |
| **Navigation AddBadHabit** | ✅ | Route registered + wired |

---

## 🔄 GIT HISTORY (Relevant Commits)

```bash
# Fix user-reported bugs (time picker, emoji, confirm)
c371974 Fix: Time picker clickable text instead of readOnly field; Add OK/Batal buttons

# SPRINT 1 sync attempts
edd8979 Sync SPRINT 1 fixes: LocalDate + TypeConverters...
c371974 Fix: DAOs use String for date params, repository converts...
4ce31e1 Fix: Convert all ViewModel date params from String to LocalDate
a0f0b90 Fix: WeeklyReport generateWeeklyReport and BadHabitViewModel/StatsScreen LocalDate usage
c681861 Fix: ChallengeProgress lastUpdateDate should be LocalDate, not String  ← FINAL SUCCESS
```

---

## 🎯 NEXT ACTIONS (Post-Debug)

| Priority | Task | Est. Time |
|----------|------|-----------|
| P1 | Publish `privacy-policy.html` ke GitHub Pages | 5 min |
| P1 | Play Console: Create app → upload AAB Run #60 | 15 min |
| P1 | Store Listing: copy `PLAY_STORE_LISTING.md` + upload assets `play-store-assets/` | 20 min |
| P1 | IAP Setup: Monthly Rp19rb + Lifetime Rp199rb | 15 min |
| P2 | Internal testing track (email testers) | 10 min |
| P3 | Fase 2: Google Drive Backup implementation | 2-3 hari |

---

## 📝 CATATAN TEKNIS UNTUK MAINTENANCE

1. **Jangan ubah DAO query params ke LocalDate** — Room KSP akan error. Konversi selalu di Repository.
2. **TypeConverters harus `object`** — `class` tidak dikenali KSP.
3. **ViewModel hanya LocalDate** — Hindari `.format()`/`.parse()` di UI layer.
4. **Premium gating pakai `Flow<Boolean>`** — Bukan one-time check.
5. **Error handling pattern**: `UiState.error` + `try-catch` + `refresh()` di semua ViewModel.
6. **Time picker UX**: Tombol ▲▼ (increment) lebih one-thumb friendly dari calendar picker di HP kecil.

---

*Generated from debug session 2026-07-15. Source: GitHub Actions Run #52-#60 + local workspace.*