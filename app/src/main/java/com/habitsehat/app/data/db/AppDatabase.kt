package com.habitsehat.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.habitsehat.app.data.model.BadHabit
import com.habitsehat.app.data.model.BadHabitLog
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.model.HabitLog
import com.habitsehat.app.data.model.WaterLog

@Database(
    entities = [Habit::class, HabitLog::class, WaterLog::class, BadHabit::class, BadHabitLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun waterLogDao(): WaterLogDao
    abstract fun badHabitDao(): BadHabitDao
    abstract fun badHabitLogDao(): BadHabitLogDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habitsehat.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}