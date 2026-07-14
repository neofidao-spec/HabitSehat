# 🗺️ PETA KERJA — HabitSehat

> **Proyek:** Aplikasi Android utility habit tracker + water tracker + HabitStop  
> **Status:** Development aktif — Build sukses (Run #23)  
> **Stack:** Kotlin + Jetpack Compose + Room + Material 3 + GitHub Actions  
> **Repo:** `github.com/neofidao-spec/HabitSehat`

---

## 📊 STATUS SAAT INI

| Komponen | Status | Catatan |
|----------|--------|---------|
| **Home screen** | ✅ Selesai | Streak circle, habit list, water card |
| **AddHabit screen** | ✅ Selesai | Form + save to Room |
| **Stats screen** | ✅ Selesai | Summary, chart, heatmap |
| **Theme system** | ✅ Selesai | 20 tema (5 gratis, 15 premium) |
| **Premium gating** | ✅ Selesai | PremiumManager + lock UI |
| **Premium screen** | ✅ Selesai | Comparison table, pricing |
| **More screen** | ✅ Selesai | Settings menu |
| **Settings screen** | ✅ Selesai | Water goal, about, delete data |
| **HabitStop** | ✅ Selesai | Bad habit tracker, money saved, streak |
| **Widget** | ✅ Selesai | Mini (2×2), Medium (4×2), Large (4×4), WorkManager update, premium gating |
| **Pomodoro Timer** | ✅ Selesai | Circular countdown, 25/50/90 menit, integrasi habit, premium gating |
| **Weekly Report** | ✅ Selesai | Auto-generate dari Room, konsistensi %, habit breakdown, insight |
| **Challenges** | ✅ Selesai | 7/21/30 hari challenge, progress tracker, badge system, auto-detect |
| **Release build** | ❌ Belum | Keystore, signing, ProGuard |
| **Play Store** | ❌ Belum | Listing, assets, screenshots |

---

## 🎯 FASE-FASE PENGERJAAN

### FASE 1 — Finalisasi Fitur Inti (SEKARANG)
*Target: Semua fitur yang dijanjikan di Premium sudah ada*

#### 1.1 🔧 Fix Bug / Technical Debt
- [ ] `Screen.AddBadHabit` — hapus referensi di MainActivity (tidak dipakai, navigasi ke AddHabit biasa saja)
- [ ] `SettingsManager.saveWaterGoal` — implementasi penyimpanan water goal ke DataStore
- [ ] Delete all data — implementasi clear database di repository
- [ ] Cek semua TODO di kode, selesaikan yang tersisa

#### 1.2 📱 Pomodoro Timer
- [ ] Buat `PomodoroScreen.kt` — timer 25/50/90 menit
- [ ] Buat `PomodoroViewModel.kt` — state management timer (pause, resume, reset)
- [ ] Tambah route `Screen.Pomodoro` + navigasi
- [ ] Integrasi dengan habit: setelah timer selesai, centang habit fokus
- [ ] Statistik fokus: total jam fokus minggu ini (simpan di Room)
- [ ] Premium gate: mode 90 menit + white noise untuk premium
- [ ] UI: Circular countdown, controls (start/pause/reset)

#### 1.3 📱 Widget Interaktif
- [ ] Buat `HabitWidget.kt` — AppWidgetProvider
- [ ] Layout: Mini (2×2) — streak + progress circle
- [ ] Medium (4×2) — 3 habit teratas + water progress
- [ ] Large (4×4) — full habit list + water + streak
- [ ] Interaktif (Android 15+): tap widget → log habit/minum langsung
- [ ] Update mekanisme: WorkManager periodic update (30 menit)
- [ ] Premium gate: medium + large + interaktif untuk premium

#### 1.4 📱 Pomodoro Timer (lanjutan)
- [ ] White noise player (resource audio raw/)
- [ ] Focus session history di Room (`pomodoro_sessions` table)
- [ ] Weekly focus stats di laporan

---

### FASE 2 — Fitur Pelengkap Premium

#### 2.1 📊 Weekly Smart Report
- [ ] Buat `ReportGenerator.kt` — logic generate ringkasan mingguan dari data Room
- [ ] Format output: konsistensi %, air rata-rata, hari terkuat/lemah, insight
- [ ] Notifikasi tiap Minggu malam (WorkManager)
- [ ] Share sebagai gambar (screenshot composable → Bitmap)
- [ ] Premium gate: full report + share image

#### 2.2 🏆 Streak Challenges
- [ ] Buat model `Challenge` + `ChallengeProgress` di Room
- [ ] Challenges: 7 hari, 21 hari, 30 hari
- [ ] Badge system: achievement yang didapat
- [ ] UI: challenge list, progress tracker, completion animation
- [ ] Premium gate: challenges + badges

#### 2.3 ☁️ Google Drive Backup
- [ ] Integrasi Google Drive API (atau simpan ke file lokal dulu)
- [ ] Auto backup tiap malam (WorkManager)
- [ ] Manual backup + restore
- [ ] Encrypt data sebelum upload
- [ ] Premium gate

---

### FASE 3 — Release Build

#### 3.1 🔐 Keystore & Signing
- [ ] Generate keystore (`habitsehat.keystore`) — di VPS atau lokal
- [ ] Simpan credential di GitHub Secrets:
  - `KEYSTORE_BASE64` — keystore file yang di-base64
  - `KEYSTORE_PASSWORD`
  - `KEY_ALIAS`
  - `KEY_PASSWORD`
- [ ] Update `build.gradle.kts` — signing config untuk release
- [ ] Update workflow `build.yml` — decrypt keystore + sign APK

#### 3.2 📦 Release Optimization
- [ ] Review ProGuard rules (`proguard-rules.pro`)
- [ ] Optimize R8: obfuscation, minification, resource shrinking
- [ ] Target APK size: < 5MB
- [ ] Split APK per ABI (armeabi-v7a, arm64-v8a, x86_64) untuk ukuran lebih kecil
- [ ] Bundle release: Android App Bundle (AAB) untuk Play Store

#### 3.3 🧪 Testing
- [ ] Test di emulator / device fisik (Xiaomi Helio G99)
- [ ] Regression test: semua fitur berfungsi
- [ ] Edge cases: first run, data kosong, rotate, dark mode switch
- [ ] Performance test: scroll smooth, database query cepat

---

### FASE 4 — Play Store Assets

#### 4.1 🎨 Desain Grafis
- [ ] Icon aplikasi (512×512, adaptive icon)
- [ ] Feature graphic (1024×500)
- [ ] Screenshots 3-8 (min 1280×720):
  - Home screen
  - Habit tracking
  - Water tracker
  - Heatmap calendar
  - Theme studio
  - HabitStop
- [ ] Promo video (30 detik) — opsional

#### 4.2 📝 Listing Copy (Bahasa Indonesia)
- [ ] **Judul:** HabitSehat — Kebiasaan Sehat Harian
- [ ] **Short description:** (80 char)
  > Lacak kebiasaan & minum air. Tema keren, widget, heatmap.
- [ ] **Full description:** (min 200 char)
  > HabitSehat membantu kamu membangun kebiasaan sehat dengan tampilan premium.
  > 
  > FITUR UTAMA:
  > ✅ Habit Tracker — catat dan lacak 5+ kebiasaan harian
  > 💧 Water Tracker — pantau asupan air dengan target harian
  > 🔥 Streak & Heatmap — lihat konsistensi seperti GitHub
  > 🚬 HabitStop — hentikan kebiasaan buruk + hitung uang tersimpan
  > 🎨 20+ Tema Premium — Pastel, Dark, Cyberpunk, dan banyak lagi
  > 📊 Weekly Report — ringkasan mingguan otomatis
  > 🍅 Pomodoro Timer — fokus maksimal
  > 🏆 Challenges — 7/21/30 hari bangun kebiasaan
  > 📱 Widget — lihat progress dari home screen
  > 
  > Semua data disimpan LOKAL — 100% offline, tanpa perlu akun.
  - **Category:** Health & Fitness / Productivity
  - **Tags:** habit tracker, water drink reminder, daily routine, pomodoro, habit stop, healthy lifestyle

#### 4.3 🌐 Privacy Policy
- [ ] Buat halaman privacy policy (bisa pakai GitHub Pages)
- [ ] Link di listing Play Store
- [ ] Isi: data lokal, tidak ada koleksi data, tidak ada iklan

---

### FASE 5 — Beta Testing

#### 5.1 Internal Testing
- [ ] Upload AAB ke Play Console → Internal Testing
- [ ] Test dengan akun sendiri (Fani)
- [ ] Fix bug yang muncul

#### 5.2 Closed Testing
- [ ] Undang 5-10 tester (teman/social media)
- [ ] Kumpulkan feedback:
  - Bug report
  - UI/UX feedback
  - Fitur yang kurang
- [ ] Iterasi berdasarkan feedback

#### 5.3 Open Testing
- [ ] Buka ke publik (opsional, bisa langsung production)
- [ ] Pantau crash dari Google Play Console

---

### FASE 6 — Production Launch

#### 6.1 🚀 Rilis ke Production
- [ ] Set harga: Rp19rb (one-time purchase)
- [ ] Set in-app product: Premium Rp19rb/bln + Rp199rb lifetime
- [ ] Submit untuk review Play Store
- [ ] Pantau status review

#### 6.2 📣 Promosi
- [ ] Post di Twitter/X: @neofidao-spec
- [ ] Share di grup Telegram/WhatsApp
- [ ] Minta review dari pengguna awal
- [ ] Optimasi ASO (App Store Optimization) berdasarkan data awal

---

### FASE 7 — Post-Launch Iterasi

- [ ] Fitur baru berdasarkan feedback user
- [ ] AI-powered insight (local LLM via ML Kit / TensorFlow Lite)
- [ ] Export data ke CSV/JSON
- [ ] Smart notification dengan variasi pesan (30+ template)
- [ ] Social features (shared challenges, leaderboard) — opsional
- [ ] Wear OS companion

---

## 📋 PRIORITAS MINGGU INI

| Prioritas | Task | Estimasi |
|-----------|------|----------|
| 🔴 P1 | Fix `Screen.AddBadHabit` + SettingsManager water goal | 30 menit |
| 🔴 P1 | Pomodoro Timer screen | 2-3 jam |
| 🟡 P2 | Widget dasar (mini) | 3-4 jam |
| 🟡 P2 | Weekly Report generator | 2 jam |
| 🟢 P3 | Challenges | 2 jam |
| 🟢 P3 | Widget medium + large | 2 jam |

---

## 🚧 RISK MANAGEMENT

| Risiko | Dampak | Mitigasi |
|--------|--------|----------|
| **OOM di GitHub Actions** | Build gagal | Heap 2048m sudah di-set, monitor usage |
| **Keystore hilang** | APK lama bisa diupdate | Simpan backup di multiple tempat (VPS, GitHub Secrets, lokal) |
| **ProGuard over-remove** | Crash di release | Test release build sebelum submit |
| **APK > 5MB** | Enggan download | Split ABI, review dependency |
| **Play Store reject** | Delay launch | Ikuti policy, hindari permission sensitif |
| **Low conversion rate** | Revenue rendah | A/B testing harga, promo terbatas |

---

## 🏗️ FILE STRUCTURE (REFERENCE)

```
HabitSehat/
├── .github/workflows/build.yml      # CI/CD
├── app/
│   ├── src/main/
│   │   ├── java/com/habitsehat/app/
│   │   │   ├── MainActivity.kt       # Entry point + navigasi
│   │   │   ├── data/
│   │   │   │   ├── db/
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   └── Daos.kt          # Semua DAO (HabitLogDao, BadHabitDao, dll)
│   │   │   │   ├── model/
│   │   │   │   │   ├── Models.kt        # Habit, HabitLog, WaterLog, BadHabit, BadHabitLog
│   │   │   │   │   └── AppTheme.kt      # 20 tema
│   │   │   │   ├── preferences/
│   │   │   │   │   ├── SettingsManager.kt
│   │   │   │   │   └── PremiumManager.kt
│   │   │   │   └── repository/
│   │   │   │       └── HabitRepository.kt
│   │   │   └── ui/
│   │   │       ├── navigation/Navigation.kt
│   │   │       ├── theme/
│   │   │       │   ├── Theme.kt
│   │   │       │   └── Color.kt
│   │   │       ├── components/
│   │   │       │   ├── Components.kt
│   │   │       │   └── HeatmapCalendar.kt
│   │   │       └── screens/
│   │   │           ├── HomeScreen.kt
│   │   │           ├── HomeViewModel.kt
│   │   │           ├── AddHabitScreen.kt
│   │   │           ├── StatsScreen.kt
│   │   │           ├── StatsViewModel.kt
│   │   │           ├── ThemeScreen.kt
│   │   │           ├── PremiumScreen.kt
│   │   │           ├── MoreScreen.kt
│   │   │           ├── SettingsScreen.kt
│   │   │           ├── HabitStopScreen.kt
│   │   │           └── BadHabitViewModel.kt
│   │   ├── res/...
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts                   # Root project
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── proguard-rules.pro
└── README.md
```

---

## 📈 PROGRESS TRACKER

| Fase | % Progress | Status |
|------|-----------|--------|
| **Fase 1:** Finalisasi fitur inti | 60% | 🟡 Sedang dikerjakan |
| **Fase 2:** Fitur pelengkap premium | 0% | ⚪ Belum mulai |
| **Fase 3:** Release build | 20% | 🟡 Build GitHub Actions aktif, signing belum |
| **Fase 4:** Play Store assets | 0% | ⚪ Belum mulai |
| **Fase 5:** Beta testing | 0% | ⚪ Belum mulai |
| **Fase 6:** Production launch | 0% | ⚪ Belum mulai |
| **Fase 7:** Post-launch | 0% | ⚪ Belum mulai |

---

## 🔗 REFERENSI CEPAT

| Resource | Path |
|----------|------|
| Konsep aplikasi | `~/workspace/KONSEP_APLIKASI.md` |
| Build status | `github.com/neofidao-spec/HabitSehat/actions` |
| APK terbaru | Artifact Run #23 (debug) |
| Repo | `github.com/neofidao-spec/HabitSehat` (public) |
| Remote VPS | 82.153.226.188 (Debian 11, 1GB RAM) — **tidak dipakai build** |

---

> **Mulai:** Setiap sesi coding, lihat PETA_KERJA.md → kerjakan item Prioritas P1 → P2 → P3  
> **Commit:** `git add -A && git -c user.name="Fani Hernanda" -c user.email="nandaori04@gmail.com" commit -m "Pesan deskriptif" && git push`  
> **Verify:** Tunggu GitHub Actions selesai, cek hasilnya
