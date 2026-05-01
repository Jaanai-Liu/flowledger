package com.flowledger.app.ui.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flowledger.app.data.local.dao.CategorySum
import com.flowledger.app.data.local.dao.DailySummary
import com.flowledger.app.ui.theme.ExpenseRed
import com.flowledger.app.ui.theme.IncomeGreen
import com.flowledger.app.util.CurrencyFormatter
import com.flowledger.app.util.DateUtils
import java.time.LocalDate

private val chartColors = listOf(
    Color(0xFFE91E63), Color(0xFF2196F3), Color(0xFFFF9800),
    Color(0xFF9C27B0), Color(0xFF00BCD4), Color(0xFF4CAF50),
    Color(0xFF3F51B5), Color(0xFF607D8B), Color(0xFF795548),
    Color(0xFFCDDC39)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val now = LocalDate.now()
    val canGoNext = state.currentYear < now.year || state.currentMonth < now.monthValue

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("统计") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // 1. Month selector
                item {
                    MonthSelector(
                        label = state.monthLabel,
                        canGoNext = canGoNext,
                        onPrevious = { viewModel.previousMonth() },
                        onNext = { viewModel.nextMonth() }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // 2. Month summary card
                item {
                    MonthSummaryCard(
                        income = state.totalIncome,
                        expense = state.totalExpense
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 3. Chart range chips
                item {
                    Text("每日支出", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        ChartRange.entries.forEach { range ->
                            FilterChip(
                                selected = state.selectedRange == range,
                                onClick = { viewModel.selectRange(range) },
                                label = { Text(range.label) },
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 4. Daily expense line chart
                item {
                    if (state.dailySummaries.isEmpty()) {
                        EmptyChartPlaceholder()
                    } else {
                        DailyLineChart(dailySummaries = state.dailySummaries)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 5. Category proportion
                if (state.categorySums.isNotEmpty()) {
                    item {
                        Text("支出分类占比", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        CategoryPieBlock(categorySums = state.categorySums, totalExpense = state.totalExpense)
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                // 6. Category breakdown list
                if (state.categorySums.isNotEmpty()) {
                    item {
                        Text("分类明细", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(state.categorySums) { cat ->
                        CategoryRow(cat = cat, total = state.totalExpense)
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }

                // 7. Month-over-month comparison
                if (state.monthSummaries.isNotEmpty()) {
                    item {
                        Text("月度对比", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        MonthOverMonthChart(state.monthSummaries)
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun MonthSelector(
    label: String,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowLeft, contentDescription = "上月")
        }
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.width(120.dp),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = onNext, enabled = canGoNext) {
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "下月",
                tint = if (canGoNext) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun MonthSummaryCard(income: Long, expense: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("收入", style = MaterialTheme.typography.labelMedium)
                Text(
                    CurrencyFormatter.format(income),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = IncomeGreen
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("支出", style = MaterialTheme.typography.labelMedium)
                Text(
                    CurrencyFormatter.format(expense),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ExpenseRed
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("结余", style = MaterialTheme.typography.labelMedium)
                Text(
                    CurrencyFormatter.format(income - expense),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DailyLineChart(dailySummaries: List<DailySummary>) {
    val maxVal = dailySummaries.maxOf { it.expense }.coerceAtLeast(1)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Canvas(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                if (dailySummaries.size < 2) return@Canvas
                val stepX = size.width / (dailySummaries.size - 1).coerceAtLeast(1)
                val path = Path()
                dailySummaries.forEachIndexed { index, day ->
                    val x = index * stepX
                    val y = size.height - (day.expense.toFloat() / maxVal) * size.height
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, color = ExpenseRed.copy(alpha = 0.7f), style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
                // dots
                dailySummaries.forEachIndexed { index, day ->
                    val x = index * stepX
                    val y = size.height - (day.expense.toFloat() / maxVal) * size.height
                    drawCircle(color = ExpenseRed, radius = 3.dp.toPx(), center = Offset(x, y))
                }
            }
            // X axis labels (subset)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (dailySummaries.isNotEmpty()) {
                    Text(
                        DateUtils.formatShortDate(dailySummaries.first().date),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        DateUtils.formatShortDate(dailySummaries.last().date),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryPieBlock(categorySums: List<CategorySum>, totalExpense: Long) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pie chart
            Canvas(modifier = Modifier.size(110.dp)) {
                var startAngle = -90f
                val total = totalExpense.toFloat().coerceAtLeast(1f)
                categorySums.take(10).forEachIndexed { index, cat ->
                    val sweepAngle = (cat.total / total) * 360f
                    drawArc(
                        color = chartColors[index % chartColors.size],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle.coerceAtLeast(1f),
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )
                    startAngle += sweepAngle
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            // Legend
            Column(modifier = Modifier.weight(1f)) {
                categorySums.take(6).forEachIndexed { index, cat ->
                    val pct = if (totalExpense > 0) (cat.total.toFloat() / totalExpense * 100) else 0f
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(8.dp)) {
                            drawCircle(color = chartColors[index % chartColors.size])
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "${cat.name} ${"%.1f".format(pct)}%",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            CurrencyFormatter.format(cat.total),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(cat: CategorySum, total: Long) {
    val pct = if (total > 0) (cat.total.toFloat() / total * 100) else 0f
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(cat.name, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodyMedium)
        // Progress bar
        Box(
            modifier = Modifier.weight(1f).height(12.dp).padding(end = 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val trackColor = MaterialTheme.colorScheme.surfaceVariant
            val fillColor = ExpenseRed.copy(alpha = 0.6f)
            val corner = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx())
            // Background track
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = trackColor,
                    size = Size(size.width, size.height),
                    cornerRadius = corner
                )
            }
            // Filled portion
            Canvas(
                modifier = Modifier
                    .fillMaxWidth(pct / 100f)
                    .height(12.dp)
            ) {
                drawRoundRect(
                    color = fillColor,
                    size = Size(size.width, size.height),
                    cornerRadius = corner
                )
            }
        }
        Text("${"%.1f".format(pct)}%", style = MaterialTheme.typography.labelMedium, modifier = Modifier.width(40.dp))
        Text(CurrencyFormatter.format(cat.total), style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun MonthOverMonthChart(monthSummaries: List<MonthSummary>) {
    val maxVal = monthSummaries.maxOf { maxOf(it.income, it.expense) }.coerceAtLeast(1)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                val stepX = size.width / monthSummaries.size
                monthSummaries.forEachIndexed { index, m ->
                    val x = index * stepX + stepX / 2
                    val incomeH = (m.income.toFloat() / maxVal) * size.height
                    val expenseH = (m.expense.toFloat() / maxVal) * size.height
                    val barW = stepX * 0.3f
                    // Income bar
                    drawRect(
                        color = IncomeGreen.copy(alpha = 0.7f),
                        topLeft = Offset(x - barW - stepX * 0.05f, size.height - incomeH),
                        size = Size(barW, incomeH)
                    )
                    // Expense bar
                    drawRect(
                        color = ExpenseRed.copy(alpha = 0.7f),
                        topLeft = Offset(x + stepX * 0.05f, size.height - expenseH),
                        size = Size(barW, expenseH)
                    )
                }
            }
            // Legend
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Canvas(modifier = Modifier.size(8.dp)) { drawCircle(color = IncomeGreen) }
                Text(" 收入  ", style = MaterialTheme.typography.labelSmall)
                Canvas(modifier = Modifier.size(8.dp)) { drawCircle(color = ExpenseRed) }
                Text(" 支出", style = MaterialTheme.typography.labelSmall)
            }
            // X labels
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (monthSummaries.isNotEmpty()) {
                    val first = monthSummaries.first()
                    val last = monthSummaries.last()
                    Text("${first.month}月", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${last.month}月", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun EmptyChartPlaceholder() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("暂无每日数据", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
