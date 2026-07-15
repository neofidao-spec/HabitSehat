package com.habitsehat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitsehat.app.data.model.BadHabit
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class BadHabitViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBadHabits()
    }

    fun loadBadHabits() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val habits = repository.getAllBadHabits()
            val statsList = mutableListOf<BadHabitStat>()

            for (habit in habits) {
                val (totalResisted, totalDays) = repository.getBadHabitStats(habit.id)
                val moneySaved = repository.getMoneySaved(habit)
                val streak = repository.getBadHabitResistedStreak(habit.id, LocalDate.now().minusDays(365))
                val lastResisted = repository.getLastResistedDate(habit.id)

                statsList.add(BadHabitStat(
                    badHabit = habit,
                    currentStreak = streak,
                    totalDaysResisted = totalDays,
                    totalMoneySaved = moneySaved,
                    totalOccurrencesResisted = totalResisted,
                    lastResistedDate = lastResisted
                ))
            }

            _uiState.value = _uiState.value.copy(badHabits = statsList, isLoading = false)
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

    fun resist(badHabitId: Long) {
        viewModelScope.launch {
            repository.resistBadHabit(badHabitId)
            loadBadHabits()
        }
    }

    fun giveIn(badHabitId: Long) {
        viewModelScope.launch {
            repository.giveInBadHabit(badHabitId)
            loadBadHabits()
        }
    }

    data class UiState(
        val badHabits: List<BadHabitStat> = emptyList(),
        val isLoading: Boolean = true
    )

    data class BadHabitStat(
        val badHabit: BadHabit,
        val currentStreak: Int,
        val totalDaysResisted: Int,
        val totalMoneySaved: Int,
        val totalOccurrencesResisted: Int,
        val lastResistedDate: LocalDate?
    )
}