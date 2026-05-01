package com.flowledger.app.ui.recurring

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.flowledger.app.data.local.entity.RecurringFrequency
import com.flowledger.app.data.local.entity.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringRuleEditScreen(
    ruleId: Long?,
    onNavigateBack: () -> Unit
) {
    // Placeholder state for the form
    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf(RecurringFrequency.MONTHLY) }
    var dayOfMonth by remember { mutableStateOf("1") }
    var noteTemplate by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (ruleId != null) "编辑周期规则" else "添加周期规则") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text("类型", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.selectableGroup()) {
                listOf(TransactionType.EXPENSE to "支出", TransactionType.INCOME to "收入").forEach { (t, label) ->
                    Row(
                        modifier = Modifier.selectable(selected = type == t, onClick = { type = t }, role = Role.RadioButton).padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = type == t, onClick = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(label)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("金额") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("周期", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.selectableGroup()) {
                listOf(RecurringFrequency.MONTHLY to "每月", RecurringFrequency.WEEKLY to "每周", RecurringFrequency.YEARLY to "每年").forEach { (freq, label) ->
                    Row(
                        modifier = Modifier.selectable(selected = frequency == freq, onClick = { frequency = freq }, role = Role.RadioButton).padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = frequency == freq, onClick = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(label)
                    }
                }
            }

            if (frequency == RecurringFrequency.MONTHLY || frequency == RecurringFrequency.YEARLY) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = dayOfMonth,
                    onValueChange = { dayOfMonth = it },
                    label = { Text("日期") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = noteTemplate,
                onValueChange = { noteTemplate = it },
                label = { Text("备注模板") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth(),
                enabled = amount.isNotBlank()
            ) {
                Text("保存")
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
