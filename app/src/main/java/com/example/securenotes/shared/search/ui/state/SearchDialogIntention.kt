package com.example.securenotes.shared.search.ui.state

import com.example.securenotes.shared.search.domain.model.SearchDialogNoteModel

sealed class SearchDialogIntention {
    data class NoteClicked(val note: SearchDialogNoteModel): SearchDialogIntention()
    object Search: SearchDialogIntention()
    object RefreshSearch: SearchDialogIntention()
    object ClearSearch: SearchDialogIntention()

}