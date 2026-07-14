package com.habitsehat.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.habitsehat.app.data.model.BadHabit
import com.habitsehat.app.data.model.BadHabitLog
import com.habitsehat.app.data.model.Challenge
import com.habitsehat.app.data.model.ChallengeProgress
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.model.HabitLog
import com.habitsehat.app.data.model.PomodoroSession
import com.habitsehat.app.data.model.WaterLog

@Database(
    entities = [
        Habit::class, HabitLog::class, WaterLog::class,
        BadHabit::class, BadHabitLog::class,
        PomodoroSession::class,
        Challenge::class, ChallengeProgress::class
    ],
    version = 3,
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

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habitsehat.db"
                )
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}
