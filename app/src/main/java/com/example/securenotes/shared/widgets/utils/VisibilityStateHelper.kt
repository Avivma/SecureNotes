package com.example.securenotes.shared.widgets.utils

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View

object VisibilityStateHelper {
    private const val KEY_VISIBILITY = "visibility"
    private const val ALPHA_VISIBILITY = "alpha"
    private const val PARCELABLE_KEY = "superState"

    fun saveVisibilityState(visibility: Int, alpha: Float,saveInstance: () -> Parcelable?): Parcelable {
        val superState = saveInstance()
        return Bundle().apply {
            putParcelable(PARCELABLE_KEY, superState)
            putInt(KEY_VISIBILITY, visibility)
            putFloat(ALPHA_VISIBILITY, alpha)
        }
    }

    fun restoreVisibilityState(view: View, state: Parcelable?, restoreInstance: (Parcelable?) -> Unit) {
        if (state is Bundle) {
            val updateState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                state.getParcelable(PARCELABLE_KEY, Parcelable::class.java)
            } else {
                @Suppress("DEPRECATION")
                state.getParcelable(PARCELABLE_KEY)
            }
            restoreInstance(updateState)
            view.visibility = state.getInt(KEY_VISIBILITY, View.VISIBLE)
            view.alpha = state.getFloat(ALPHA_VISIBILITY, 1f)
        } else {
            restoreInstance(state)
        }
    }
}