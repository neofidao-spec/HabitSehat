package com.habitsehat.app.data.repository

import com.habitsehat.app.data.db.AppDatabase
import com.habitsehat.app.data.db.CategoryTotal
import com.habitsehat.app.data.db.ExpenseWithCategory
import com.habitsehat.app.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HabitRepository(private val db: AppDatabase) {

    private val habitDao = db.habitDao()
    private val habitLogDao = db.habitLogDao()
    private val waterLogDao = db.waterLogDao()
    private val badHabitDao = db.badHabitDao()
    private val badHabitLogDao = db.badHabitLogDao()
    private val expenseDao = db.expenseDao()
    private val expenseCategoryDao = db.expenseCategoryDao()
    private val challengeDao = db.challengeDao()
    private val challengeProgressDao = db.challengeProgressDao()
    private val pomodoroDao = db.pomodoroDao()

    private val isoDateFormat = DateTimeFormatter.ISO_LOCAL_DATE

    private fun toStr(date: LocalDate) = date.format(isoDateFormat)

    private fun today() = LocalDate.now()

    // ============ HABITS ============
    suspend fun getAllHabits() = habitDao.getAllActive()
    suspend fun addHabit(habit: Habit) = habitDao.insert(habit)
    suspend fun updateHabit(habit: Habit) = habitDao.update(habit)
    suspend fun getHabitById(id: Long): Habit? = habitDao.getById(id)
    suspend fun archiveHabit(id: Long) = habitDao.archive(id)
    suspend fun restoreHabit(id: Long) = habitDao.restore(id)
    suspend fun getArchivedHabits() = habitDao.getAllArchived()
    suspend fun deleteHabit(habit: Habit) {
        habitLogDao.deleteByHabitId(habit.id)
        habitDao.delete(habit)
    }

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
            badHabitLogDao.insert(BadHabitLog(badHabitId = badHabitId, date = dateStr, resistedCount = 1))
        }
    }

    suspend fun giveInBadHabit(badHabitId: Long, date: LocalDate = today()) {
        val dateStr = toStr(date)
        val existingLogs = badHabitLogDao.getLogs(badHabitId, dateStr)
        if (existingLogs.isNotEmpty()) {
            val log = existingLogs[0].copy(gaveInCount = existingLogs[0].gaveInCount + 1)
            badHabitLogDao.update(log)
        } else {
            badHabitLogDao.insert(BadHabitLog(badHabitId = badHabitId, date = dateStr, gaveInCount = 1))
        }
    }

    suspend fun getTotalDaysResisted(badHabitId: Long): Int {
        return badHabitLogDao.getTotalDaysResisted(badHabitId)
    }

    suspend fun getTotalMoneySaved(badHabitId: Long): Long {
        return badHabitLogDao.getTotalOccurrencesResisted(badHabitId) ?: 0L
    }

    suspend fun getResistedStreak(badHabitId: Long, since: LocalDate): Int {
        return badHabitLogDao.getResistedStreak(badHabitId, toStr(since))
    }

    suspend fun getLastResistedDate(badHabitId: Long): LocalDate? {
        val dateStr = badHabitLogDao.getLastResistedDate(badHabitId)
        return dateStr?.let { LocalDate.parse(it) }
    }

    // ============ EXPENSE TRACKING ============
    suspend fun addExpense(expense: Expense) = expenseDao.insert(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.update(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.delete(expense)

    suspend fun getExpenseById(id: Long): Expense? = expenseDao.getById(id)

    suspend fun getExpensesByDate(date: LocalDate): List<Expense> = expenseDao.getExpensesByDate(toStr(date))

    suspend fun getExpensesInRange(start: LocalDate, end: LocalDate): List<Expense> = expenseDao.getExpensesInRange(toStr(start), toStr(end))

    suspend fun getExpenseTotalByDate(date: LocalDate): Long = expenseDao.getTotalByDate(toStr(date)) ?: 0

    suspend fun getExpenseTotalInRange(start: LocalDate, end: LocalDate): Long = expenseDao.getTotalInRange(toStr(start), toStr(end)) ?: 0

    suspend fun getExpensesWithCategory(start: LocalDate, end: LocalDate): List<ExpenseWithCategory> = expenseDao.getExpensesWithCategory(toStr(start), toStr(end))

    suspend fun getExpenseTotalsByCategory(start: LocalDate, end: LocalDate): List<CategoryTotal> = expenseDao.getTotalsByCategory(toStr(start), toStr(end))

    // Expense Categories
    suspend fun getAllExpenseCategories(): List<ExpenseCategory> = expenseCategoryDao.getAll()

    suspend fun getDefaultExpenseCategories(): List<ExpenseCategory> = expenseCategoryDao.getDefaults()

    suspend fun addExpenseCategory(category: ExpenseCategory) = expenseCategoryDao.insert(category)

    suspend fun updateExpenseCategory(category: ExpenseCategory) = expenseCategoryDao.update(category)

    suspend fun deleteExpenseCategory(category: ExpenseCategory) = expenseCategoryDao.delete(category)

    suspend fun getExpenseCategoryById(id: Long) = expenseCategoryDao.getById(id)

    // Default categories for first run
    suspend fun addDefaultExpenseCategories() {
        val existing = expenseCategoryDao.getAll()
        if (existing.isNotEmpty()) return

        val defaults = listOf(
            ExpenseCategory(name = "Makan & Minum", icon = "🍽️", colorHex = "#FF9800", isDefault = true, sortOrder = 1),
            ExpenseCategory(name = "Transport", icon = "🚌", colorHex = "#2196F3", isDefault = true, sortOrder = 2),
            ExpenseCategory(name = "Belanja", icon = "🛒", colorHex = "#4CAF50", isDefault = true, sortOrder = 3),
            ExpenseCategory(name = "Hiburan", icon = "🎮", colorHex = "#9C27B0", isDefault = true, sortOrder = 4),
            ExpenseCategory(name = "Kesehatan", icon = "💊", colorHex = "#F44336", isDefault = true, sortOrder = 5),
            ExpenseCategory(name = "Lainnya", icon = "📦", colorHex = "#607D8B", isDefault = true, sortOrder = 6),
        )
        for (c in defaults) expenseCategoryDao.insert(c)
    }

    // Weekly expense recap
    suspend fun generateWeeklyExpenseReport(): WeeklyExpenseReport {
        val now = LocalDate.now()
        val weekStart = now.minusDays(now.dayOfWeek.value - 1) // Monday
        val weekEnd = weekStart.plusDays(6)

        val dailyTotals = mutableListOf<DailyExpenseSummary>()
        var current = weekStart
        while (current <= weekEnd) {
            val total = getExpenseTotalByDate(current)
            dailyTotals.add(DailyExpenseSummary(current, total))
            current = current.plusDays(1)
        }

        val categoryTotals = getExpenseTotalsByCategory(weekStart, weekEnd)
        val weeklyTotal = dailyTotals.sumOf { it.total }

        return WeeklyExpenseReport(
            weekStart = weekStart,
            weekEnd = weekEnd,
            dailyTotals = dailyTotals,
            categoryTotals = categoryTotals,
            weeklyTotal = weeklyTotal
        )
    }

    // Comprehensive weekly report
    suspend fun generateWeeklyReport(): WeeklyReport {
        val now = LocalDate.now()
        val weekStart = now.minusDays(now.dayOfWeek.value - 1) // Monday
        val weekEnd = weekStart.plusDays(6)

        // Habit stats
        val habits = getAllHabits()
        val habitStats = mutableListOf<HabitStat>()
        var totalDone = 0
        var totalPossible = 0
        var bestStreak = 0

        for (habit in habits) {
            var doneDays = 0
            var possibleDays = 0
            var currentStreak = 0

            var current = weekStart
            while (current <= weekEnd) {
                possibleDays++
                if (isHabitChecked(habit.id, current)) {
                    doneDays++
                    totalDone++
                }
                totalPossible++
                current = current.plusDays(1)
            }

            habitStats.add(HabitStat(habit.name, doneDays, possibleDays))

            // Calculate streak for this habit
            val streak = getStreak(habit.id, weekStart.minusDays(365))
            if (streak > bestStreak) bestStreak = streak
        }

        val consistencyPercent = if (totalPossible > 0) (totalDone * 100 / totalPossible) else 0

        // Water average
        var waterTotal = 0
        var currentDate = weekStart
        while (currentDate <= weekEnd) {
            waterTotal += getWaterTotal(currentDate)
            currentDate = currentDate.plusDays(1)
        }
        val averageWaterMl = waterTotal.toFloat() / 7

        // Focus time
        val totalWeeklyFocusSeconds = getWeeklyFocusSeconds(weekStart.minusDays(6)) ?: 0

        // Money saved (bad habits)
        val badHabits = getAllBadHabits()
        var totalMoneySavedThisWeek = 0L
        for (badHabit in badHabits) {
            totalMoneySavedThisWeek += getTotalMoneySaved(badHabit.id)
        }

        // Best and worst day
        var bestDay = weekStart
        var worstDay = weekStart
        var bestDayDone = -1
        var worstDayDone = Int.MAX_VALUE

        currentDate = weekStart
        while (currentDate <= weekEnd) {
            var dayDone = 0
            for (habit in habits) {
                if (isHabitChecked(habit.id, currentDate)) dayDone++
            }
            if (dayDone > bestDayDone) {
                bestDayDone = dayDone
                bestDay = currentDate
            }
            if (dayDone < worstDayDone) {
                worstDayDone = dayDone
                worstDay = currentDate
            }
            currentDate = currentDate.plusDays(1)
        }

        return WeeklyReport(
            weekStart = weekStart.toString(),
            weekEnd = weekEnd.toString(),
            consistencyPercent = consistencyPercent,
            bestStreak = bestStreak,
            averageWaterMl = averageWaterMl,
            totalWeeklyFocusSeconds = totalWeeklyFocusSeconds,
            totalMoneySavedThisWeek = totalMoneySavedThisWeek,
            habitStats = habitStats,
            bestDay = bestDay.toString(),
            worstDay = worstDay.toString()
        )
    }

    // ============ CHALLENGES ============
    suspend fun getAllChallenges() = challengeDao.getAll()
    suspend fun getActiveChallenges() = challengeDao.getAllActive()

    suspend fun addChallenge(challenge: Challenge) = challengeDao.insert(challenge)

    suspend fun updateChallenge(challenge: Challenge) = challengeDao.update(challenge)

    suspend fun deleteChallenge(challenge: Challenge) = challengeDao.delete(challenge)

    suspend fun getChallengeById(id: Long): Challenge? = challengeDao.getById(id)

    suspend fun getChallengeProgress(challengeId: Long): ChallengeProgress? = challengeProgressDao.getProgress(challengeId)

    suspend fun getAllChallengeProgress() = challengeProgressDao.getAllProgress()

    suspend fun getActiveChallengeProgress() = challengeProgressDao.getActiveProgress()

    suspend fun getCompletedChallengeProgress() = challengeProgressDao.getCompletedProgress()

    suspend fun startChallenge(challengeId: Long, startDate: LocalDate = today()) {
        val progress = ChallengeProgress(
            challengeId = challengeId,
            startDate = startDate,
            lastUpdateDate = startDate,
            completed = false
        )
        challengeProgressDao.insert(progress)
    }

    suspend fun updateChallengeProgress(challengeId: Long, currentDays: Int, completed: Boolean = false) {
        val progress = challengeProgressDao.getProgress(challengeId)
        if (progress != null) {
            val updated = progress.copy(
                currentDays = currentDays,
                lastUpdateDate = today(),
                completed = completed
            )
            challengeProgressDao.update(updated)
        }
    }

    suspend fun autoUpdateChallenges() {
        val challenges = challengeDao.getAll()
        val today = LocalDate.now()

        for (challenge in challenges) {
            val progress = challengeProgressDao.getProgress(challenge.id)
            if (progress != null && !progress.completed) {
                var currentDays = 0
                when (challenge.category) {
                    "water" -> {
                        val waterTotal = getWaterTotal(today)
                        if (waterTotal >= 2500) {
                            currentDays = progress.currentDays + 1
                        } else {
                            currentDays = 0
                        }
                    }
                    "focus" -> {
                        val focusSessions = pomodoroDao.getSessionCount(toStr(today))
                        if (focusSessions >= 1) {
                            currentDays = progress.currentDays + 1
                        } else {
                            currentDays = 0
                        }
                    }
                    else -> {
                        val habits = habitDao.getAllActive()
                        var allDone = true
                        for (h in habits) {
                            if (!isHabitChecked(h.id, today)) {
                                allDone = false
                                break
                            }
                        }
                        if (allDone && habits.isNotEmpty()) {
                            currentDays = progress.currentDays + 1
                        } else {
                            currentDays = 0
                        }
                    }
                }

                val isCompleted = currentDays >= challenge.targetDays
                updateChallengeProgress(challenge.id, currentDays, isCompleted)
            }
        }
    }

    // ============ POMODORO ============
    suspend fun addPomodoroSession(session: PomodoroSession) = pomodoroDao.insert(session)

    suspend fun getTotalFocusSeconds(date: LocalDate = today()): Int = pomodoroDao.getTotalFocusSeconds(toStr(date)) ?: 0

    suspend fun getSessionCount(date: LocalDate = today()): Int = pomodoroDao.getSessionCount(toStr(date))

    suspend fun getWeeklyFocusSeconds(since: LocalDate): Int = pomodoroDao.getWeeklyFocusSeconds(toStr(since)) ?: 0

    suspend fun getFocusSecondsInRange(start: LocalDate, end: LocalDate): Int = pomodoroDao.getFocusSecondsInRange(toStr(start), toStr(end)) ?: 0

    suspend fun getRecentSessions(): List<PomodoroSession> = pomodoroDao.getRecentSessions()

    // ============ RESET ALL ============
    suspend fun deleteAllData() {
        habitLogDao.deleteAll()
        habitDao.deleteAll()
        waterLogDao.deleteAll()
        badHabitLogDao.deleteAll()
        badHabitDao.deleteAll()
        expenseDao.deleteAll()
        expenseCategoryDao.deleteAll()
        challengeProgressDao.deleteAll()
        challengeDao.deleteAll()
        pomodoroDao.deleteAll()
    }
}