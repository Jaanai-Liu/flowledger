package com.flowledger.app.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowledger.app.data.local.dao.CategorySum
import com.flowledger.app.data.local.dao.DailySummary
import com.flowledger.app.data.local.entity.TransactionStatus
import com.flowledger.app.data.local.entity.TransactionType
import com.flowledger.app.data.repository.SettingsRepository
import com.flowledger.app.data.repository.TransactionRepository
import com.flowledger.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

enum class ChartRange(val days: Int, val label: String) {
    WEEK(7, "7天"),
    HALF_MONTH(15, "15天"),
    MONTH(30, "30天")
}

data class MonthSummary(
    val year: Int,
    val month: Int,
    val income: Long,
    val expense: Long
)

data class StatisticsState(
    val currentYear: Int = LocalDate.now().year,
    val currentMonth: Int = LocalDate.now().monthValue,
    val monthLabel: String = "",
    val totalIncome: Long = 0,
    val totalExpense: Long = 0,
    val dailySummaries: List<DailySummary> = emptyList(),
    val categorySums: List<CategorySum> = emptyList(),
    val selectedRange: ChartRange = ChartRange.WEEK,
    val monthSummaries: List<MonthSummary> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(StatisticsState())
    val state: StateFlow<StatisticsState> = _state.asStateFlow()

    private var monthDataJob: Job? = null
    private var dailyDataJob: Job? = null

    init {
        viewModelScope.launch {
            settingsRepo.chartRange.collect { rangeStr ->
                val range = try { ChartRange.valueOf(rangeStr) } catch (_: Exception) { ChartRange.WEEK }
                _state.update { it.copy(selectedRange = range) }
            }
        }
        observeMonthData()
        observeMonthOverMonth()
    }

    fun selectRange(range: ChartRange) {
        _state.update { it.copy(selectedRange = range) }
        viewModelScope.launch { settingsRepo.setChartRange(range.name) }
        observeDailyData()
    }

    fun navigateToMonth(year: Int, month: Int) {
        _state.update { it.copy(currentYear = year, currentMonth = month) }
        observeMonthData()
    }

    fun previousMonth() {
        val s = _state.value
        if (s.currentMonth == 1) navigateToMonth(s.currentYear - 1, 12)
        else navigateToMonth(s.currentYear, s.currentMonth - 1)
    }

    fun nextMonth() {
        val s = _state.value
        val now = LocalDate.now()
        if (s.currentYear == now.year && s.currentMonth == now.monthValue) return
        if (s.currentMonth == 12) navigateToMonth(s.currentYear + 1, 1)
        else navigateToMonth(s.currentYear, s.currentMonth + 1)
    }

    private fun observeMonthData() {
        monthDataJob?.cancel()
        val s = _state.value
        val startDate = DateUtils.monthStart(s.currentYear, s.currentMonth)
        val endDate = DateUtils.monthEnd(s.currentYear, s.currentMonth)

        _state.update {
            it.copy(
                monthLabel = DateUtils.formatMonthYear(s.currentYear, s.currentMonth),
                isLoading = true
            )
        }

        monthDataJob = viewModelScope.launch {
            transactionRepo.getByDateRange(startDate, endDate).collect { transactions ->
                val confirmed = transactions.filter { it.status == TransactionStatus.CONFIRMED }
                val income = confirmed.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val expense = confirmed.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

                // Category sums for expense
                val expenseTxs = confirmed.filter { it.type == TransactionType.EXPENSE }
                val catMap = mutableMapOf<String, Long>()
                expenseTxs.forEach { tx ->
                    val key = tx.note ?: "其他支出"
                    catMap[key] = (catMap[key] ?: 0) + tx.amount
                }
                val catSums = catMap.entries
                    .sortedByDescending { it.value }
                    .map { CategorySum(it.key, it.value) }

                // Daily summaries
                val dailyMap = mutableMapOf<Long, Pair<Long, Long>>()
                transactions.filter { it.status == TransactionStatus.CONFIRMED }.forEach { tx ->
                    val (inc, exp) = dailyMap.getOrDefault(tx.date, Pair(0L, 0L))
                    if (tx.type == TransactionType.INCOME) dailyMap[tx.date] = Pair(inc + tx.amount, exp)
                    else dailyMap[tx.date] = Pair(inc, exp + tx.amount)
                }
                val dailySummaries = dailyMap.entries
                    .sortedBy { it.key }
                    .map { DailySummary(it.key, it.value.first, it.value.second) }

                _state.update {
                    it.copy(
                        totalIncome = income,
                        totalExpense = expense,
                        categorySums = catSums,
                        dailySummaries = dailySummaries,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observeDailyData() {
        dailyDataJob?.cancel()
        val s = _state.value
        val endDate = DateUtils.todayEpochDay()
        val startDate = endDate - s.selectedRange.days + 1

        dailyDataJob = viewModelScope.launch {
            transactionRepo.getByDateRange(startDate, endDate).collect { transactions ->
                val confirmed = transactions.filter { it.status == TransactionStatus.CONFIRMED }
                val dailyMap = mutableMapOf<Long, Pair<Long, Long>>()
                confirmed.forEach { tx ->
                    val (inc, exp) = dailyMap.getOrDefault(tx.date, Pair(0L, 0L))
                    if (tx.type == TransactionType.INCOME) dailyMap[tx.date] = Pair(inc + tx.amount, exp)
                    else dailyMap[tx.date] = Pair(inc, exp + tx.amount)
                }
                val dailySummaries = dailyMap.entries
                    .sortedBy { it.key }
                    .map { DailySummary(it.key, it.value.first, it.value.second) }

                // Fill in missing dates with zero
                val filled = (startDate..endDate).map { date ->
                    dailyMap[date]?.let { DailySummary(date, it.first, it.second) }
                        ?: DailySummary(date, 0, 0)
                }

                _state.update { it.copy(dailySummaries = filled) }
            }
        }
    }

    private fun observeMonthOverMonth() {
        viewModelScope.launch {
            val now = LocalDate.now()
            val summaries = mutableListOf<MonthSummary>()
            for (i in 11 downTo 0) {
                val month = now.minusMonths(i.toLong())
                val startDate = DateUtils.monthStart(month.year, month.monthValue)
                val endDate = DateUtils.monthEnd(month.year, month.monthValue)
                val income = transactionRepo.getTotalIncome(startDate, endDate)
                val expense = transactionRepo.getTotalExpense(startDate, endDate)
                summaries.add(MonthSummary(month.year, month.monthValue, income, expense))
            }
            _state.update { it.copy(monthSummaries = summaries) }
        }
    }
}
