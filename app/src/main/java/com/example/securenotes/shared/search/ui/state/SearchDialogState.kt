package com.example.securenotes.shared.search.ui.state

import com.example.securenotes.shared.search.domain.model.SearchDialogNoteModel

sealed class SearchDialogState {
    data class ToggleSearchButtons(val isEnabled: Boolean): SearchDialogState()
    data class DisplayNotes(val notes: List<SearchDialogNoteModel>): SearchDialogState()

    sealed class DisplayMessage: SearchDialogState() {
        object SearchEmpty: SearchDialogState()
        object NoResults: SearchDialogState()
    }

    sealed class Navigation: SearchDialogState() {
        data class NavigateToModifyNote(val noteId: Int, val searchText: String): SearchDialogState.Navigation()
    }
}