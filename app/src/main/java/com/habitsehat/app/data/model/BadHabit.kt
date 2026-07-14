package com.habitsehat.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "bad_habits")
data class BadHabit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                          // "Merokok", "Scroll TikTok", "Minum kopi manis"
    val category: BadHabitCategory = BadHabitCategory.Other,
    val costPerOccurrence: Int = 0,            // Biaya per kali (rupiah)
    val occurrencesPerDay: Int = 1,            // Estimasi frekuensi harian
    val startDate: String = LocalDate.now().toString(), // YYYY-MM-DD
    val isActive: Boolean = true,
    val icon: String = "🚫",                   // Emoji icon
    val colorHex: String = "#F44336",          // Warna tema
    val healthBenefits: String = "",           // Manfaat kesehatan (deskripsi)
    val motivationNote: String = ""            // Catatan motivasi pribadi
)

enum class BadHabitCategory(val label: String) {
    Smoking("Merokok"),
    Vaping("Vape"),
    Alcohol("Alkohol"),
    Sugar("Gula/Minuman Manis"),
    Caffeine("Kafein Berlebih"),
    SocialMedia("Media Sosial"),
    Gaming("Game"),
    Gambling("Judi/Taruhan"),
    Procrastination("Menunda-nunda"),
    LateNight("Begadang"),
    JunkFood("Makanan Sampah"),
    Other("Lainnya")
}

@Entity(tableName = "bad_habit_logs")
data class BadHabitLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val badHabitId: Long,
    val date: String,                          // YYYY-MM-DD
    val resistedCount: Int = 0,                // Berapa kali berhasil menolak
    val gaveInCount: Int = 0,                  // Berapa kali "kalah" (opsional tracking)
    val note: String = "",                     // Catatan harian
    val mood: Int = 3                          // 1-5 skala mood
)

data class BadHabitWithStats(
    val badHabit: BadHabit,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalDaysResisted: Int,
    val totalMoneySaved: Int,
    val totalOccurrencesResisted: Int,
    val lastResistedDate: String?
)