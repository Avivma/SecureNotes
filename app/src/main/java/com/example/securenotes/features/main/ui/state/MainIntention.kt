package com.example.securenotes.features.main.ui.state

import com.example.securenotes.features.main.ui.model.UiNote

sealed class MainIntention {
    object LoadNotes : MainIntention()
    object AddNote : MainIntention()
    data class EditNote(val note: UiNote) : MainIntention()
    data class RemoveNote(val note: UiNote, val displayDialog: Boolean = false) : MainIntention()
}
