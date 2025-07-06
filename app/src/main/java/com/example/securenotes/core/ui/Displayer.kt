package com.example.securenotes.core.ui

import android.content.Context
import android.widget.Toast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DisplayToast @Inject constructor(
    private val context: Context
) {
    operator fun invoke(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
