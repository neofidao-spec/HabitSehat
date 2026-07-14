package com.habitsehat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StatsViewModel(private val repository: HabitRepository) : ViewModel() {

    data class DayData(
        val date: String,
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
            val today = java.time.LocalDate.now()
            val fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")

            // Last 7 days
            val weekData = (6 downTo 0).map { daysAgo ->
                val d = today.minusDays(daysAgo.toLong())
                val ds = d.format(fmt)
                var done = 0
                for (h in habits) {
                    val c = repository.getHabitCount(h.id, ds)
                    if (c >= h.targetCount) done++
                }
                val w = repository.getWaterTotal(ds)
                DayData(ds, done, habits.size, w)
            }

            // Last 30 days (simplified — just check if all habits done)
            val monthData = (29 downTo 0).map { daysAgo ->
                val d = today.minusDays(daysAgo.toLong())
                val ds = d.format(fmt)
                var done = 0
                for (h in habits) {
                    val c = repository.getHabitCount(h.id, ds)
                    if (c >= h.targetCount) done++
                }
                val w = repository.getWaterTotal(ds)
                DayData(ds, done, habits.size, w)
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
