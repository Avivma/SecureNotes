package com.example.securenotes.features.main.ui.state

import com.example.securenotes.features.main.ui.model.UiNote


sealed class MainState() {
    object Waiting: MainState()
    data class DisplayNotes(val notes: List<UiNote>): MainState()
    data class DisplayRemoveQuestion(val note: UiNote): MainState()
    data class NoteRemoved(val title: String) : MainState()
    data class Error(val message: String): MainState()

    sealed class Navigation: MainState() {
        object NavigateToAddNote: MainState.Navigation()
    }
}