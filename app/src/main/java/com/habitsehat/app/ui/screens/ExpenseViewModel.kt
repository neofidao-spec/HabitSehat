package com.habitsehat.app.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.habitsehat.app.data.model.Expense
import com.habitsehat.app.data.model.ExpenseCategory
import com.habitsehat.app.data.db.ExpenseWithCategory
import com.habitsehat.app.data.model.WeeklyExpenseReport
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
            repository.addDefaultExpenseCategories()
            val categories = repository.getAllExpenseCategories()
            val todayExpenses = repository.getExpensesWithCategory(LocalDate.now(), LocalDate.now())
            val todayTotal = repository.getExpenseTotalByDate(LocalDate.now())
            val weeklyReport = repository.generateWeeklyExpenseReport()
            _uiState.value = _uiState.value.copy(
                categories = categories,
                todayExpenses = todayExpenses,
                todayTotal = todayTotal,
                weeklyReport = weeklyReport,
                isLoading = false
            )
        }
    }

    fun addExpense(expense: Expense) {
        viewModelScope.launch {
            repository.addExpense(expense)
            loadData()
        }
    }

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
            loadData()
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            try {
                repository.deleteExpense(expense)
                loadData()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Gagal hapus: ${e.message}")
            }
        }
    }

    fun addCategory(category: ExpenseCategory) {
        viewModelScope.launch {
            repository.addExpenseCategory(category)
            loadData()
        }
    }

    fun deleteCategory(category: ExpenseCategory) {
        viewModelScope.launch {
            repository.deleteExpenseCategory(category)
            loadData()
        }
    }

    fun updateCategory(category: ExpenseCategory) {
        viewModelScope.launch {
            repository.updateExpenseCategory(category)
            loadData()
        }
    }

    fun refresh() {
        loadData()
    }

    data class UiState(
        val categories: List<ExpenseCategory> = emptyList(),
        val todayExpenses: List<ExpenseWithCategory> = emptyList(),
        val todayTotal: Long = 0,
        val weeklyReport: WeeklyExpenseReport? = null,
        val isLoading: Boolean = true,
        val error: String? = null
    )

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
