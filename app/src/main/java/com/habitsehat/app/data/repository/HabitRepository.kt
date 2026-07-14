package com.habitsehat.app.data.repository

import com.habitsehat.app.data.db.HabitDao
import com.habitsehat.app.data.db.HabitLogDao
import com.habitsehat.app.data.db.WaterLogDao
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
    private val waterLogDao: WaterLogDao
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
        val todayStr = today()
        for (h in habits) {
            val c = habitLogDao.getTotalCount(h.id, todayStr) ?: 0
            if (c >= h.targetCount) done++
        }
        emit(Pair(done, habits.size))
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

    suspend fun getWaterLogs(date: String = today()): List<WaterLog> {
        return waterLogDao.getLogs(date)
    }
}
