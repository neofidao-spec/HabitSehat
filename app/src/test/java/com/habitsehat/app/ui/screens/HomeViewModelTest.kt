package com.habitsehat.app.ui.screens

import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HomeViewModelTest {
    @Mock private lateinit var repository: HabitRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = HomeViewModel(repository)
    }

    @Test
    fun initialState_shouldHaveDefaultValues() {
        val state = viewModel.uiState.value
        assertEquals(0, state.habitsTotal)
        assertEquals(0, state.habitsDone)
        assertEquals(0, state.waterTotal)
        assertEquals(2500, state.waterGoal)
    }

    @Test
    fun refresh_withHabits_shouldUpdateState() = runBlocking {
        val habits = listOf(
            Habit(id = 1, name = "Exercise"),
            Habit(id = 2, name = "Read")
        )
        whenever(repository.getAllHabits()).thenReturn(habits)
        whenever(repository.getWaterTotal()).thenReturn(500)

        viewModel.refresh()

        val state = viewModel.uiState.value
        assertEquals(2, state.habitsTotal)
        assertEquals(false, state.isLoading)
        assertEquals(500, state.waterTotal)
    }

    @Test
    fun saveHabit_shouldCallRepository() = runBlocking {
        val habit = Habit(name = "Meditate")
        viewModel.saveHabit(habit)

        org.mockito.kotlin.verify(repository).addHabit(habit)
    }

    @Test
    fun addWater_shouldCallRepository() = runBlocking {
        val amount = 250
        viewModel.addWater(amount)

        org.mockito.kotlin.verify(repository).addWater(amount)
    }

    @Test
    fun error_shouldBeClearedOnRefresh() = runBlocking {
        whenever(repository.getAllHabits()).thenReturn(emptyList())
        whenever(repository.getWaterTotal()).thenReturn(0)

        viewModel.refresh()

        val error = viewModel.uiState.value.error
        assertEquals(null, error)
    }

    private fun assertEquals(expected: Any?, actual: Any?) {
        if (expected != actual) throw AssertionError("Expected $expected but was $actual")
    }
}