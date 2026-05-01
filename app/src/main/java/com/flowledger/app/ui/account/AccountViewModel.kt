package com.flowledger.app.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowledger.app.data.local.entity.AccountEntity
import com.flowledger.app.data.local.entity.AccountType
import com.flowledger.app.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountListState(
    val accounts: List<AccountEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepo: AccountRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AccountListState())
    val state: StateFlow<AccountListState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            accountRepo.getAllActive().collect { accounts ->
                _state.update { it.copy(accounts = accounts, isLoading = false) }
            }
        }
    }

    fun insert(name: String, type: AccountType, initialBalance: Long, onComplete: () -> Unit) {
        viewModelScope.launch {
            accountRepo.insert(
                AccountEntity(
                    name = name,
                    type = type,
                    initialBalance = initialBalance
                )
            )
            onComplete()
        }
    }

    fun archive(account: AccountEntity, onComplete: () -> Unit) {
        viewModelScope.launch {
            accountRepo.update(account.copy(isArchived = true))
            onComplete()
        }
    }
}
