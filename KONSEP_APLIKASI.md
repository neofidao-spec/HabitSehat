# Konsep Aplikasi HabitSehat — Senior Developer Vision

## Visi
Aplikasi utility personal yang **sederhana, indah, dan terpercaya** — membantu user membangun kebiasaan baik, mengendalikan kebiasaan buruk, dan mengelola pengeluaran harian. **Offline-first, tanpa login, privat.**

## Filosofi Desain
1. **Minimalis & Fokus** — Satu layar = satu tujuan. Tidak ada informasi berlebihan.
2. **Konsisten** — Setiap tombol, card, input — polanya sama di seluruh app.
3. **Responsif** — Setiap ketukan terasa: animasi halus, spring bounce, feedback instan.
4. **Personal** — 20 tema, greeting dinamis, quote rotasi, streak visual.
5. **Offline-first** — Privasi user adalah prioritas. Tidak perlu akun.

## Standar Mutu (Tidak Bisa Ditawar)

### ✅ Berlaku Saat Ini
| Standar | Status |
|---------|--------|
| Semua tombol berfungsi (tidak ada dead button) | ✅ (Edit dihapus) |
| Scroll bekerja di semua screen panjang | ✅ |
| Loading state (CircularProgressIndicator) | ✅ |
| Error handling try-catch di ViewModel | ✅ |
| Empty state dengan pesan dan icon | ✅ |
| Konfirmasi destructive action (hapus) | ✅ |
| Touch target >= 48dp | ✅ |
| Konsistensi spacing (16dp horizontal, 12dp antar card) | ✅ |

### ❌ Perlu Dibangun
| Standar | Prioritas |
|---------|-----------|
| Snackbar untuk feedback error/sukses | **HIGH** |
| Edit habit di dropdown menu | **HIGH** |
| Material3 DatePicker (bukan manual chevron) | **MEDIUM** |
| Haptic feedback untuk habit check | **MEDIUM** |
| Snackbar undo untuk archive/delete | **MEDIUM** |
| ExpenseScreen klik item → edit (bukan add baru) | **LOW** |

## Arsitektur Screen

### Navigasi
```
BottomNav: Beranda | Statistik | HabitStop | Pengeluaran | Tema/More
        ↓                    ↓
   AddHabit ←─── Stack ──→ Premium
   AddBadHabit              Settings
   AddExpense               WeeklyReport
   ExpenseCategories        Challenges
   ExpenseReport            Pomodoro
```

### Struktur Setiap Screen
1. **Scaffold** — TopAppBar (± back button) + FAB jika perlu
2. **Loading** — CircularProgressIndicator di center
3. **Error** — SnackbarHost (BELUM ADA — PENTING)
4. **Konten** — LazyColumn / verticalScroll, spacing 12dp
5. **Empty state** — Icon + pesan + CTA

---

## Action Plan — Perbaikan Prioritas

### 1. Snackbar Error/Sukses Feedback
Semua screen perlu menampilkan error message ke user via Snackbar.
Saat ini error hanya disimpan di state (tidak tampak).

### 2. Edit Habit via Dropdown
Tambahkan route EditHabit + AddHabitScreen edit mode.
Load habit dari repository, pre-fill form, update on save.

### 3. Material3 DatePicker
AddExpenseScreen: ganti manual chevron date picker dengan DatePickerDialog Material3.

### 4. ExpenseScreen Item → Edit
Klik expense → navigasi ke AddExpense dengan data expense untuk diedit.

### 5. Toast/Snackbar untuk Aksi Cepat
"Kebiasaan diarsipkan" → snackbar dengan tombol "Undo"
"Pengeluaran dihapus" → snackbar dengan tombol "Undo"

---

## Roadmap Pengerjaan

### Fase A — UX Foundation (Sekarang)
- ✅ Scroll di semua screen
- ✅ Hapus dead button Edit
- ✅ Archive card + restore
- ✅ Category edit semua
- [ ] **Snackbar error/sukses** ← **MULAI SINI**
- [ ] **Konfirmasi hapus expense** (saat ini langsung hapus dari ExpenseItemCard)

### Fase B — Fitur Lengkap
- [ ] Edit habit
- [ ] Edit expense
- [ ] Material3 DatePicker
- [ ] Haptic feedback

### Fase C — Polish
- [ ] Animasi transisi antar screen
- [ ] Shimmer loading
- [ ] Widget improvement
- [ ] Aksesibilitas (contentDescription lengkap)
