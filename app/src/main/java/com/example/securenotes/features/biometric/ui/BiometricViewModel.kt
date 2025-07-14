package com.example.securenotes.features.biometric.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.MainThread
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.securenotes.features.biometric.ui.state.BiometricIntention
import com.example.securenotes.features.biometric.ui.state.BiometricState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.Executor
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
class BiometricViewModel @Inject constructor(
    private val application: Application
) : AndroidViewModel(application) {
    private var biometricPrompt: BiometricPrompt? = null
    private val _authenticationResultLiveData = MutableLiveData<BiometricState>()
    val authenticationResultLiveData: LiveData<BiometricState> = _authenticationResultLiveData

    @MainThread
    suspend fun action(intention: BiometricIntention) {
        when (intention) {
            is BiometricIntention.AuthenticateUser -> authenticateWithBiometrics(intention.authenticationParams)
            BiometricIntention.CancelAuthentication -> cancelAuthentication()
        }
    }

    private suspend fun authenticateWithBiometrics(authenticationParams: Triple<FragmentActivity, Executor, BiometricPrompt.PromptInfo>) {
        _authenticationResultLiveData.value = BiometricState.AskingForBiometric
        if (!isAuthenticationAvailable()) {
            _authenticationResultLiveData.value = BiometricState.DisplayUnavailableMessage
            return
        }

        val (activity, executor, promptInfo) = authenticationParams
        val authenticateSucceeded = hasAuthenticate(activity, executor, promptInfo)
        _authenticationResultLiveData.value =
            if (authenticateSucceeded) BiometricState.Navigation.NavigateToMainScreen
            else BiometricState.DisplayFailedMessage
    }

    private suspend fun hasAuthenticate(
        activity: FragmentActivity,
        executor: Executor,
        promptInfo: BiometricPrompt.PromptInfo
    ): Boolean = suspendCoroutine { continuation ->
        biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    continuation.resume(true)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    continuation.resume(false)
                }

                override fun onAuthenticationFailed() {
                    // Authentication failed – allow retry
                }
            })

        authenticateSafe(promptInfo)
    }

    fun authenticateSafe(info: BiometricPrompt.PromptInfo) {
        Handler(Looper.getMainLooper()).post {
            try {
                biometricPrompt?.authenticate(info)
            } catch (e: IllegalStateException) {
                Log.e("BiometricAuth", "Biometric prompt failed: ${e.message}")
            }
        }
    }

    private fun isAuthenticationAvailable(): Boolean {
        val biometricManager = BiometricManager.from(application)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    private fun cancelAuthentication() {
        biometricPrompt?.cancelAuthentication()
        biometricPrompt = null
    }
}
