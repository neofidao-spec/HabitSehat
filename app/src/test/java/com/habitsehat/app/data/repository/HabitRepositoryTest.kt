package com.habitsehat.app.data.repository

import com.habitsehat.app.data.db.*
import com.habitsehat.app.data.model.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import java.time.LocalDate
import kotlin.test.assertEquals

class HabitRepositoryTest {
    @Mock private lateinit var habitDao: HabitDao
    @Mock private lateinit var habitLogDao: HabitLogDao
    @Mock private lateinit var waterLogDao: WaterLogDao
    @Mock private lateinit var badHabitDao: BadHabitDao
    @Mock private lateinit var badHabitLogDao: BadHabitLogDao
    @Mock private lateinit var pomodoroDao: PomodoroDao
    @Mock private lateinit var challengeDao: ChallengeDao
    @Mock private lateinit var challengeProgressDao: ChallengeProgressDao

    private lateinit var repository: HabitRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = HabitRepository(
            habitDao, habitLogDao, waterLogDao, badHabitDao,
            badHabitLogDao, pomodoroDao, challengeDao, challengeProgressDao
        )
    }

    @Test
    fun checkHabit_shouldInsertHabitLog() = runBlocking {
        val habitId = 1L
        val today = LocalDate.now()

        repository.checkHabit(habitId, today)

        org.mockito.kotlin.verify(habitLogDao).insert(
            org.mockito.kotlin.argThat {
                it.habitId == habitId && it.date == today
            }
        )
    }

    @Test
    fun getHabitCount_shouldReturnCount() = runBlocking {
        val habitId = 1L
        val today = LocalDate.now()
        whenever(habitLogDao.getTotalCount(habitId, today)).thenReturn(2)

        val count = repository.getHabitCount(habitId, today)

        assertEquals(2, count)
    }

    @Test
    fun addWater_shouldInsertWaterLog() = runBlocking {
        val amount = 250
        val today = LocalDate.now()

        repository.addWater(amount, today)

        org.mockito.kotlin.verify(waterLogDao).insert(
            org.mockito.kotlin.argThat {
                it.amountMl == amount && it.date == today
            }
        )
    }

    @Test
    fun getAllHabits_shouldReturnHabits() = runBlocking {
        val habits = listOf(
            Habit(id = 1, name = "Exercise"),
            Habit(id = 2, name = "Read")
        )
        whenever(habitDao.getAllActive()).thenReturn(habits)

        val result = repository.getAllHabits()

        assertEquals(2, result.size)
        assertEquals("Exercise", result[0].name)
    }
}