package com.example.securenotes.features.biometric.ui.state

import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

sealed class BiometricIntention {
    data class AuthenticateUser(val authenticationParams: Triple<FragmentActivity, Executor, BiometricPrompt.PromptInfo>) : BiometricIntention()
    object CancelAuthentication : BiometricIntention()
}