package com.habitsehat.app.data.repository

import com.habitsehat.app.data.db.AppDatabase
import com.habitsehat.app.data.db.BadHabitDao
import com.habitsehat.app.data.db.BadHabitLogDao
import com.habitsehat.app.data.db.HabitDao
import com.habitsehat.app.data.db.HabitLogDao
import com.habitsehat.app.data.db.WaterLogDao
import com.habitsehat.app.data.model.BadHabit
import com.habitsehat.app.data.model.BadHabitLog
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.model.HabitLog
import com.habitsehat.app.data.model.WaterLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao,
    private val waterLogDao: WaterLogDao,
    private val badHabitDao: BadHabitDao,
    private val badHabitLogDao: BadHabitLogDao
) {
    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private fun today() = LocalDate.now().format(dateFormat)

    // Habits
    suspend fun getAllHabits() = habitDao.getAllActive()
    suspend fun addHabit(habit: Habit) = habitDao.insert(habit)
    suspend fun updateHabit(habit: Habit) = habitDao.update(habit)
    suspend fun archiveHabit(id: Long) = habitDao.archive(id)

    // Habit Logs
    suspend fun checkHabit(habitId: Long, date: String = today()) {
        habitLogDao.insert(HabitLog(habitId = habitId, date = date))
    }

    suspend fun uncheckHabit(habitId: Long, date: String = today()) {
        habitLogDao.undoLast(habitId, date)
    }

    suspend fun isHabitChecked(habitId: Long, date: String = today()): Boolean {
        return (habitLogDao.getTotalCount(habitId, date) ?: 0) > 0
    }

    suspend fun getHabitCount(habitId: Long, date: String = today()): Int {
        return habitLogDao.getTotalCount(habitId, date) ?: 0
    }

    fun getTodayProgress(): Flow<Pair<Int, Int>> = flow {
        val habits = habitDao.getAllActive()
        var done = 0
        var total = habits.size
        for (habit in habits) {
            if (isHabitChecked(habit.id)) done++
        }
        emit(done to total)
    }

    // Water
    suspend fun addWater(amountMl: Int, date: String = today()) {
        waterLogDao.insert(WaterLog(date = date, amountMl = amountMl))
    }

    suspend fun undoWater(date: String = today()) {
        waterLogDao.undoLast(date)
    }

    suspend fun getWaterTotal(date: String = today()): Int {
        return waterLogDao.getTotal(date) ?: 0
    }

    fun getWaterLogs(date: String = today()): Flow<List<WaterLog>> = flow {
        emit(waterLogDao.getLogs(date))
    }

    // Bad Habits (HabitStop)
    suspend fun getAllBadHabits() = badHabitDao.getAllActive()

    suspend fun addBadHabit(badHabit: BadHabit) = badHabitDao.insert(badHabit)

    suspend fun updateBadHabit(badHabit: BadHabit) = badHabitDao.update(badHabit)

    suspend fun deactivateBadHabit(id: Long) = badHabitDao.deactivate(id)

    suspend fun getBadHabitById(id: Long) = badHabitDao.getById(id)

    // Bad Habit Logs - Resist (berhasil menolak)
    suspend fun resistBadHabit(badHabitId: Long, date: String = today()) {
        val existingLogs = badHabitLogDao.getLogs(badHabitId, date)
        if (existingLogs.isNotEmpty()) {
            val log = existingLogs[0].copy(resistedCount = existingLogs[0].resistedCount + 1)
            badHabitLogDao.update(log)
        } else {
            badHabitLogDao.insert(BadHabitLog(
                badHabitId = badHabitId,
                date = date,
                resistedCount = 1,
                gaveInCount = 0
            ))
        }
    }

    // Bad Habit Logs - Give In (serah/mengalah)
    suspend fun giveInBadHabit(badHabitId: Long, date: String = today()) {
        val existingLogs = badHabitLogDao.getLogs(badHabitId, date)
        if (existingLogs.isNotEmpty()) {
            val log = existingLogs[0].copy(gaveInCount = existingLogs[0].gaveInCount + 1)
            badHabitLogDao.update(log)
        } else {
            badHabitLogDao.insert(BadHabitLog(
                badHabitId = badHabitId,
                date = date,
                resistedCount = 0,
                gaveInCount = 1
            ))
        }
    }

    suspend fun getBadHabitStats(badHabitId: Long) {
        val totalDays = badHabitLogDao.getTotalDaysResisted(badHabitId)
        val totalOccurrences = badHabitLogDao.getTotalOccurrencesResisted(badHabitId) ?: 0
        val lastDate = badHabitLogDao.getLastResistedDate(badHabitId)
    }

    suspend fun getTotalMoneySaved(): Int {
        val badHabits = badHabitDao.getAllActiveSync()
        var totalSaved = 0
        for (habit in badHabits) {
            val occurrences = badHabitLogDao.getTotalOccurrencesResisted(habit.id) ?: 0
            totalSaved += occurrences * habit.costPerOccurrence
        }
        return totalSaved
    }

    suspend fun getBadHabitResistedStreak(badHabitId: Long, since: String): Int {
        return badHabitLogDao.getResistedStreak(badHabitId, since)
    }

    // Additional methods for BadHabitViewModel
    suspend fun getBadHabitStats(badHabitId: Long): Pair<Int, Int> {
        val totalDays = badHabitLogDao.getTotalDaysResisted(badHabitId)
        val totalOccurrences = badHabitLogDao.getTotalOccurrencesResisted(badHabitId) ?: 0
        return Pair(totalOccurrences, totalDays)
    }

    suspend fun getBadHabitStreak(badHabitId: Long): Int {
        val since = LocalDate.now().minusDays(365).format(dateFormat)
        return badHabitLogDao.getResistedStreak(badHabitId, since)
    }

    suspend fun getMoneySaved(badHabit: BadHabit): Int {
        val occurrences = badHabitLogDao.getTotalOccurrencesResisted(badHabit.id) ?: 0
        return occurrences * badHabit.costPerOccurrence
    }

    suspend fun getLastResistedDate(badHabitId: Long): String? {
        return badHabitLogDao.getLastResistedDate(badHabitId)
    }
}