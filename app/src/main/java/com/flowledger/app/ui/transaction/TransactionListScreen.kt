package com.flowledger.app.ui.transaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flowledger.app.data.local.entity.TransactionType
import com.flowledger.app.ui.component.EmptyState
import com.flowledger.app.ui.component.TransactionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAdd: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("账单") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Rounded.Add, contentDescription = "添加")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.foundation.layout.Row {
                        FilterChip(
                            selected = state.filterType == null,
                            onClick = { viewModel.filterByType(null) },
                            label = { Text("全部") },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        FilterChip(
                            selected = state.filterType == TransactionType.EXPENSE,
                            onClick = { viewModel.filterByType(TransactionType.EXPENSE) },
                            label = { Text("支出") },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        FilterChip(
                            selected = state.filterType == TransactionType.INCOME,
                            onClick = { viewModel.filterByType(TransactionType.INCOME) },
                            label = { Text("收入") }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (state.transactions.isEmpty()) {
                    item { EmptyState(message = "暂无记录") }
                } else {
                    items(state.transactions, key = { it.id }) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = { onNavigateToDetail(transaction.id) }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}
