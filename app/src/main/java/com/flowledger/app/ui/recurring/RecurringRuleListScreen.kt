package com.flowledger.app.ui.recurring

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flowledger.app.data.local.entity.RecurringFrequency
import com.flowledger.app.data.local.entity.RecurringRuleEntity
import com.flowledger.app.data.local.entity.TransactionType
import com.flowledger.app.ui.component.EmptyState
import com.flowledger.app.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringRuleListScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToAdd: () -> Unit,
    viewModel: RecurringRuleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("周期收支") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Rounded.Add, contentDescription = "添加规则")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.rules.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(message = "暂无周期规则，点击 + 添加")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
            ) {
                items(state.rules, key = { it.id }) { rule ->
                    RecurringRuleItem(
                        rule = rule,
                        onToggle = { viewModel.toggleActive(rule) },
                        onDelete = { viewModel.delete(rule) },
                        onClick = { onNavigateToEdit(rule.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun RecurringRuleItem(
    rule: RecurringRuleEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (rule.type == TransactionType.INCOME) "+" else "-",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (rule.type == TransactionType.INCOME)
                            com.flowledger.app.ui.theme.IncomeGreen
                        else com.flowledger.app.ui.theme.ExpenseRed
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(CurrencyFormatter.format(rule.amount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                }
                Text(
                    when (rule.frequency) {
                        RecurringFrequency.MONTHLY -> "每月${rule.dayOfMonth}日"
                        RecurringFrequency.WEEKLY -> "每周"
                        RecurringFrequency.YEARLY -> "每年"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!rule.noteTemplate.isNullOrBlank()) {
                    Text(rule.noteTemplate, style = MaterialTheme.typography.bodySmall)
                }
            }
            Switch(checked = rule.isActive, onCheckedChange = { onToggle() })
            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.Delete, contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
