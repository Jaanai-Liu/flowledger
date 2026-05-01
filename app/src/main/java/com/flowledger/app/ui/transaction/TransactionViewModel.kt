package com.flowledger.app.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowledger.app.data.local.entity.AccountEntity
import com.flowledger.app.data.local.entity.CategoryEntity
import com.flowledger.app.data.local.entity.PaymentMethodEntity
import com.flowledger.app.data.local.entity.TransactionEntity
import com.flowledger.app.data.local.entity.TransactionSource
import com.flowledger.app.data.local.entity.TransactionStatus
import com.flowledger.app.data.local.entity.TransactionType
import com.flowledger.app.data.repository.AccountRepository
import com.flowledger.app.data.repository.CategoryRepository
import com.flowledger.app.data.repository.PaymentMethodRepository
import com.flowledger.app.data.repository.TransactionRepository
import com.flowledger.app.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionListState(
    val transactions: List<TransactionEntity> = emptyList(),
    val filterType: TransactionType? = null,
    val isLoading: Boolean = true
)

data class TransactionEditState(
    val type: TransactionType = TransactionType.EXPENSE,
    val amount: String = "",
    val date: Long = DateUtils.todayEpochDay(),
    val note: String = "",
    val categoryId: Long? = null,
    val accountId: Long? = null,
    val paymentMethodId: Long? = null,
    val categories: List<CategoryEntity> = emptyList(),
    val accounts: List<AccountEntity> = emptyList(),
    val paymentMethods: List<PaymentMethodEntity> = emptyList(),
    val isSaving: Boolean = false,
    val isLoaded: Boolean = false
)

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val categoryRepo: CategoryRepository,
    private val accountRepo: AccountRepository,
    private val paymentMethodRepo: PaymentMethodRepository
) : ViewModel() {

    private val _listState = MutableStateFlow(TransactionListState())
    val listState: StateFlow<TransactionListState> = _listState.asStateFlow()

    private val _editState = MutableStateFlow(TransactionEditState())
    val editState: StateFlow<TransactionEditState> = _editState.asStateFlow()

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            transactionRepo.getAll().collect { transactions ->
                _listState.update { it.copy(transactions = transactions, isLoading = false) }
            }
        }
    }

    fun filterByType(type: TransactionType?) {
        _listState.update { it.copy(filterType = type) }
        if (type != null) {
            viewModelScope.launch {
                transactionRepo.getByType(type).collect { transactions ->
                    _listState.update { it.copy(transactions = transactions) }
                }
            }
        } else {
            loadTransactions()
        }
    }

    fun loadEditData(transactionId: Long? = null) {
        viewModelScope.launch {
            categoryRepo.getByType(TransactionType.EXPENSE).collect { categories ->
                _editState.update { it.copy(categories = categories) }
            }
        }
        viewModelScope.launch {
            accountRepo.getAllActive().collect { accounts ->
                _editState.update { it.copy(accounts = accounts) }
            }
        }
        viewModelScope.launch {
            paymentMethodRepo.getAllActive().collect { methods ->
                _editState.update { it.copy(paymentMethods = methods) }
            }
        }

        if (transactionId != null) {
            viewModelScope.launch {
                val transaction = transactionRepo.getById(transactionId)
                if (transaction != null) {
                    _editState.update {
                        it.copy(
                            type = transaction.type,
                            amount = (transaction.amount / 100.0).toString(),
                            date = transaction.date,
                            note = transaction.note ?: "",
                            categoryId = transaction.categoryId,
                            accountId = transaction.accountId,
                            paymentMethodId = transaction.paymentMethodId,
                            isLoaded = true
                        )
                    }
                    // Load categories for this type
                    categoryRepo.getByType(transaction.type).collect { categories ->
                        _editState.update { state -> state.copy(categories = categories) }
                    }
                } else {
                    _editState.update { it.copy(isLoaded = true) }
                }
            }
        } else {
            _editState.update { it.copy(isLoaded = true) }
        }
    }

    fun updateType(type: TransactionType) {
        _editState.update { it.copy(type = type) }
        viewModelScope.launch {
            categoryRepo.getByType(type).collect { categories ->
                _editState.update { it.copy(categories = categories) }
            }
        }
    }

    fun updateAmount(amount: String) {
        _editState.update { it.copy(amount = amount) }
    }

    fun updateDate(date: Long) {
        _editState.update { it.copy(date = date) }
    }

    fun updateNote(note: String) {
        _editState.update { it.copy(note = note) }
    }

    fun updateCategory(categoryId: Long) {
        _editState.update { it.copy(categoryId = categoryId) }
    }

    fun updateAccount(accountId: Long) {
        _editState.update { it.copy(accountId = accountId) }
    }

    fun updatePaymentMethod(paymentMethodId: Long?) {
        _editState.update { it.copy(paymentMethodId = paymentMethodId) }
    }

    fun save(onComplete: () -> Unit) {
        val state = _editState.value
        val amountInCents = try {
            (state.amount.toDouble() * 100).toLong()
        } catch (e: NumberFormatException) {
            return
        }

        if (amountInCents <= 0 || state.categoryId == null || state.accountId == null) return

        _editState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            val transaction = TransactionEntity(
                type = state.type,
                amount = amountInCents,
                categoryId = state.categoryId!!,
                accountId = state.accountId!!,
                paymentMethodId = state.paymentMethodId,
                date = state.date,
                note = state.note.ifBlank { null },
                source = TransactionSource.MANUAL,
                status = TransactionStatus.CONFIRMED
            )
            transactionRepo.insert(transaction)
            onComplete()
        }
    }

    fun loadTransaction(transactionId: Long, onLoaded: (TransactionEntity) -> Unit) {
        viewModelScope.launch {
            val transaction = transactionRepo.getById(transactionId)
            if (transaction != null) {
                onLoaded(transaction)
            }
        }
    }

    fun confirmPending(transactionId: Long) {
        viewModelScope.launch {
            val transaction = transactionRepo.getById(transactionId) ?: return@launch
            transactionRepo.update(transaction.copy(status = TransactionStatus.CONFIRMED))
        }
    }

    fun delete(transactionId: Long, onComplete: () -> Unit) {
        viewModelScope.launch {
            val transaction = transactionRepo.getById(transactionId) ?: return@launch
            transactionRepo.delete(transaction)
            onComplete()
        }
    }
}
