package com.example.securenotes.features.modifynote.ui.state

import com.example.securenotes.features.modifynote.ui.utils.ModifyNoteViewsManager.ViewFocus

sealed class ModifyNoteIntention {
    data class FetchData(val searchText: String = "") : ModifyNoteIntention()
    data class GotFocus(val focusView: ViewFocus) : ModifyNoteIntention()
    object SaveNote : ModifyNoteIntention()
    object BackPressed : ModifyNoteIntention()
    object MinimizedPressed : ModifyNoteIntention()
    data class RemoveNote(val noteId: Int, val displayDialog: Boolean = false) : ModifyNoteIntention()
    object Undo : ModifyNoteIntention()
    object Redo : ModifyNoteIntention()
    object RevealSearch : ModifyNoteIntention()
    object OpenMenu : ModifyNoteIntention()
}