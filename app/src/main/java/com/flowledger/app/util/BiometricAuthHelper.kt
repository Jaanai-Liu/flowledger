package com.flowledger.app.util

import android.app.Activity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricAuthHelper(private val activity: FragmentActivity) {

    private var onSuccess: (() -> Unit)? = null
    private var onError: ((String) -> Unit)? = null

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(activity)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticate(onSuccess: () -> Unit, onError: (String) -> Unit) {
        this.onSuccess = onSuccess
        this.onError = onError

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("身份验证")
            .setSubtitle("请验证身份以解锁 FlowLedger")
            .setNegativeButtonText("取消")
            .build()

        val biometricPrompt = BiometricPrompt(activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess?.invoke()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        onError?.invoke(errString.toString())
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onError?.invoke("验证失败，请重试")
                }
            })

        biometricPrompt.authenticate(promptInfo)
    }
}
