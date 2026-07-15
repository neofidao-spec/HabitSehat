package com.habitsehat.app.data.db

import androidx.room.*
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
import java.time.LocalDate

@Dao
interface HabitDao {
    @Insert
    suspend fun insert(habit: Habit)

    @Update
    suspend fun update(habit: Habit)

    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY sortOrder ASC")
    suspend fun getAllActive(): List<Habit>

    @Query("SELECT * FROM habits ORDER BY sortOrder ASC")
    suspend fun getAll(): List<Habit>

    @Query("UPDATE habits SET isArchived = 1 WHERE id = :id")
    suspend fun archive(id: Long)

    @Query("UPDATE habits SET isArchived = 0 WHERE id = :id")
    suspend fun restore(id: Long)

    @Query("SELECT * FROM habits WHERE isArchived = 1 ORDER BY sortOrder ASC")
    suspend fun getAllArchived(): List<Habit>

    @Delete
    suspend fun delete(habit: Habit)
}

@Dao
interface HabitLogDao {
    @Insert
    suspend fun insert(log: HabitLog)

    @Query("SELECT COUNT(*) FROM habit_logs WHERE habitId = :habitId AND date = :date")
    suspend fun getTotalCount(habitId: Long, date: String): Int?

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND date = :date AND id = (SELECT id FROM habit_logs WHERE habitId = :habitId AND date = :date ORDER BY id DESC LIMIT 1)")
    suspend fun undoLast(habitId: Long, date: String)

    @Query("SELECT COUNT(*) FROM habit_logs WHERE habitId = :habitId AND date >= :since")
    suspend fun getStreakCount(habitId: Long, since: String): Int

    @Delete
    suspend fun delete(log: HabitLog)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId")
    suspend fun deleteByHabitId(habitId: Long)
}

@Dao
interface WaterLogDao {
    @Insert
    suspend fun insert(log: WaterLog)

    @Query("SELECT SUM(amountMl) FROM water_logs WHERE date = :date")
    suspend fun getTotal(date: String): Int?

    @Query("DELETE FROM water_logs WHERE date = :date AND id = (SELECT id FROM water_logs WHERE date = :date ORDER BY id DESC LIMIT 1)")
    suspend fun undoLast(date: String)

    @Query("SELECT * FROM water_logs WHERE date = :date ORDER BY id DESC")
    suspend fun getLogs(date: String): List<WaterLog>

    @Query("SELECT AVG(amountMl) FROM water_logs WHERE date >= :start AND date <= :end")
    suspend fun getAverageInRange(start: String, end: String): Double?

    @Query("DELETE FROM water_logs")
    suspend fun deleteAll()
}

@Dao
interface BadHabitDao {
    @Insert
    suspend fun insert(badHabit: BadHabit)

    @Update
    suspend fun update(badHabit: BadHabit)

    @Query("SELECT * FROM bad_habits WHERE isActive = 1 ORDER BY sortOrder ASC")
    suspend fun getAllActive(): List<BadHabit>

    @Query("SELECT * FROM bad_habits ORDER BY sortOrder ASC")
    suspend fun getAll(): List<BadHabit>

    @Query("UPDATE bad_habits SET isActive = 0 WHERE id = :id")
    suspend fun deactivate(id: Long)

    @Query("SELECT * FROM bad_habits WHERE id = :id")
    suspend fun getById(id: Long): BadHabit?

    @Delete
    suspend fun delete(badHabit: BadHabit)
}

@Dao
interface BadHabitLogDao {
    @Insert
    suspend fun insert(log: BadHabitLog)

    @Update
    suspend fun update(log: BadHabitLog)

    @Query("SELECT * FROM bad_habit_logs WHERE badHabitId = :badHabitId AND date = :date")
    suspend fun getLogs(badHabitId: Long, date: String): List<BadHabitLog>

    @Query("SELECT COUNT(DISTINCT date) FROM bad_habit_logs WHERE badHabitId = :badHabitId AND resistedCount > 0")
    suspend fun getTotalDaysResisted(badHabitId: Long): Int

    @Query("SELECT COALESCE(SUM(gaveInCount + resistedCount), 0) FROM bad_habit_logs WHERE badHabitId = :badHabitId")
    suspend fun getTotalOccurrencesResisted(badHabitId: Long): Int?

    @Query("SELECT COUNT(*) FROM bad_habit_logs WHERE badHabitId = :badHabitId AND date >= :since AND resistedCount > 0")
    suspend fun getResistedStreak(badHabitId: Long, since: String): Int

    @Query("SELECT MAX(date) FROM bad_habit_logs WHERE badHabitId = :badHabitId AND resistedCount > 0")
    suspend fun getLastResistedDate(badHabitId: Long): String?

    @Delete
    suspend fun delete(log: BadHabitLog)

    @Query("DELETE FROM bad_habit_logs WHERE badHabitId = :badHabitId")
    suspend fun deleteByBadHabitId(badHabitId: Long)

    @Query("DELETE FROM bad_habit_logs")
    suspend fun deleteAll()
}

@Dao
interface PomodoroDao {
    @Insert
    suspend fun insert(session: PomodoroSession)

    @Query("SELECT COALESCE(SUM(focusSeconds), 0) FROM pomodoro_sessions WHERE date = :date")
    suspend fun getTotalFocusSeconds(date: String): Int?

    @Query("SELECT COUNT(*) FROM pomodoro_sessions WHERE date = :date")
    suspend fun getSessionCount(date: String): Int

    @Query("SELECT COALESCE(SUM(focusSeconds), 0) FROM pomodoro_sessions WHERE date >= :since")
    suspend fun getWeeklyFocusSeconds(since: String): Int?

    @Query("SELECT COALESCE(SUM(focusSeconds), 0) FROM pomodoro_sessions WHERE date >= :start AND date <= :end")
    suspend fun getFocusSecondsInRange(start: String, end: String): Int?

    @Query("SELECT * FROM pomodoro_sessions ORDER BY id DESC LIMIT 10")
    suspend fun getRecentSessions(): List<PomodoroSession>

    @Query("DELETE FROM pomodoro_sessions")
    suspend fun deleteAll()
}

@Dao
interface ChallengeDao {
    @Insert
    suspend fun insert(challenge: Challenge)

    @Update
    suspend fun update(challenge: Challenge)

    @Query("SELECT * FROM challenges WHERE isActive = 1 ORDER BY targetDays ASC")
    suspend fun getAllActive(): List<Challenge>

    @Query("SELECT * FROM challenges ORDER BY targetDays ASC")
    suspend fun getAll(): List<Challenge>

    @Query("SELECT * FROM challenges WHERE id = :id")
    suspend fun getById(id: Long): Challenge?

    @Delete
    suspend fun delete(challenge: Challenge)

    @Query("DELETE FROM challenges")
    suspend fun deleteAll()
}

@Dao
interface ChallengeProgressDao {
    @Insert
    suspend fun insert(progress: ChallengeProgress)

    @Update
    suspend fun update(progress: ChallengeProgress)

    @Query("SELECT * FROM challenge_progress WHERE challengeId = :challengeId")
    suspend fun getProgress(challengeId: Long): ChallengeProgress?

    @Query("SELECT * FROM challenge_progress")
    suspend fun getAllProgress(): List<ChallengeProgress>

    @Query("SELECT * FROM challenge_progress WHERE completed = 0")
    suspend fun getActiveProgress(): List<ChallengeProgress>

    @Query("SELECT * FROM challenge_progress WHERE completed = 1")
    suspend fun getCompletedProgress(): List<ChallengeProgress>

    @Delete
    suspend fun delete(progress: ChallengeProgress)

    @Query("DELETE FROM challenge_progress")
    suspend fun deleteAll()
}

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY id DESC")
    suspend fun getExpensesByDate(date: String): List<Expense>

    @Query("SELECT * FROM expenses WHERE date >= :start AND date <= :end ORDER BY date DESC, id DESC")
    suspend fun getExpensesInRange(start: String, end: String): List<Expense>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE date = :date")
    suspend fun getTotalByDate(date: String): Long?

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE date >= :start AND date <= :end")
    suspend fun getTotalInRange(start: String, end: String): Long?

    @Query("SELECT e.*, c.id as category_id, c.name as category_name, c.icon as category_icon, c.colorHex as category_colorHex, c.isDefault as category_isDefault, c.sortOrder as category_sortOrder, c.createdAt as category_createdAt FROM expenses e JOIN expense_categories c ON e.categoryId = c.id WHERE e.date >= :start AND e.date <= :end ORDER BY e.date DESC, e.id DESC")
    suspend fun getExpensesWithCategory(start: String, end: String): List<ExpenseWithCategory>

    @Query("SELECT c.name as categoryName, c.icon as categoryIcon, c.colorHex as categoryColor, COALESCE(SUM(e.amount), 0) as total FROM expenses e JOIN expense_categories c ON e.categoryId = c.id WHERE e.date >= :start AND e.date <= :end GROUP BY e.categoryId ORDER BY total DESC")
    suspend fun getTotalsByCategory(start: String, end: String): List<CategoryTotal>

    @Query("DELETE FROM expenses")
    suspend fun deleteAll()
}

@Dao
interface ExpenseCategoryDao {
    @Insert
    suspend fun insert(category: ExpenseCategory)

    @Update
    suspend fun update(category: ExpenseCategory)

    @Delete
    suspend fun delete(category: ExpenseCategory)

    @Query("SELECT * FROM expense_categories ORDER BY sortOrder ASC")
    suspend fun getAll(): List<ExpenseCategory>

    @Query("SELECT * FROM expense_categories WHERE isDefault = 1 ORDER BY sortOrder ASC")
    suspend fun getDefaults(): List<ExpenseCategory>

    @Query("SELECT * FROM expense_categories WHERE id = :id")
    suspend fun getById(id: Long): ExpenseCategory?

    @Query("DELETE FROM expense_categories")
    suspend fun deleteAll()
}

data class ExpenseWithCategory(
    @Embedded val expense: Expense,
    @Embedded(prefix = "category_") val expenseCategory: ExpenseCategory?
)

data class CategoryTotal(
    val categoryName: String,
    val categoryIcon: String,
    val categoryColor: String,
    val total: Long
)
