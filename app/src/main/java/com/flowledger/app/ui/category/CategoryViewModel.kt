package com.flowledger.app.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowledger.app.data.local.entity.CategoryEntity
import com.flowledger.app.data.local.entity.TransactionType
import com.flowledger.app.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryState(
    val expenseCategories: List<CategoryEntity> = emptyList(),
    val incomeCategories: List<CategoryEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepo: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CategoryState())
    val state: StateFlow<CategoryState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepo.getByType(TransactionType.EXPENSE).collect { categories ->
                _state.update { it.copy(expenseCategories = categories) }
            }
        }
        viewModelScope.launch {
            categoryRepo.getByType(TransactionType.INCOME).collect { categories ->
                _state.update { it.copy(incomeCategories = categories, isLoading = false) }
            }
        }
    }
}

data class PaymentMethodState(
    val methods: List<com.flowledger.app.data.local.entity.PaymentMethodEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class PaymentMethodViewModel @Inject constructor(
    private val paymentMethodRepo: com.flowledger.app.data.repository.PaymentMethodRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PaymentMethodState())
    val state: StateFlow<PaymentMethodState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            paymentMethodRepo.getAllActive().collect { methods ->
                _state.update { it.copy(methods = methods, isLoading = false) }
            }
        }
    }
}
