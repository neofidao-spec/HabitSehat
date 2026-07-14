package com.habitsehat.app.data.db

import androidx.room.*
import com.habitsehat.app.data.model.BadHabit
import com.habitsehat.app.data.model.BadHabitLog

@Dao
interface BadHabitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(badHabit: BadHabit): Long

    @Update
    suspend fun update(badHabit: BadHabit)

    @Query("SELECT * FROM bad_habits WHERE isActive = 1 ORDER BY id DESC")
    fun getAllActive(): List<BadHabit>

    @Query("SELECT * FROM bad_habits WHERE isActive = 1 ORDER BY id DESC")
    suspend fun getAllActiveSync(): List<BadHabit>

    @Query("UPDATE bad_habits SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)

    @Query("SELECT * FROM bad_habits WHERE id = :id")
    suspend fun getById(id: Long): BadHabit?
}

@Dao
interface BadHabitLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: BadHabitLog)

    @Update
    suspend fun update(log: BadHabitLog)

    @Query("SELECT * FROM bad_habit_logs WHERE badHabitId = :badHabitId AND date = :date")
    suspend fun getLogs(badHabitId: Long, date: String): List<BadHabitLog>

    @Query("SELECT SUM(resistedCount) FROM bad_habit_logs WHERE badHabitId = :badHabitId")
    suspend fun getTotalOccurrencesResisted(badHabitId: Long): Int?

    @Query("SELECT COUNT(DISTINCT date) FROM bad_habit_logs WHERE badHabitId = :badHabitId AND resistedCount > 0")
    suspend fun getTotalDaysResisted(badHabitId: Long): Int

    @Query("""
        SELECT COUNT(*) FROM bad_habit_logs
        WHERE badHabitId = :badHabitId
        AND date BETWEEN :since AND date('now')
        AND resistedCount > 0
        AND date IN (
            SELECT date FROM bad_habit_logs
            WHERE badHabitId = :badHabitId
            AND resistedCount > 0
            ORDER BY date DESC
        )
    """)
    suspend fun getResistedStreak(badHabitId: Long, since: String): Int

    @Query("SELECT MAX(date) FROM bad_habit_logs WHERE badHabitId = :badHabitId AND resistedCount > 0")
    suspend fun getLastResistedDate(badHabitId: Long): String?
}