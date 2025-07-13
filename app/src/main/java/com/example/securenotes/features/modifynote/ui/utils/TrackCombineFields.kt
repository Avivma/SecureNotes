package com.example.securenotes.features.modifynote.ui.utils

import androidx.lifecycle.MediatorLiveData
import com.example.securenotes.shared.ui.ViewFieldTracked

class TrackCombineFields<T>(val titleField: ViewFieldTracked<T>,
                         val contentField: ViewFieldTracked<T>) {

    val hasChangedOccurred = MediatorLiveData<Boolean>(false).apply {
        addSource(titleField.hasChanged) { checkChanges() }
        addSource(contentField.hasChanged) { checkChanges() }
    }

    private fun checkChanges() {
        hasChangedOccurred.value = titleField.hasChanged.value == true || contentField.hasChanged.value == true
    }
}