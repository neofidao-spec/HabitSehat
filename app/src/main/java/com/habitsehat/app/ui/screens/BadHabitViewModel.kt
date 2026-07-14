package com.habitsehat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitsehat.app.data.model.BadHabit
import com.habitsehat.app.data.model.BadHabitWithStats
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BadHabitViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBadHabits()
    }

    private fun loadBadHabits() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val habits = repository.getAllBadHabits()
            val withStats = mutableListOf<BadHabitWithStats>()

            for (h in habits) {
                val (totalResisted, totalDays) = repository.getBadHabitStats(h.id)
                val streak = repository.getBadHabitResistedStreak(h.id, "")
                val moneySaved = repository.getMoneySaved(h)
                val lastResisted = repository.getLastResistedDate(h.id)
                withStats.add(BadHabitWithStats(
                    badHabit = h,
                    currentStreak = streak,
                    longestStreak = streak,
                    totalDaysResisted = totalDays,
                    totalMoneySaved = moneySaved,
                    totalOccurrencesResisted = totalResisted,
                    lastResistedDate = lastResisted
                ))
            }

            _uiState.update { it.copy(badHabits = withStats, isLoading = false) }
        }
    }

    fun addBadHabit(badHabit: BadHabit) {
        viewModelScope.launch {
            repository.addBadHabit(badHabit)
            loadBadHabits()
        }
    }

    fun updateBadHabit(badHabit: BadHabit) {
        viewModelScope.launch {
            repository.updateBadHabit(badHabit)
            loadBadHabits()
        }
    }

    fun deactivateBadHabit(id: Long) {
        viewModelScope.launch {
            repository.deactivateBadHabit(id)
            loadBadHabits()
        }
    }

    fun resistBadHabit(badHabitId: Long) {
        viewModelScope.launch {
            repository.resistBadHabit(badHabitId)
            loadBadHabits()
        }
    }

    fun giveInBadHabit(badHabitId: Long) {
        viewModelScope.launch {
            repository.giveInBadHabit(badHabitId)
            loadBadHabits()
        }
    }

    data class UiState(
        val badHabits: List<BadHabitWithStats> = emptyList(),
        val isLoading: Boolean = true
    )
}