package com.example.securenotes.features.main.ui.state

import com.example.securenotes.features.main.ui.model.UiNote


sealed class MainState() {
    object Waiting: MainState()
    data class DisplayNotes(val notes: List<UiNote>): MainState()
    data class DisplayRemoveQuestion(val note: UiNote): MainState()
    data class NoteRemoved(val title: String) : MainState()
    data class Error(val message: String): MainState()
    object OpenMenu: MainState()

    sealed class Navigation: MainState() {
        data class NavigateToModifyNote(val note: UiNote? = null): MainState.Navigation()
        object NavigateToSearchDialog: MainState.Navigation()
    }
}