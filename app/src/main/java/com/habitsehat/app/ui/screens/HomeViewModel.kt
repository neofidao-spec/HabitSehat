package com.habitsehat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HabitRepository) : ViewModel() {

    data class UiState(
        val habits: List<Habit> = emptyList(),
        val habitsDone: Int = 0,
        val habitsTotal: Int = 0,
        val waterTotal: Int = 0,
        val waterGoal: Int = 2500,
        val isLoading: Boolean = true
    )

    // Track per-habit checked state for today
    private val _checkedStates = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val checkedStates: StateFlow<Map<Long, Boolean>> = _checkedStates.asStateFlow()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val habits = repository.getAllHabits()
            val water = repository.getWaterTotal()
            val todayStr = java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            )

            val checked = mutableMapOf<Long, Boolean>()
            var done = 0
            for (h in habits) {
                val count = repository.getHabitCount(h.id, todayStr)
                val isDone = count >= h.targetCount
                checked[h.id] = isDone
                if (isDone) done++
            }
            _checkedStates.value = checked

            _uiState.update {
                it.copy(
                    habits = habits,
                    habitsDone = done,
                    habitsTotal = habits.size,
                    waterTotal = water,
                    isLoading = false
                )
            }
        }
    }

    fun saveHabit(habit: Habit) {
        viewModelScope.launch {
            repository.addHabit(habit)
            refresh()
        }
    }

    fun checkHabit(habitId: Long) {
        viewModelScope.launch {
            repository.checkHabit(habitId)
            refresh()
        }
    }

    fun uncheckHabit(habitId: Long) {
        viewModelScope.launch {
            repository.uncheckHabit(habitId)
            refresh()
        }
    }

    fun toggleHabit(habitId: Long) {
        viewModelScope.launch {
            val todayStr = java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            )
            val currentlyDone = repository.getHabitCount(habitId, todayStr) > 0
            if (currentlyDone) {
                repository.uncheckHabit(habitId)
            } else {
                repository.checkHabit(habitId)
            }
            refresh()
        }
    }

    fun addWater(amountMl: Int = 200) {
        viewModelScope.launch {
            repository.addWater(amountMl)
            refresh()
        }
    }

    fun undoWater() {
        viewModelScope.launch {
            repository.undoWater()
            refresh()
        }
    }

    fun archiveHabit(habitId: Long) {
        viewModelScope.launch {
            repository.archiveHabit(habitId)
            refresh()
        }
    }
}
