package com.habitsehat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitsehat.app.data.model.Habit
import com.habitsehat.app.data.model.PomodoroSession
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class PomodoroUiState(
    val selectedMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isFinished: Boolean = false,
    val selectedHabitId: Long? = null,
    val availableHabits: List<Habit> = emptyList(),
    val whiteNoiseEnabled: Boolean = false,
    val isPremium: Boolean = false,
    val totalTodaySeconds: Int = 0,
    val sessionCountToday: Int = 0
)

class PomodoroViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PomodoroUiState())
    val uiState: StateFlow<PomodoroUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private fun today() = LocalDate.now()

    init {
        loadHabits()
        loadTodayStats()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            val habits = repository.getAllHabits()
            _uiState.value = _uiState.value.copy(availableHabits = habits)
        }
    }

    private fun loadTodayStats() {
        viewModelScope.launch {
            val totalSec = repository.getTotalFocusSeconds()
            val count = repository.getSessionCount()
            _uiState.value = _uiState.value.copy(
                totalTodaySeconds = totalSec,
                sessionCountToday = count
            )
        }
    }

    fun selectDuration(minutes: Int) {
        if (_uiState.value.isRunning || _uiState.value.isPaused) return
        _uiState.value = _uiState.value.copy(
            selectedMinutes = minutes,
            remainingSeconds = minutes * 60,
            isFinished = false
        )
    }

    fun selectHabit(habitId: Long?) {
        _uiState.value = _uiState.value.copy(selectedHabitId = habitId)
    }

    fun toggleWhiteNoise() {
        val current = _uiState.value
        if (!current.isPremium) return
        _uiState.value = current.copy(whiteNoiseEnabled = !current.whiteNoiseEnabled)
    }

    fun setPremium(isPremium: Boolean) {
        _uiState.value = _uiState.value.copy(isPremium = isPremium)
        // Premium-only: mode 90 menit, white noise
        if (!isPremium && _uiState.value.selectedMinutes == 90) {
            selectDuration(50)
        }
    }

    fun start() {
        if (_uiState.value.isRunning) return
        _uiState.value = _uiState.value.copy(isRunning = true, isPaused = false, isFinished = false)
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && _uiState.value.isRunning) {
                delay(1000L)
                _uiState.value = _uiState.value.copy(
                    remainingSeconds = _uiState.value.remainingSeconds - 1
                )
            }
            if (_uiState.value.remainingSeconds <= 0) {
                finishSession()
            }
        }
    }

    fun pause() {
        if (!_uiState.value.isRunning) return
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(isRunning = false, isPaused = true)
    }

    fun resume() {
        if (!_uiState.value.isPaused) return
        start()
    }

    fun reset() {
        timerJob?.cancel()
        val mins = _uiState.value.selectedMinutes
        _uiState.value = _uiState.value.copy(
            remainingSeconds = mins * 60,
            isRunning = false,
            isPaused = false,
            isFinished = false
        )
    }

    private fun finishSession() {
        timerJob?.cancel()
        val state = _uiState.value
        val totalSeconds = state.selectedMinutes * 60
        viewModelScope.launch {
            repository.savePomodoroSession(
                PomodoroSession(
                    durationMinutes = state.selectedMinutes,
                    completedSeconds = totalSeconds,
                    habitId = state.selectedHabitId,
                    date = today()
                )
            )
            _uiState.value = _uiState.value.copy(
                isRunning = false,
                isFinished = true,
                whiteNoiseEnabled = false
            )
            loadTodayStats()
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
