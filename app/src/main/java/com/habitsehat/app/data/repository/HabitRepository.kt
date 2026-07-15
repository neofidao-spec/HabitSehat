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
    private val challengeProgressDao: ChallengeProgressDao,
    private val expenseDao: ExpenseDao,
    private val expenseCategoryDao: ExpenseCategoryDao
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

        var totalDoneDays = 0
        var totalPossibleDays = 0
        val habitStats = mutableListOf<HabitStat>()

        var currentDay = monday
        val dailyDoneCounts = mutableMapOf<String, Int>()
        while (!currentDay.isAfter(sunday)) {
            val dateStr = toStr(currentDay)
            var done = 0
            for (habit in habits) {
                if (isHabitChecked(habit.id, currentDay)) done++
            }
            dailyDoneCounts[dateStr] = done
            totalPossibleDays++
            if (done == habits.size && habits.isNotEmpty()) totalDoneDays++
            currentDay = currentDay.plusDays(1)
        }

        for (habit in habits) {
            var habitDoneDays = 0
            currentDay = monday
            while (!currentDay.isAfter(sunday)) {
                if (isHabitChecked(habit.id, currentDay)) habitDoneDays++
                currentDay = currentDay.plusDays(1)
            }
            habitStats.add(HabitStat(habit.name, habitDoneDays, 7))
        }

        val waterAvg = waterLogDao.getAverageInRange(start, end) ?: 0.0
        val prevWaterAvg = waterLogDao.getAverageInRange(previousWeekStart, previousWeekEnd) ?: 0.0

        val bestDay = dailyDoneCounts.maxByOrNull { it.value }?.key ?: start
        val worstDay = dailyDoneCounts.minByOrNull { it.value }?.key ?: start

        val focusSeconds = pomodoroDao.getFocusSecondsInRange(start, end) ?: 0

        val badHabits = badHabitDao.getAllActive()
        var moneySaved = 0
        for (habit in badHabits) {
            val resisted = badHabitLogDao.getTotalOccurrencesResisted(habit.id) ?: 0
            moneySaved += resisted * habit.costPerOccurrence
        }

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

        if (toStr(progress.lastUpdateDate) == todayStr) return true

        val updatedDays = progress.currentDays + 1
        val completed = updatedDays >= challenge.targetDays
        challengeProgressDao.update(progress.copy(
            currentDays = updatedDays,
            lastUpdateDate = today(),
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
        expenseDao.deleteAll()
        expenseCategoryDao.deleteAll()
    }

    // ============ EXPENSE TRACKING ============
    suspend fun addExpense(expense: Expense) = expenseDao.insert(expense)

    suspend fun updateExpense(expense: Expense) = expenseDao.update(expense)

    suspend fun deleteExpense(expense: Expense) = expenseDao.delete(expense)

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
            ExpenseCategory(name = "Belanja", icon = "🛍️", colorHex = "#9C27B0", isDefault = true, sortOrder = 3),
            ExpenseCategory(name = "Hiburan", icon = "🎮", colorHex = "#E91E63", isDefault = true, sortOrder = 4),
            ExpenseCategory(name = "Kesehatan", icon = "💊", colorHex = "#4CAF50", isDefault = true, sortOrder = 5),
            ExpenseCategory(name = "Pendidikan", icon = "📚", colorHex = "#673AB7", isDefault = true, sortOrder = 6),
            ExpenseCategory(name = "Seminar/Bootcamp", icon = "🎓", colorHex = "#3F51B5", isDefault = true, sortOrder = 7),
            ExpenseCategory(name = "Menabung/Investasi", icon = "💰", colorHex = "#009688", isDefault = true, sortOrder = 8),
            ExpenseCategory(name = "Lainnya", icon = "📦", colorHex = "#607D8B", isDefault = true, sortOrder = 9)
        )
        for (c in defaults) expenseCategoryDao.insert(c)
    }

    // Weekly expense recap
    suspend fun generateWeeklyExpenseReport(): WeeklyExpenseReport {
        val now = LocalDate.now()
        val monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val sunday = monday.plusDays(6)

        val expenses = getExpensesWithCategory(monday, sunday)
        val total = getExpenseTotalInRange(monday, sunday)
        val byCategory = getExpenseTotalsByCategory(monday, sunday)

        var dailyTotals = mutableMapOf<String, Long>()
        var currentDay = monday
        while (!currentDay.isAfter(sunday)) {
            val dayTotal = getExpenseTotalByDate(currentDay)
            dailyTotals[toStr(currentDay)] = dayTotal
            currentDay = currentDay.plusDays(1)
        }

        return WeeklyExpenseReport(
            weekStart = toStr(monday),
            weekEnd = toStr(sunday),
            totalExpenses = total,
            dailyTotals = dailyTotals,
            categoryTotals = byCategory,
            expenseItems = expenses
        )
    }

    // Monthly expense recap
    suspend fun generateMonthlyExpenseReport(): MonthlyExpenseReport {
        val now = LocalDate.now()
        val firstDay = now.withDayOfMonth(1)
        val lastDay = now.with(TemporalAdjusters.lastDayOfMonth())

        val total = getExpenseTotalInRange(firstDay, lastDay)
        val byCategory = getExpenseTotalsByCategory(firstDay, lastDay)
        val expenses = getExpensesWithCategory(firstDay, lastDay)

        var weeklyBreakdown = mutableListOf<WeeklyExpenseReport>()
        var weekStart = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        if (weekStart.isBefore(firstDay)) weekStart = weekStart.plusDays(7)

        while (weekStart.isBefore(lastDay) || weekStart.isEqual(lastDay)) {
            val weekEnd = weekStart.plusDays(6)
            val actualWeekEnd = if (weekEnd.isAfter(lastDay)) lastDay else weekEnd
            val weekTotal = getExpenseTotalInRange(weekStart, actualWeekEnd)
            val weekCat = getExpenseTotalsByCategory(weekStart, actualWeekEnd)
            weeklyBreakdown.add(WeeklyExpenseReport(
                weekStart = toStr(weekStart),
                weekEnd = toStr(actualWeekEnd),
                totalExpenses = weekTotal,
                dailyTotals = mutableMapOf(),
                categoryTotals = weekCat,
                expenseItems = getExpensesWithCategory(weekStart, actualWeekEnd)
            ))
            weekStart = weekStart.plusDays(7)
        }

        return MonthlyExpenseReport(
            monthStart = toStr(firstDay),
            monthEnd = toStr(lastDay),
            totalExpenses = total,
            categoryTotals = byCategory,
            weeklyBreakdown = weeklyBreakdown,
            allExpenses = expenses
        )
    }
}

data class WeeklyExpenseReport(
    val weekStart: String,
    val weekEnd: String,
    val totalExpenses: Long,
    val dailyTotals: Map<String, Long>,
    val categoryTotals: List<CategoryTotal>,
    val expenseItems: List<ExpenseWithCategory>
)

data class MonthlyExpenseReport(
    val monthStart: String,
    val monthEnd: String,
    val totalExpenses: Long,
    val categoryTotals: List<CategoryTotal>,
    val weeklyBreakdown: List<WeeklyExpenseReport>,
    val allExpenses: List<ExpenseWithCategory>
)
