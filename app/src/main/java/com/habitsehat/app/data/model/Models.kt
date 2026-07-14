package com.habitsehat.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String = "check_circle",
    val colorHex: String = "#4CAF50",
    val targetCount: Int = 1,
    val unit: String = "kali",
    val reminderEnabled: Boolean = true,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0,
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "habit_logs")
data class HabitLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val date: String, // yyyy-MM-dd
    val count: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // yyyy-MM-dd
    val amountMl: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bad_habits")
data class BadHabit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String = "🚫",
    val costPerOccurrence: Int = 0,        // biaya per kali (Rupiah)
    val frequencyPerDay: Int = 1,          // estimasi frekuensi harian
    val healthImpact: String = "",         // dampak kesehatan
    val trigger: String = "",              // pemicu
    val replacementHabit: String = "",     // kebiasaan pengganti
    val isActive: Boolean = true,
    val startDate: String = "",            // yyyy-MM-dd (tanggal mulai stop)
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bad_habit_logs")
data class BadHabitLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val badHabitId: Long,
    val date: String,                      // yyyy-MM-dd
    val resistedCount: Int = 0,            // berapa kali berhasil menolak
    val gaveInCount: Int = 0,              // berapa kali menyerah
    val mood: Int = 3,                     // 1-5 (skala mood)
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)