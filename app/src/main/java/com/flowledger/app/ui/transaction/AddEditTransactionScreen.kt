package com.flowledger.app.ui.transaction

import android.app.DatePickerDialog
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flowledger.app.data.local.entity.TransactionType
import com.flowledger.app.util.DateUtils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTransactionScreen(
    transactionId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val state by viewModel.editState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(transactionId) {
        viewModel.loadEditData(transactionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (transactionId != null) "编辑记录" else "添加记录") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (!state.isLoaded) {
            Column(
                modifier = Modifier.fillMaxSize().padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Type selection
                Text("类型", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.selectableGroup()) {
                    listOf(
                        TransactionType.EXPENSE to "支出",
                        TransactionType.INCOME to "收入"
                    ).forEach { (type, label) ->
                        Row(
                            modifier = Modifier
                                .selectable(
                                    selected = state.type == type,
                                    onClick = { viewModel.updateType(type) },
                                    role = Role.RadioButton
                                )
                                .padding(end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = state.type == type,
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(label)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount
                OutlinedTextField(
                    value = state.amount,
                    onValueChange = { viewModel.updateAmount(it) },
                    label = { Text("金额") },
                    placeholder = { Text("0.00") },
                    prefix = { Text(if (state.type == TransactionType.EXPENSE) "¥" else "¥") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Date
                var showDatePicker by remember { mutableStateOf(false) }
                if (showDatePicker) {
                    val datePicker = DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            viewModel.updateDate(
                                LocalDate.of(year, month + 1, dayOfMonth).toEpochDay()
                            )
                            showDatePicker = false
                        },
                        DateUtils.fromEpochDay(state.date).year,
                        DateUtils.fromEpochDay(state.date).monthValue - 1,
                        DateUtils.fromEpochDay(state.date).dayOfMonth
                    )
                    datePicker.show()
                    showDatePicker = false
                }

                OutlinedTextField(
                    value = DateUtils.formatDate(state.date),
                    onValueChange = {},
                    label = { Text("日期") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Category dropdown
                CategoryDropdown(
                    selectedId = state.categoryId,
                    categories = state.categories,
                    onSelected = { viewModel.updateCategory(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Account dropdown
                AccountDropdown(
                    selectedId = state.accountId,
                    accounts = state.accounts,
                    onSelected = { viewModel.updateAccount(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Payment method dropdown
                PaymentMethodDropdown(
                    selectedId = state.paymentMethodId,
                    methods = state.paymentMethods,
                    onSelected = { viewModel.updatePaymentMethod(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Note
                OutlinedTextField(
                    value = state.note,
                    onValueChange = { viewModel.updateNote(it) },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save button
                Button(
                    onClick = {
                        viewModel.save { onNavigateBack() }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving && state.amount.isNotBlank() && state.categoryId != null && state.accountId != null
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp).width(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("保存")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(
    selectedId: Long?,
    categories: List<com.flowledger.app.data.local.entity.CategoryEntity>,
    onSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = categories.find { it.id == selectedId }?.name ?: "选择分类"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("分类") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onSelected(category.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountDropdown(
    selectedId: Long?,
    accounts: List<com.flowledger.app.data.local.entity.AccountEntity>,
    onSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = accounts.find { it.id == selectedId }?.name ?: "选择账户"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("账户") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            accounts.forEach { account ->
                DropdownMenuItem(
                    text = { Text(account.name) },
                    onClick = {
                        onSelected(account.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentMethodDropdown(
    selectedId: Long?,
    methods: List<com.flowledger.app.data.local.entity.PaymentMethodEntity>,
    onSelected: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = methods.find { it.id == selectedId }?.name ?: "选择支付方式（可选）"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("支付方式") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("无") },
                onClick = {
                    onSelected(null)
                    expanded = false
                }
            )
            methods.forEach { method ->
                DropdownMenuItem(
                    text = { Text(method.name) },
                    onClick = {
                        onSelected(method.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
