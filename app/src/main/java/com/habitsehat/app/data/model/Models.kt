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
    val date: String,
    val count: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val amountMl: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bad_habits")
data class BadHabit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val emoji: String = "🚫",
    val colorHex: String = "#F44336",
    val costPerOccurrence: Int = 0,
    val frequencyPerDay: Int = 1,
    val healthImpact: String = "",
    val trigger: String = "",
    val replacementHabit: String = "",
    val isActive: Boolean = true,
    val startDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bad_habit_logs")
data class BadHabitLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val badHabitId: Long,
    val date: String,
    val resistedCount: Int = 0,
    val gaveInCount: Int = 0,
    val mood: Int = 3,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val durationMinutes: Int,          // 25, 50, atau 90
    val completedSeconds: Int,         // berapa detik benar-benar fokus
    val habitId: Long? = null,         // habit yang dikerjakan (opsional)
    val date: String,                  // yyyy-MM-dd
    val createdAt: Long = System.currentTimeMillis()
)
