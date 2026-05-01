package com.flowledger.app.ui.transaction

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flowledger.app.data.local.entity.TransactionEntity
import com.flowledger.app.data.local.entity.TransactionStatus
import com.flowledger.app.ui.component.AmountText
import com.flowledger.app.util.CurrencyFormatter
import com.flowledger.app.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    var transaction by remember { mutableStateOf<TransactionEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        viewModel.loadTransaction(transactionId) { transaction = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("交易详情") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(transactionId) }) {
                        Icon(Icons.Rounded.Edit, contentDescription = "编辑")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "删除")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (transaction != null) {
                val t = transaction!!
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        AmountText(type = t.type, amount = t.amount)

                        Spacer(modifier = Modifier.height(12.dp))

                        if (t.status == TransactionStatus.PENDING) {
                            Text(
                                "状态：待确认",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                DetailRow("日期", DateUtils.formatDate(t.date) + " " + DateUtils.getDayOfWeekDisplay(t.date))
                DetailRow("类型", if (t.type.name == "INCOME") "收入" else "支出")
                DetailRow("备注", t.note ?: "无")

                if (t.source.name.startsWith("AUTO")) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "来源：${when (t.source) {
                            com.flowledger.app.data.local.entity.TransactionSource.AUTO_NOTIFICATION -> "自动抓取"
                            com.flowledger.app.data.local.entity.TransactionSource.AUTO_RECURRING -> "自动周期"
                            else -> "手动"
                        }}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                if (t.status == TransactionStatus.PENDING) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            viewModel.confirmPending(t.id)
                            onNavigateBack()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("确认此记录")
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("删除后无法恢复，确定要删除此记录吗？") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(transactionId) { onNavigateBack() }
                    showDeleteDialog = false
                }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
