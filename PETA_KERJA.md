# 🗺️ PETA KERJA — HabitSehat

> **Proyek:** Aplikasi Android utility habit tracker + water tracker + HabitStop  
> **Status:** Build #52 ✅ sukses — Semua fitur inti selesai, Play Store assets 100%  
> **Stack:** Kotlin + Jetpack Compose + Room + Material 3 + GitHub Actions  
> **Repo:** `github.com/neofidao-spec/HabitSehat`  
> **Last updated:** 2026-07-15

---

## 📊 STATUS SAAT INI

| Komponen | Status | Catatan |
|----------|--------|---------|
| **Home screen** | ✅ Selesai | Streak circle, habit list, water card |
| **AddHabit screen** | ✅ Selesai | Form + save to Room + time picker (▲▼ buttons) |
| **Stats screen** | ✅ Selesai | Summary, chart, heatmap |
| **Theme system** | ✅ Selesai | 20 tema (5 gratis, 15 premium) + dynamic color |
| **Premium gating** | ✅ Selesai | PremiumManager + lock UI |
| **Premium screen** | ✅ Selesai | Comparison table, pricing Rp19rb/bln + Rp199rb lifetime |
| **More screen** | ✅ Selesai | Settings menu, theme, premium, pomodoro, report, challenges |
| **Settings screen** | ✅ Selesai | Water goal, dark mode, theme, delete data, about |
| **HabitStop** | ✅ Selesai | Bad habit tracker, money saved, health timeline, streak, emoji/color picker |
| **Widget** | ✅ Selesai | Mini (2×2), Medium (4×2), Large (4×4), WorkManager update, premium gating |
| **Pomodoro Timer** | ✅ Selesai | Circular countdown, 25/50/90 menit, integrasi habit, white noise, premium gating |
| **Weekly Report** | ✅ Selesai | Auto-generate dari Room, konsistensi %, habit breakdown, insight |
| **Challenges** | ✅ Selesai | 7/21/30 hari challenge, progress tracker, badge system, auto-detect |
| **Release build** | ✅ Selesai | Signing config, ProGuard, keystore workflow, AAB + APK signed, release v1.0.0 published |
| **Play Store assets** | ✅ Selesai | Icon adaptive, feature graphic, 6 screenshots, listing copy, privacy policy |

---

## 🎯 FASE-FASE PENGERJAAN

### FASE 1 — Finalisasi Fitur Inti ✅ **100% SELESAI**
*Semua fitur yang dijanjikan di Premium sudah ada*

| Task | Status |
|------|--------|
| AddHabit time picker fix (▲▼ buttons, no keyboard dep) | ✅ |
| HabitStop AddBadHabitScreen (emoji picker + OK/Batal, color picker + OK/Batal) | ✅ |
| SettingsManager water goal wiring | ✅ |
| Delete all data (clearAllData) | ✅ |
| Pomodoro Timer (25/50/90, white noise, habit link, stats) | ✅ |
| Widget (3 size, interactive, WorkManager, premium gate) | ✅ |
| Weekly Report (auto-generate, share image, premium gate) | ✅ |
| Challenges (7/21/30 hari, badge, auto-detect, premium gate) | ✅ |
| Theme Studio (20 tema, dynamic Material You) | ✅ |

---

### FASE 2 — Fitur Pelengkap Premium 🟡 **10%**

| Task | Status |
|------|--------|
| Google Drive Backup (auto backup, manual backup/restore, encrypt, premium gate) | ⏳ Belum mulai |

---

### FASE 3 — Release Build ✅ **100% SELESAI**

| Task | Status |
|------|--------|
| Keystore generation di GitHub Actions runner | ✅ |
| Signing config release (build.gradle.kts) | ✅ |
| ProGuard + R8 optimization (minify, shrink resources) | ✅ |
| Signed Release APK (6.8 MB) + AAB (18 MB) | ✅ |
| GitHub Actions workflow: push tag `v*` → auto build + sign + release | ✅ |
| GitHub Release v1.0.0 published | ✅ |

---

### FASE 4 — Play Store Assets ✅ **100% SELESAI**

| Asset | File | Spec |
|-------|------|------|
| App Icon (adaptive) | `ic_launcher*.png`, `ic_launcher_foreground.png` | 5 densities, round + foreground |
| Feature Graphic | `feature-graphic-1024x500.png` | 1024×500 |
| Screenshots (6) | `screenshot-1` to `6` | 1280×720 phone frames |
| Full Listing | `PLAY_STORE_LISTING.md` | Title, short/long desc, tags |
| Privacy Policy | `privacy-policy.html` | Hosted via GitHub Pages |

**Files ready di repo:**
```
HabitSehat/
├── play-store-assets/           # All upload-ready assets
│   ├── icon-512.png
│   ├── feature-graphic-1024x500.png
│   └── screenshot-1...6.png
├── PLAY_STORE_LISTING.md        # Copy-paste ke Play Console
├── privacy-policy.html          # Host di GitHub Pages
├── app/build/outputs/bundle/release/app-release.aab  (dari CI artifact)
└── .github/workflows/build.yml  # CI/CD production-ready
```

---

### FASE 5 — Beta Testing ⚪ **0%**

| Task | Status |
|------|--------|
| Internal Testing (upload AAB ke Play Console) | ⏳ |
| Closed Testing (5-10 tester) | ⏳ |
| Open Testing (opsional) | ⏳ |

---

### FASE 6 — Production Launch ⚪ **0%**

| Task | Status |
|------|--------|
| Set harga: Premium Rp19rb/bln + Rp199rb lifetime (IAP) | ⏳ |
| Submit untuk review Play Store | ⏳ |
| Promosi (Twitter/X, Telegram, grup) | ⏳ |

---

### FASE 7 — Post-Launch Iterasi ⚪ **0%**

- Fitur baru berdasarkan feedback user
- AI-powered insight (local LLM via ML Kit / TensorFlow Lite)
- Export data ke CSV/JSON
- Smart notification dengan variasi pesan (30+ template)
- Social features (shared challenges, leaderboard) — opsional
- Wear OS companion

---

## 📈 PROGRESS TRACKER

| Fase | % Progress | Status |
|------|-----------|--------|
| **Fase 1:** Finalisasi fitur inti | 100% | ✅ Selesai |
| **Fase 2:** Fitur pelengkap premium | 10% | 🟡 Google Drive backup (tersisa) |
| **Fase 3:** Release build | 100% | ✅ Signed APK + AAB, GitHub Release v1.0.0 |
| **Fase 4:** Play Store assets | 100% | ✅ ALL DONE |
| **Fase 5:** Beta testing | 0% | ⚪ Belum mulai |
| **Fase 6:** Production launch | 0% | ⚪ Belum mulai |
| **Fase 7:** Post-launch | 0% | ⚪ Belum mulai |

---

## 🔗 REFERENSI CEPAT

| Resource | Path |
|----------|------|
| Konsep aplikasi | `~/workspace/KONSEP_APLIKASI.md` |
| Build status | `github.com/neofidao-spec/HabitSehat/actions` |
| APK terbaru | Artifact Run #52 (signed release) |
| Repo | `github.com/neofidao-spec/HabitSehat` (public) |
| Play Store assets | `~/workspace/HabitSehat/play-store-assets/` |
| Listing copy | `~/workspace/HabitSehat/PLAY_STORE_LISTING.md` |
| Privacy policy | `~/workspace/HabitSehat/privacy-policy.html` |

---

## 📋 NEXT ACTIONS (Prioritas)

| Prioritas | Task | Estimasi |
|-----------|------|----------|
| 🔴 P1 | **Publish Privacy Policy ke GitHub Pages** (Settings → Pages → main branch / root) | 5 menit |
| 🔴 P1 | **Play Console: Create App** → upload AAB dari Run #52 artifact | 15 menit |
| 🔴 P1 | **Store Listing**: copy dari `PLAY_STORE_LISTING.md`, upload assets dari `play-store-assets/` | 20 menit |
| 🟡 P2 | **IAP Setup**: Premium Monthly Rp19rb + Lifetime Rp199rb | 15 menit |
| 🟡 P2 | **Internal Testing** → Closed Testing → Production | 1-2 hari |

---

> **Mulai:** Setiap sesi coding, lihat PETA_KERJA.md → kerjakan item Prioritas P1 → P2 → P3  
> **Commit:** `git add -A && git -c user.name="Fani Hernanda" -c user.email="nandaori04@gmail.com" commit -m "Pesan deskriptif" && git push`  
> **Verify:** Tunggu GitHub Actions selesai, cek hasilnya