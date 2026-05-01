package com.flowledger.app.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowledger.app.data.local.dao.CategorySum
import com.flowledger.app.data.local.dao.DailySummary
import com.flowledger.app.data.repository.TransactionRepository
import com.flowledger.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatisticsState(
    val dailySummaries: List<DailySummary> = emptyList(),
    val categorySums: List<CategorySum> = emptyList(),
    val totalIncome: Long = 0,
    val totalExpense: Long = 0,
    val startDate: Long = DateUtils.currentMonthStart(),
    val endDate: Long = DateUtils.currentMonthEnd(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData(startDate: Long = DateUtils.currentMonthStart(), endDate: Long = DateUtils.currentMonthEnd()) {
        _state.update { it.copy(startDate = startDate, endDate = endDate, isLoading = true) }
        viewModelScope.launch {
            val income = transactionRepo.getTotalIncome(startDate, endDate)
            val expense = transactionRepo.getTotalExpense(startDate, endDate)
            _state.update { it.copy(totalIncome = income, totalExpense = expense) }
        }
        viewModelScope.launch {
            val daily = transactionRepo.getDailySummary(startDate, endDate)
            _state.update { it.copy(dailySummaries = daily) }
        }
        viewModelScope.launch {
            val category = transactionRepo.getExpenseByCategory(startDate, endDate)
            _state.update { it.copy(categorySums = category, isLoading = false) }
        }
    }

    fun loadCurrentMonth() {
        val today = DateUtils.today()
        loadData(
            startDate = DateUtils.monthStart(today.year, today.monthValue),
            endDate = DateUtils.monthEnd(today.year, today.monthValue)
        )
    }

    fun loadPreviousMonth() {
        val today = DateUtils.today()
        val prevMonth = today.minusMonths(1)
        loadData(
            startDate = DateUtils.monthStart(prevMonth.year, prevMonth.monthValue),
            endDate = DateUtils.monthEnd(prevMonth.year, prevMonth.monthValue)
        )
    }
}
