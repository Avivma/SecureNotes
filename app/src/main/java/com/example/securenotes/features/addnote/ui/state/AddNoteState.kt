package com.example.securenotes.features.addnote.ui.state

import com.example.securenotes.features.addnote.domain.model.AddNoteModel

sealed class AddNoteState {
    object Idle : AddNoteState()
    object NoteSaved : AddNoteState()
    data class DisplayRemoveQuestion(val note: AddNoteModel): AddNoteState()
    data class NoteRemoved(val title: String) : AddNoteState()
    data class Error(val message: String) : AddNoteState()

    sealed class Navigation: AddNoteState() {
        object NavigateBack: AddNoteState.Navigation()
    }
}