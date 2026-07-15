package com.habitsehat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitsehat.app.data.model.Expense
import com.habitsehat.app.data.model.ExpenseCategory
import com.habitsehat.app.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExpenseViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val categories = repository.getAllExpenseCategories()
            val todayExpenses = repository.getExpensesByDate(LocalDate.now())
            val todayTotal = repository.getExpenseTotalByDate(LocalDate.now())
            _uiState.value = _uiState.value.copy(
                categories = categories,
                todayExpenses = todayExpenses,
                todayTotal = todayTotal,
                isLoading = false
            )
        }
    }

    fun refresh() {
        loadData()
    }

    data class UiState(
        val categories: List<ExpenseCategory> = emptyList(),
        val todayExpenses: List<Expense> = emptyList(),
        val todayTotal: Long = 0,
        val isLoading: Boolean = true
    )
}