package com.habitsehat.app.data.db

import androidx.room.*
import com.habitsehat.app.data.model.BadHabit
import com.habitsehat.app.data.model.BadHabitLog
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.model.HabitLog
import com.habitsehat.app.data.model.WaterLog

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY createdAt ASC")
    suspend fun getAllActive(): List<Habit>

    @Query("SELECT * FROM habits ORDER BY createdAt ASC")
    suspend fun getAll(): List<Habit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: Habit): Long

    @Update
    suspend fun update(habit: Habit)

    @Delete
    suspend fun delete(habit: Habit)

    @Query("UPDATE habits SET isArchived = 1 WHERE id = :id")
    suspend fun archive(id: Long)
}

@Dao
interface HabitLogDao {
    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId AND date = :date ORDER BY createdAt DESC")
    suspend fun getLogs(habitId: Long, date: String): List<HabitLog>

    @Query("SELECT SUM(count) FROM habit_logs WHERE habitId = :habitId AND date = :date")
    suspend fun getTotalCount(habitId: Long, date: String): Int?

    @Query("SELECT COUNT(DISTINCT date) FROM habit_logs WHERE habitId = :habitId AND date >= :since")
    suspend fun getStreakCount(habitId: Long, since: String): Int

    @Insert
    suspend fun insert(log: HabitLog)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND date = :date AND id IN (SELECT id FROM habit_logs WHERE habitId = :habitId AND date = :date LIMIT 1)")
    suspend fun undoLast(habitId: Long, date: String)
}

@Dao
interface WaterLogDao {
    @Query("SELECT * FROM water_logs WHERE date = :date ORDER BY createdAt ASC")
    suspend fun getLogs(date: String): List<WaterLog>

    @Query("SELECT SUM(amountMl) FROM water_logs WHERE date = :date")
    suspend fun getTotal(date: String): Int?

    @Insert
    suspend fun insert(log: WaterLog)

    @Delete
    suspend fun delete(log: WaterLog)

    @Query("DELETE FROM water_logs WHERE date = :date AND id IN (SELECT id FROM water_logs WHERE date = :date ORDER BY createdAt DESC LIMIT 1)")
    suspend fun undoLast(date: String)
}

@Dao
interface BadHabitDao {
    @Query("SELECT * FROM bad_habits WHERE isActive = 1 ORDER BY createdAt ASC")
    suspend fun getAllActive(): List<BadHabit>

    @Query("SELECT * FROM bad_habits ORDER BY createdAt ASC")
    suspend fun getAll(): List<BadHabit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(badHabit: BadHabit): Long

    @Update
    suspend fun update(badHabit: BadHabit)

    @Delete
    suspend fun delete(badHabit: BadHabit)

    @Query("UPDATE bad_habits SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)
}

@Dao
interface BadHabitLogDao {
    @Query("SELECT * FROM bad_habit_logs WHERE badHabitId = :badHabitId AND date = :date ORDER BY createdAt DESC")
    suspend fun getLogs(badHabitId: Long, date: String): List<BadHabitLog>

    @Query("SELECT * FROM bad_habit_logs WHERE badHabitId = :badHabitId ORDER BY date DESC")
    suspend fun getAllLogs(badHabitId: Long): List<BadHabitLog>

    @Query("SELECT COUNT(DISTINCT date) FROM bad_habit_logs WHERE badHabitId = :badHabitId AND resistedCount > 0 AND date >= :since")
    suspend fun getResistedStreak(badHabitId: Long, since: String): Int

    @Query("SELECT COUNT(DISTINCT date) FROM bad_habit_logs WHERE badHabitId = :badHabitId AND resistedCount > 0")
    suspend fun getTotalDaysResisted(badHabitId: Long): Int

    @Query("SELECT SUM(resistedCount) FROM bad_habit_logs WHERE badHabitId = :badHabitId")
    suspend fun getTotalOccurrencesResisted(badHabitId: Long): Int?

    @Query("SELECT MAX(date) FROM bad_habit_logs WHERE badHabitId = :badHabitId AND resistedCount > 0")
    suspend fun getLastResistedDate(badHabitId: Long): String?

    @Insert
    suspend fun insert(log: BadHabitLog)

    @Update
    suspend fun update(log: BadHabitLog)

    @Query("DELETE FROM bad_habit_logs WHERE badHabitId = :badHabitId AND date = :date")
    suspend fun deleteLog(badHabitId: Long, date: String)
}
