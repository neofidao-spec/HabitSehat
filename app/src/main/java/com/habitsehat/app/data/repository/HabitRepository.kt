package com.habitsehat.app.data.repository

import com.habitsehat.app.data.db.*
import com.habitsehat.app.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao,
    private val waterLogDao: WaterLogDao,
    private val badHabitDao: BadHabitDao,
    private val badHabitLogDao: BadHabitLogDao,
    private val pomodoroDao: PomodoroDao,
    private val challengeDao: ChallengeDao,
    private val challengeProgressDao: ChallengeProgressDao
) {
    private fun today() = LocalDate.now()
    private val isoDateFormat = DateTimeFormatter.ISO_LOCAL_DATE

    private fun toStr(date: LocalDate) = date.format(isoDateFormat)

    // ============ HABITS ============
    suspend fun getAllHabits() = habitDao.getAllActive()
    suspend fun addHabit(habit: Habit) = habitDao.insert(habit)
    suspend fun updateHabit(habit: Habit) = habitDao.update(habit)
    suspend fun archiveHabit(id: Long) = habitDao.archive(id)

    suspend fun checkHabit(habitId: Long, date: LocalDate = today()) {
        habitLogDao.insert(HabitLog(habitId = habitId, date = date))
    }

    suspend fun uncheckHabit(habitId: Long, date: LocalDate = today()) {
        habitLogDao.undoLast(habitId, toStr(date))
    }

    suspend fun isHabitChecked(habitId: Long, date: LocalDate = today()): Boolean {
        return (habitLogDao.getTotalCount(habitId, toStr(date)) ?: 0) > 0
    }

    suspend fun getHabitCount(habitId: Long, date: LocalDate = today()): Int {
        return habitLogDao.getTotalCount(habitId, toStr(date)) ?: 0
    }

    suspend fun getStreak(habitId: Long, since: LocalDate): Int {
        return habitLogDao.getStreakCount(habitId, toStr(since))
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

    // ============ WATER ============
    suspend fun addWater(amountMl: Int, date: LocalDate = today()) {
        waterLogDao.insert(WaterLog(date = date, amountMl = amountMl))
    }

    suspend fun undoWater(date: LocalDate = today()) {
        waterLogDao.undoLast(toStr(date))
    }

    suspend fun getWaterTotal(date: LocalDate = today()): Int {
        return waterLogDao.getTotal(toStr(date)) ?: 0
    }

    fun getWaterLogs(date: LocalDate = today()): Flow<List<WaterLog>> = flow {
        emit(waterLogDao.getLogs(toStr(date)))
    }

    // ============ BAD HABITS ============
    suspend fun getAllBadHabits() = badHabitDao.getAllActive()
    suspend fun addBadHabit(badHabit: BadHabit) = badHabitDao.insert(badHabit)
    suspend fun updateBadHabit(badHabit: BadHabit) = badHabitDao.update(badHabit)
    suspend fun deactivateBadHabit(id: Long) = badHabitDao.deactivate(id)
    suspend fun getBadHabitById(id: Long) = badHabitDao.getById(id)

    suspend fun resistBadHabit(badHabitId: Long, date: LocalDate = today()) {
        val dateStr = toStr(date)
        val existingLogs = badHabitLogDao.getLogs(badHabitId, dateStr)
        if (existingLogs.isNotEmpty()) {
            val log = existingLogs[0].copy(resistedCount = existingLogs[0].resistedCount + 1)
            badHabitLogDao.update(log)
        } else {
            badHabitLogDao.insert(BadHabitLog(
                badHabitId = badHabitId, date = date,
                resistedCount = 1, gaveInCount = 0
            ))
        }
    }

    suspend fun giveInBadHabit(badHabitId: Long, date: LocalDate = today()) {
        val dateStr = toStr(date)
        val existingLogs = badHabitLogDao.getLogs(badHabitId, dateStr)
        if (existingLogs.isNotEmpty()) {
            val log = existingLogs[0].copy(gaveInCount = existingLogs[0].gaveInCount + 1)
            badHabitLogDao.update(log)
        } else {
            badHabitLogDao.insert(BadHabitLog(
                badHabitId = badHabitId, date = date,
                resistedCount = 0, gaveInCount = 1
            ))
        }
    }

    suspend fun getBadHabitStats(badHabitId: Long): Pair<Int, Int> {
        val totalDays = badHabitLogDao.getTotalDaysResisted(badHabitId)
        val totalOccurrences = badHabitLogDao.getTotalOccurrencesResisted(badHabitId) ?: 0
        return totalOccurrences to totalDays
    }

    suspend fun getTotalMoneySaved(): Int {
        val badHabits = badHabitDao.getAllActive()
        var totalSaved = 0
        for (habit in badHabits) {
            val occurrences = badHabitLogDao.getTotalOccurrencesResisted(habit.id) ?: 0
            totalSaved += occurrences * habit.costPerOccurrence
        }
        return totalSaved
    }

    suspend fun getBadHabitResistedStreak(badHabitId: Long, since: LocalDate): Int {
        return badHabitLogDao.getResistedStreak(badHabitId, toStr(since))
    }

    suspend fun getLastResistedDate(badHabitId: Long): LocalDate? {
        val str = badHabitLogDao.getLastResistedDate(badHabitId)
        return str?.let { LocalDate.parse(it) }
    }

    suspend fun getMoneySaved(badHabit: BadHabit): Int {
        val occurrences = badHabitLogDao.getTotalOccurrencesResisted(badHabit.id) ?: 0
        return occurrences * badHabit.costPerOccurrence
    }

    suspend fun getBadHabitStreak(badHabitId: Long): Int {
        val since = LocalDate.now().minusDays(365)
        return getBadHabitResistedStreak(badHabitId, since)
    }

    // ============ POMODORO ============
    suspend fun savePomodoroSession(session: PomodoroSession) = pomodoroDao.insert(session)
    suspend fun getTotalFocusSeconds(): Int = pomodoroDao.getTotalFocusSeconds(toStr(today())) ?: 0
    suspend fun getSessionCount(): Int = pomodoroDao.getSessionCount(toStr(today()))
    suspend fun getWeeklyFocusSeconds(): Int {
        val since = LocalDate.now().minusDays(7)
        return pomodoroDao.getWeeklyFocusSeconds(toStr(since)) ?: 0
    }
    suspend fun getRecentSessions() = pomodoroDao.getRecentSessions()

    // ============ WEEKLY REPORT ============
    suspend fun generateWeeklyReport(): WeeklyReport {
        val now = LocalDate.now()
        val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = monday.plusDays(6)

        val start = toStr(monday)
        val end = toStr(sunday)
        val previousWeekStart = toStr(monday.minusDays(7))
        val previousWeekEnd = toStr(monday.minusDays(1))

        val habits = habitDao.getAllActive()

        // This week stats
        var totalDoneDays = 0
        var totalPossibleDays = 0
        val habitStats = mutableListOf<HabitStat>()

        // Check each day
        var currentDay = monday
        val dailyDoneCounts = mutableMapOf<String, Int>() // date -> done count
        while (!currentDay.isAfter(sunday)) {
            val dateStr = toStr(currentDay)
            var done = 0
            for (habit in habits) {
                if (isHabitChecked(habit.id, dateStr)) done++
            }
            dailyDoneCounts[dateStr] = done
            totalPossibleDays++
            if (done == habits.size && habits.isNotEmpty()) totalDoneDays++
            currentDay = currentDay.plusDays(1)
        }

        // Per-habit stats
        for (habit in habits) {
            var habitDoneDays = 0
            currentDay = monday
            while (!currentDay.isAfter(sunday)) {
                val dateStr = toStr(currentDay)
                if (isHabitChecked(habit.id, dateStr)) habitDoneDays++
                currentDay = currentDay.plusDays(1)
            }
            habitStats.add(HabitStat(habit.name, habitDoneDays, 7))
        }

        // Water average
        val waterAvg = waterLogDao.getAverageInRange(start, end) ?: 0.0
        val prevWaterAvg = waterLogDao.getAverageInRange(previousWeekStart, previousWeekEnd) ?: 0.0

        // Best & worst day
        val bestDay = dailyDoneCounts.maxByOrNull { it.value }?.key ?: start
        val worstDay = dailyDoneCounts.minByOrNull { it.value }?.key ?: start

        // Focus time
        val focusSeconds = pomodoroDao.getFocusSecondsInRange(start, end) ?: 0

        // Money saved this week
        val badHabits = badHabitDao.getAllActive()
        var moneySaved = 0
        for (habit in badHabits) {
            val resisted = badHabitLogDao.getTotalOccurrencesResisted(habit.id) ?: 0
            moneySaved += resisted * habit.costPerOccurrence
        }

        // Streak
        val bestStreak = habits.maxOfOrNull {
            habitLogDao.getStreakCount(it.id, toStr(LocalDate.now().minusDays(365)))
        } ?: 0

        return WeeklyReport(
            weekStart = start,
            weekEnd = end,
            totalHabits = habits.size,
            totalPossibleDays = totalPossibleDays,
            totalDoneDays = totalDoneDays,
            habitStats = habitStats,
            averageWaterMl = waterAvg,
            previousWeekWaterAvg = prevWaterAvg,
            bestDay = bestDay,
            worstDay = worstDay,
            bestStreak = bestStreak,
            totalWeeklyFocusSeconds = focusSeconds,
            totalMoneySavedThisWeek = moneySaved
        )
    }

    // ============ CHALLENGES ============
    suspend fun getAllChallenges() = challengeDao.getAllActive()

    suspend fun getChallengeProgress(challengeId: Long) = challengeProgressDao.getProgress(challengeId)

    suspend fun getAllProgress() = challengeProgressDao.getAllProgress()

    suspend fun getActiveProgress() = challengeProgressDao.getActiveProgress()

    suspend fun getCompletedProgress() = challengeProgressDao.getCompletedProgress()

    suspend fun joinChallenge(challengeId: Long) {
        val existing = challengeProgressDao.getProgress(challengeId)
        if (existing == null) {
            challengeProgressDao.insert(ChallengeProgress(
                challengeId = challengeId,
                startDate = today(),
                lastUpdateDate = today()
            ))
        }
    }

    suspend fun updateChallengeProgress(challengeId: Long): Boolean {
        val progress = challengeProgressDao.getProgress(challengeId) ?: return false
        val challenge = challengeDao.getById(challengeId) ?: return false
        val todayStr = toStr(today())

        if (progress.lastUpdateDate == todayStr) return true // already updated today

        val updatedDays = progress.currentDays + 1
        val completed = updatedDays >= challenge.targetDays
        challengeProgressDao.update(progress.copy(
            currentDays = updatedDays,
            lastUpdateDate = todayStr,
            completed = completed
        ))
        return true
    }

    suspend fun addDefaultChallenges() {
        val existing = challengeDao.getAll()
        if (existing.isNotEmpty()) return

        val defaults = listOf(
            Challenge(name = "Pemula 7 Hari", description = "Catat kebiasaan selama 7 hari berturut-turut", icon = "🌱", targetDays = 7),
            Challenge(name = "Konsisten 21 Hari", description = "Catat kebiasaan selama 21 hari berturut-turut", icon = "🌿", targetDays = 21),
            Challenge(name = "Master 30 Hari", description = "Catat kebiasaan selama 30 hari berturut-turut", icon = "🌳", targetDays = 30),
            Challenge(name = "Rajin Minum 7 Hari", description = "Minum air cukup selama 7 hari", icon = "💧", targetDays = 7, category = "water"),
            Challenge(name = "Fokus 7 Hari", description = "Selesaikan 1 sesi fokus setiap hari", icon = "🍅", targetDays = 7, category = "focus")
        )
        for (c in defaults) challengeDao.insert(c)
    }

    // ============ CLEAR ALL ============
    suspend fun clearAllData() {
        val habits = habitDao.getAll()
        for (h in habits) {
            habitLogDao.deleteByHabitId(h.id)
            habitDao.delete(h)
        }
        waterLogDao.deleteAll()
        val badHabits = badHabitDao.getAll()
        for (bh in badHabits) {
            badHabitLogDao.deleteByBadHabitId(bh.id)
            badHabitDao.delete(bh)
        }
        pomodoroDao.deleteAll()
        challengeProgressDao.deleteAll()
        challengeDao.deleteAll()
    }
}

data class HabitStat(
    val name: String,
    val doneDays: Int,
    val totalDays: Int
)

data class WeeklyReport(
    val weekStart: String,
    val weekEnd: String,
    val totalHabits: Int,
    val totalPossibleDays: Int,
    val totalDoneDays: Int,
    val habitStats: List<HabitStat>,
    val averageWaterMl: Double,
    val previousWeekWaterAvg: Double,
    val bestDay: String,
    val worstDay: String,
    val bestStreak: Int,
    val totalWeeklyFocusSeconds: Int,
    val totalMoneySavedThisWeek: Int
) {
    val consistencyPercent: Int
        get() = if (totalPossibleDays > 0) (totalDoneDays * 100 / totalPossibleDays) else 0
}