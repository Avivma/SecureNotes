package com.example.securenotes

import android.app.Application
import com.example.securenotes.core.utils.L
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SecureNotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setup()
    }

    private fun setup() {
        L.setup()
    }
}
