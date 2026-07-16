package com.habitsehat.app.data.repository

import com.habitsehat.app.data.db.AppDatabase
import com.habitsehat.app.data.db.HabitDao
import com.habitsehat.app.data.db.HabitLogDao
import com.habitsehat.app.data.db.WaterLogDao
import com.habitsehat.app.data.model.Habit
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

class HabitRepositoryTest {
    @Mock private lateinit var db: AppDatabase
    @Mock private lateinit var habitDao: HabitDao
    @Mock private lateinit var habitLogDao: HabitLogDao
    @Mock private lateinit var waterLogDao: WaterLogDao

    private lateinit var repository: HabitRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        org.mockito.kotlin.whenever(db.habitDao()).thenReturn(habitDao)
        org.mockito.kotlin.whenever(db.habitLogDao()).thenReturn(habitLogDao)
        org.mockito.kotlin.whenever(db.waterLogDao()).thenReturn(waterLogDao)
        repository = HabitRepository(db)
    }

    @Test
    fun checkHabit_shouldInsertHabitLog() = runBlocking {
        val habitId = 1L
        val today = LocalDate.now()
        val todayStr = today.format(DateTimeFormatter.ISO_LOCAL_DATE)

        repository.checkHabit(habitId, today)

        verify(habitLogDao).insert(
            org.mockito.kotlin.argThat {
                it.habitId == habitId && it.date == todayStr
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
