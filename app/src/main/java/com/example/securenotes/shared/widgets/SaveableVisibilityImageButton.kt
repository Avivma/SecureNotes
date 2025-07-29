package com.example.securenotes.shared.widgets

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.example.securenotes.shared.widgets.utils.VisibilityStateHelper

class SaveableVisibilityImageButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatImageButton(context, attrs) {
    override fun onSaveInstanceState(): Parcelable? {
        return VisibilityStateHelper.saveVisibilityState(this.visibility, this.alpha) {
            super.onSaveInstanceState()
        }
    }
    override fun onRestoreInstanceState(state: Parcelable?) {
        VisibilityStateHelper.restoreVisibilityState(this, state) { updateState ->
            super.onRestoreInstanceState(updateState)
        }
    }
}