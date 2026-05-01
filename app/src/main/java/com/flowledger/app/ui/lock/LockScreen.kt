package com.flowledger.app.ui.lock

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flowledger.app.util.BiometricAuthHelper

@Composable
fun LockScreen(
    onUnlocked: () -> Unit,
    viewModel: LockViewModel = hiltViewModel()
) {
    val isLockEnabled by viewModel.isLockEnabled.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(isLockEnabled) {
        if (!isLockEnabled) {
            onUnlocked()
        }
    }

    if (isLockEnabled) {
        val activity = remember { context as? Activity }
        val authHelper = remember(activity) {
            activity?.let { BiometricAuthHelper(it as androidx.fragment.app.FragmentActivity) }
        }

        LaunchedEffect(Unit) {
            authHelper?.let { helper ->
                if (helper.isBiometricAvailable()) {
                    helper.authenticate(
                        onSuccess = { onUnlocked() },
                        onError = { /* stay on lock screen, user can retry */ }
                    )
                } else {
                    // No biometric available, skip lock
                    onUnlocked()
                }
            } ?: onUnlocked()
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "FlowLedger",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "请验证身份以解锁",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
