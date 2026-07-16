package com.habitsehat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(private val repository: HabitRepository) : ViewModel() {

    data class UiState(
        val habits: List<Habit> = emptyList(),
        val habitsDone: Int = 0,
        val habitsTotal: Int = 0,
        val waterTotal: Int = 0,
        val waterGoal: Int = 2500,
        val archivedCount: Int = 0,
        val isLoading: Boolean = true,
        val error: String? = null
    )

    // Track per-habit checked state and count for today
    private val _checkedStates = MutableStateFlow<Map<Long, Boolean>>(emptyMap())
    val checkedStates: StateFlow<Map<Long, Boolean>> = _checkedStates.asStateFlow()

    private val _habitCounts = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val habitCounts: StateFlow<Map<Long, Int>> = _habitCounts.asStateFlow()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val habits = repository.getAllHabits()
                val water = repository.getWaterTotal()
                val archived = repository.getArchivedHabits()
                val today = LocalDate.now()

                val checked = mutableMapOf<Long, Boolean>()
                val counts = mutableMapOf<Long, Int>()
                var done = 0
                for (h in habits) {
                    val count = repository.getHabitCount(h.id, today)
                    counts[h.id] = count
                    val isDone = count >= h.targetCount
                    checked[h.id] = isDone
                    if (isDone) done++
                }
                _checkedStates.value = checked
                _habitCounts.value = counts

                _uiState.update {
                    it.copy(
                        habits = habits,
                        habitsDone = done,
                        habitsTotal = habits.size,
                        waterTotal = water,
                        archivedCount = archived.size,
                        isLoading = false
                    )
                }

                // Auto-update challenge progress
                repository.autoUpdateChallenges()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to load data",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun saveHabit(habit: Habit) {
        viewModelScope.launch {
            try {
                repository.addHabit(habit)
                refresh()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to save habit: ${e.message}")
                }
            }
        }
    }

    fun toggleHabit(habitId: Long) {
        val today = LocalDate.now()
        val wasChecked = _checkedStates.value[habitId] ?: false
        val oldCount = _habitCounts.value[habitId] ?: 0
        val target = _uiState.value.habits.find { it.id == habitId }?.targetCount ?: 1

        // Optimistic update: flip UI state immediately
        _checkedStates.update { it + (habitId to !wasChecked) }
        val newCount = if (wasChecked) 0 else target
        _habitCounts.update { it + (habitId to newCount) }
        _uiState.update { s ->
            s.copy(habitsDone = (s.habitsDone + if (wasChecked) -1 else 1).coerceIn(0, s.habitsTotal))
        }

        viewModelScope.launch {
            try {
                if (wasChecked) {
                    repository.uncheckHabit(habitId, today)
                } else {
                    repository.checkHabit(habitId, today)
                }
                refresh() // silent reconcile with DB
            } catch (e: Exception) {
                // Revert on failure
                _checkedStates.update { it + (habitId to wasChecked) }
                _habitCounts.update { it + (habitId to oldCount) }
                _uiState.update { s -> s.copy(error = "Gagal: ${e.message}") }
                refresh()
            }
        }
    }

    fun addWater(amountMl: Int = 200) {
        val oldTotal = _uiState.value.waterTotal
        _uiState.update { it.copy(waterTotal = it.waterTotal + amountMl) }

        viewModelScope.launch {
            try {
                repository.addWater(amountMl)
                refresh()
            } catch (e: Exception) {
                _uiState.update { s -> s.copy(waterTotal = oldTotal, error = "Gagal: ${e.message}") }
                refresh()
            }
        }
    }

    fun undoWater() {
        viewModelScope.launch {
            try {
                repository.undoWater()
                refresh()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to undo water: ${e.message}")
                }
            }
        }
    }

    fun archiveHabit(habitId: Long) {
        viewModelScope.launch {
            try {
                repository.archiveHabit(habitId)
                refresh()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to archive habit: ${e.message}")
                }
            }
        }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch {
            try {
                val habit = _uiState.value.habits.find { it.id == habitId }
                if (habit != null) {
                    repository.deleteHabit(habit)
                    refresh()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Gagal hapus: ${e.message}") }
            }
        }
    }

    fun restoreArchivedHabit(habitId: Long) {
        viewModelScope.launch {
            try {
                repository.restoreHabit(habitId)
                refresh()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Gagal pulihkan: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}