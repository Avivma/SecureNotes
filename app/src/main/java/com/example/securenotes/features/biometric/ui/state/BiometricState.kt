package com.example.securenotes.features.biometric.ui.state

sealed class BiometricState {
    object AskingForBiometric : BiometricState()
    object DisplayFailedMessage : BiometricState()
    object DisplayUnavailableMessage : BiometricState()

    sealed class Navigation: BiometricState() {
        object NavigateToMainScreen: BiometricState.Navigation()
    }
}