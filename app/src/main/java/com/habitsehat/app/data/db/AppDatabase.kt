package com.habitsehat.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.habitsehat.app.data.model.BadHabit
import com.habitsehat.app.data.model.BadHabitLog
import com.habitsehat.app.data.model.Challenge
import com.habitsehat.app.data.model.ChallengeProgress
import com.habitsehat.app.data.model.Expense
import com.habitsehat.app.data.model.ExpenseCategory
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.model.HabitLog
import com.habitsehat.app.data.model.PomodoroSession
import com.habitsehat.app.data.model.WaterLog

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        Habit::class, HabitLog::class, WaterLog::class,
        BadHabit::class, BadHabitLog::class,
        PomodoroSession::class,
        Challenge::class, ChallengeProgress::class,
        Expense::class, ExpenseCategory::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun waterLogDao(): WaterLogDao
    abstract fun badHabitDao(): BadHabitDao
    abstract fun badHabitLogDao(): BadHabitLogDao
    abstract fun pomodoroDao(): PomodoroDao
    abstract fun challengeDao(): ChallengeDao
    abstract fun challengeProgressDao(): ChallengeProgressDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun expenseCategoryDao(): ExpenseCategoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // Migration 1→2: add bad_habits + bad_habit_logs
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `bad_habits` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `emoji` TEXT NOT NULL DEFAULT '🚫', `colorHex` TEXT NOT NULL DEFAULT '#F44336', `costPerOccurrence` INTEGER NOT NULL DEFAULT 0, `frequencyPerDay` INTEGER NOT NULL DEFAULT 1, `healthImpact` TEXT NOT NULL DEFAULT '', `trigger` TEXT NOT NULL DEFAULT '', `replacementHabit` TEXT NOT NULL DEFAULT '', `isActive` INTEGER NOT NULL DEFAULT 1, `sortOrder` INTEGER NOT NULL DEFAULT 0, `startDate` TEXT, `createdAt` INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `bad_habit_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `badHabitId` INTEGER NOT NULL, `date` TEXT NOT NULL, `resistedCount` INTEGER NOT NULL DEFAULT 0, `gaveInCount` INTEGER NOT NULL DEFAULT 0, `mood` INTEGER NOT NULL DEFAULT 3, `note` TEXT NOT NULL DEFAULT '', `createdAt` INTEGER NOT NULL DEFAULT 0)")
            }
        }

        // Migration 2→3: add pomodoro_sessions
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `pomodoro_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `durationMinutes` INTEGER NOT NULL, `focusSeconds` INTEGER NOT NULL, `habitId` INTEGER, `date` TEXT NOT NULL, `createdAt` INTEGER NOT NULL DEFAULT 0)")
            }
        }

        // Migration 3→4: add challenges + challenge_progress
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `challenges` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT NOT NULL, `icon` TEXT NOT NULL DEFAULT '🏆', `targetDays` INTEGER NOT NULL, `category` TEXT NOT NULL DEFAULT 'habit', `isActive` INTEGER NOT NULL DEFAULT 1, `createdAt` INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `challenge_progress` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `challengeId` INTEGER NOT NULL, `currentDays` INTEGER NOT NULL DEFAULT 0, `startDate` TEXT NOT NULL, `lastUpdateDate` TEXT NOT NULL, `completed` INTEGER NOT NULL DEFAULT 0, `createdAt` INTEGER NOT NULL DEFAULT 0)")
            }
        }

        // Migration 4→5: add expenses + expense_categories
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `expenses` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `categoryId` INTEGER NOT NULL, `date` TEXT NOT NULL, `amount` INTEGER NOT NULL, `note` TEXT NOT NULL DEFAULT '', `createdAt` INTEGER NOT NULL DEFAULT 0)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `expense_categories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `icon` TEXT NOT NULL DEFAULT '💰', `colorHex` TEXT NOT NULL DEFAULT '#4CAF50', `isDefault` INTEGER NOT NULL DEFAULT 0, `sortOrder` INTEGER NOT NULL DEFAULT 0, `createdAt` INTEGER NOT NULL DEFAULT 0)")
            }
        }

        // Migration 5→6: bump version, no schema change (placeholder for future)
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // No schema changes — version bump only
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habitsehat.db"
                )
                    .addMigrations(
                        MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4,
                        MIGRATION_4_5, MIGRATION_5_6
                    )
                    .build().also { INSTANCE = it }
            }
        }
    }
}