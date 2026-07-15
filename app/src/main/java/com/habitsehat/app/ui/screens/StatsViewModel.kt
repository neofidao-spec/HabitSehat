package com.habitsehat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StatsViewModel(private val repository: HabitRepository) : ViewModel() {

    data class DayData(
        val date: LocalDate,
        val habitsDone: Int,
        val habitsTotal: Int,
        val waterMl: Int
    )

    data class UiState(
        val weekData: List<DayData> = emptyList(),
        val monthData: List<DayData> = emptyList(),
        val bestStreak: Int = 0,
        val totalHabitsCreated: Int = 0,
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            val habits = repository.getAllHabits()
            val today = LocalDate.now()
            val displayFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

            // Last 7 days
            val weekData = (6 downTo 0).map { daysAgo ->
                val d = today.minusDays(daysAgo.toLong())
                var done = 0
                for (h in habits) {
                    val c = repository.getHabitCount(h.id, d)
                    if (c >= h.targetCount) done++
                }
                val w = repository.getWaterTotal(d)
                DayData(d, done, habits.size, w)
            }

            // Last 30 days
            val monthData = (29 downTo 0).map { daysAgo ->
                val d = today.minusDays(daysAgo.toLong())
                var done = 0
                for (h in habits) {
                    val c = repository.getHabitCount(h.id, d)
                    if (c >= h.targetCount) done++
                }
                val w = repository.getWaterTotal(d)
                DayData(d, done, habits.size, w)
            }

            // Best streak
            var best = 0
            var current = 0
            for (day in monthData.reversed()) {
                if (day.habitsDone == day.habitsTotal && day.habitsTotal > 0) {
                    current++
                    if (current > best) best = current
                } else {
                    current = 0
                }
            }

            _uiState.update {
                it.copy(
                    weekData = weekData,
                    monthData = monthData,
                    bestStreak = best,
                    totalHabitsCreated = habits.size,
                    isLoading = false
                )
            }
        }
    }
}