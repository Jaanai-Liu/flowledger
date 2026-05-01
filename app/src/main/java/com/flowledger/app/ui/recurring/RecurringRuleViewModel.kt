package com.flowledger.app.ui.recurring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowledger.app.data.local.entity.RecurringFrequency
import com.flowledger.app.data.local.entity.RecurringRuleEntity
import com.flowledger.app.data.local.entity.TransactionType
import com.flowledger.app.data.repository.RecurringRuleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecurringRuleListState(
    val rules: List<RecurringRuleEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class RecurringRuleViewModel @Inject constructor(
    private val ruleRepo: RecurringRuleRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecurringRuleListState())
    val state: StateFlow<RecurringRuleListState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            ruleRepo.getAll().collect { rules ->
                _state.update { it.copy(rules = rules, isLoading = false) }
            }
        }
    }

    fun toggleActive(rule: RecurringRuleEntity) {
        viewModelScope.launch {
            ruleRepo.update(rule.copy(isActive = !rule.isActive))
        }
    }

    fun delete(rule: RecurringRuleEntity) {
        viewModelScope.launch {
            ruleRepo.delete(rule)
        }
    }
}
