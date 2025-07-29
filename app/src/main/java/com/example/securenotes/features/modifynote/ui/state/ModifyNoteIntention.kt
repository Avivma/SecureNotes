package com.example.securenotes.features.modifynote.ui.state

import com.example.securenotes.features.modifynote.ui.utils.ModifyNoteViewsManager.ViewFocus

sealed class ModifyNoteIntention {
    data class FetchData(val searchText: String = "") : ModifyNoteIntention()
    data class GotFocus(val focusView: ViewFocus) : ModifyNoteIntention()
    data class TitleChanged(val text: String) : ModifyNoteIntention()
    data class ContentChanged(val text: String) : ModifyNoteIntention()
    object SaveNote : ModifyNoteIntention()
    object BackPressed : ModifyNoteIntention()
    object MinimizedPressed : ModifyNoteIntention()
    data class RemoveNote(val noteId: Int, val displayDialog: Boolean = false) : ModifyNoteIntention()
    object Undo : ModifyNoteIntention()
    object UndoContinuously : ModifyNoteIntention()
    object UndoStop : ModifyNoteIntention()
    object Redo : ModifyNoteIntention()
    object RedoContinuously : ModifyNoteIntention()
    object RedoStop : ModifyNoteIntention()
    object OpenMenu : ModifyNoteIntention()
    object RevealSearch : ModifyNoteIntention()
    object HideSearch : ModifyNoteIntention()
    object ClearSearch : ModifyNoteIntention()
}