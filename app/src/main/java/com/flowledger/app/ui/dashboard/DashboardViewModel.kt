package com.flowledger.app.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowledger.app.data.local.dao.DailySummary
import com.flowledger.app.data.local.entity.TransactionEntity
import com.flowledger.app.data.repository.TransactionRepository
import com.flowledger.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val monthIncome: Long = 0,
    val monthExpense: Long = 0,
    val monthBalance: Long = 0,
    val monthLabel: String = "",
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val pendingCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            val today = DateUtils.today()
            val startDate = DateUtils.monthStart(today.year, today.monthValue)
            val endDate = DateUtils.monthEnd(today.year, today.monthValue)

            val income = transactionRepo.getTotalIncome(startDate, endDate)
            val expense = transactionRepo.getTotalExpense(startDate, endDate)

            _state.update {
                it.copy(
                    monthIncome = income,
                    monthExpense = expense,
                    monthBalance = income - expense,
                    monthLabel = DateUtils.formatMonthYear(today.year, today.monthValue),
                    isLoading = false
                )
            }
        }

        viewModelScope.launch {
            transactionRepo.getByDateRange(
                DateUtils.currentMonthStart(),
                DateUtils.currentMonthEnd()
            ).collect { transactions ->
                _state.update {
                    it.copy(recentTransactions = transactions.take(10))
                }
            }
        }

        viewModelScope.launch {
            transactionRepo.getPendingTransactions().collect { pending ->
                _state.update { it.copy(pendingCount = pending.size) }
            }
        }
    }
}
