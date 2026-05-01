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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flowledger.app.data.local.dao.CategorySum
import com.flowledger.app.ui.theme.ExpenseRed
import com.flowledger.app.ui.theme.IncomeGreen
import com.flowledger.app.util.CurrencyFormatter
import com.flowledger.app.util.DateUtils

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
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Summary
                item {
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
                                Text(CurrencyFormatter.format(state.totalIncome),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = IncomeGreen)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("支出", style = MaterialTheme.typography.labelMedium)
                                Text(CurrencyFormatter.format(state.totalExpense),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = ExpenseRed)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("结余", style = MaterialTheme.typography.labelMedium)
                                Text(CurrencyFormatter.format(state.totalIncome - state.totalExpense),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Pie Chart
                if (state.categorySums.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("支出分类", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        PieChartSection(categorySums = state.categorySums)
                    }
                }

                // Daily bar chart
                if (state.dailySummaries.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("每日收支", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        DailyBarChart(dailySummaries = state.dailySummaries)
                    }
                }

                // Category list
                if (state.categorySums.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("分类明细", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(state.categorySums) { cat ->
                        val percentage = if (state.totalExpense > 0)
                            (cat.total.toFloat() / state.totalExpense * 100) else 0f
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(cat.name, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodyMedium)
                            Box(modifier = Modifier.weight(1f).height(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(percentage / 100f)
                                        .height(12.dp)
                                        .padding(end = 4.dp)
                                )
                            }
                            Text("${"%.1f".format(percentage)}%", style = MaterialTheme.typography.labelMedium)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(CurrencyFormatter.format(cat.total), style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun PieChartSection(categorySums: List<CategorySum>) {
    val total = categorySums.sumOf { it.total }.toFloat()
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier.size(120.dp)) {
                var startAngle = -90f
                categorySums.take(10).forEachIndexed { index, cat ->
                    val sweepAngle = (cat.total / total) * 360f
                    drawArc(
                        color = chartColors[index % chartColors.size],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        size = Size(size.width, size.height)
                    )
                    startAngle += sweepAngle
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                categorySums.take(6).forEachIndexed { index, cat ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Canvas(modifier = Modifier.size(8.dp)) {
                            drawCircle(color = chartColors[index % chartColors.size])
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("${cat.name} ${CurrencyFormatter.format(cat.total)}",
                            style = MaterialTheme.typography.labelSmall)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
private fun DailyBarChart(dailySummaries: List<com.flowledger.app.data.local.dao.DailySummary>) {
    val maxVal = dailySummaries.maxOf { maxOf(it.income, it.expense) }.coerceAtLeast(1)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Canvas(
                modifier = Modifier.fillMaxWidth().height(150.dp)
            ) {
                val barWidth = size.width / dailySummaries.size / 2.5f
                val gap = size.width / dailySummaries.size
                dailySummaries.forEachIndexed { index, day ->
                    val incomeHeight = (day.income.toFloat() / maxVal) * size.height
                    val expenseHeight = (day.expense.toFloat() / maxVal) * size.height
                    val x = index * gap + gap / 2

                    drawRect(
                        color = IncomeGreen.copy(alpha = 0.7f),
                        topLeft = Offset(x - barWidth, size.height - incomeHeight),
                        size = Size(barWidth, incomeHeight)
                    )
                    drawRect(
                        color = ExpenseRed.copy(alpha = 0.7f),
                        topLeft = Offset(x, size.height - expenseHeight),
                        size = Size(barWidth, expenseHeight)
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Canvas(modifier = Modifier.size(8.dp)) { drawCircle(color = IncomeGreen) }
                Text(" 收入  ", style = MaterialTheme.typography.labelSmall)
                Canvas(modifier = Modifier.size(8.dp)) { drawCircle(color = ExpenseRed) }
                Text(" 支出", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
