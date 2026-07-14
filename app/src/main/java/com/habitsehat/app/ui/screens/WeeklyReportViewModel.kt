package com.habitsehat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitsehat.app.data.repository.HabitRepository
import com.habitsehat.app.data.repository.WeeklyReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeeklyReportUiState(
    val isLoading: Boolean = true,
    val report: WeeklyReport? = null,
    val error: String? = null
)

class WeeklyReportViewModel(
    private val repository: HabitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeklyReportUiState())
    val uiState: StateFlow<WeeklyReportUiState> = _uiState.asStateFlow()

    init {
        loadReport()
    }

    fun loadReport() {
        viewModelScope.launch {
            _uiState.value = WeeklyReportUiState(isLoading = true)
            try {
                val report = repository.generateWeeklyReport()
                _uiState.value = WeeklyReportUiState(isLoading = false, report = report)
            } catch (e: Exception) {
                _uiState.value = WeeklyReportUiState(isLoading = false, error = e.message)
            }
        }
    }
}
