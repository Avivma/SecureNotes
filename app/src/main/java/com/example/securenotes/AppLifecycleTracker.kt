package com.example.securenotes

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.securenotes.features.biometric.utils.AppStateTracker
import com.example.securenotes.features.main.ui.MainFragmentDirections
import java.util.concurrent.TimeUnit

class AppLifecycleTracker(
    private val navControllerProvider: NavController
) : DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        AppStateTracker.lastBackgroundTimestamp = System.currentTimeMillis()
    }

    override fun onStart(owner: LifecycleOwner) {
        if (hasTimePassed(1)) {
            navigateToBiometric()
        }
    }

    private fun hasTimePassed(minutes: Int): Boolean {
        val now = System.currentTimeMillis()
        val last = AppStateTracker.lastBackgroundTimestamp
        return last != null && now - last > TimeUnit.MINUTES.toMillis(minutes.toLong())
    }

    private fun navigateToBiometric() {
        val direction = MainFragmentDirections.actionGlobalBiometricFragment()
        navControllerProvider.navigate(direction)
    }
}