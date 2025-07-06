package com.example.securenotes.features.addnote.ui.state

import com.example.securenotes.features.addnote.domain.model.AddNoteModel

sealed class AddNoteIntention {
    object SaveNote : AddNoteIntention()
    object BackPressed : AddNoteIntention()
    data class RemoveNote(val note: AddNoteModel) : AddNoteIntention()
}