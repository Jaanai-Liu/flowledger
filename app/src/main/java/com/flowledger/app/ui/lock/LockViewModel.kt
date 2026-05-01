package com.flowledger.app.ui.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flowledger.app.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LockViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    private val _isLockEnabled = MutableStateFlow(false)
    val isLockEnabled: StateFlow<Boolean> = _isLockEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepo.isLockEnabled.collect { enabled ->
                _isLockEnabled.value = enabled
            }
        }
    }
}
