package com.habitsehat.app.ui.screens

import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @Mock private lateinit var repository: HabitRepository
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        whenever(repository.getAllHabits()).thenReturn(emptyList())
        whenever(repository.getWaterTotal()).thenReturn(0)
        whenever(repository.getArchivedHabits()).thenReturn(emptyList())
        viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_shouldHaveDefaultValues() = runTest {
        val state = viewModel.uiState.value
        assertEquals(0, state.habitsTotal)
        assertEquals(0, state.habitsDone)
        assertEquals(0, state.waterTotal)
        assertEquals(2500, state.waterGoal)
    }

    @Test
    fun refresh_withHabits_shouldUpdateState() = runTest {
        val habits = listOf(
            Habit(id = 1, name = "Exercise"),
            Habit(id = 2, name = "Read")
        )
        whenever(repository.getAllHabits()).thenReturn(habits)
        whenever(repository.getHabitCount(1L, java.time.LocalDate.now())).thenReturn(1)
        whenever(repository.getHabitCount(2L, java.time.LocalDate.now())).thenReturn(0)

        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(2, state.habitsTotal)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun saveHabit_shouldCallRepository() = runTest {
        val habit = Habit(name = "Meditate")
        viewModel.saveHabit(habit)
        testDispatcher.scheduler.advanceUntilIdle()

        org.mockito.kotlin.verify(repository).addHabit(habit)
    }

    @Test
    fun addWater_shouldCallRepository() = runTest {
        val amount = 250
        viewModel.addWater(amount)
        testDispatcher.scheduler.advanceUntilIdle()

        org.mockito.kotlin.verify(repository).addWater(amount)
    }

    @Test
    fun error_shouldBeClearedOnRefresh() = runTest {
        viewModel.refresh()
        testDispatcher.scheduler.advanceUntilIdle()

        val error = viewModel.uiState.value.error
        assertEquals(null, error)
    }

    private fun assertEquals(expected: Any?, actual: Any?) {
        if (expected != actual) throw AssertionError("Expected $expected but was $actual")
    }
}
