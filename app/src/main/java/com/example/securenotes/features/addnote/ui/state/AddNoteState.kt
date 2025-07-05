package com.example.securenotes.features.addnote.ui.state

sealed class AddNoteState {
    object Idle : AddNoteState()
    object NoteSaved : AddNoteState()
    data class Error(val message: String) : AddNoteState()

    sealed class Navigation: AddNoteState() {
        object NavigateBack: AddNoteState.Navigation()
    }
}