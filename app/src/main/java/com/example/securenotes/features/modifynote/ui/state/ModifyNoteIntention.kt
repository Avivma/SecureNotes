package com.example.securenotes.features.modifynote.ui.state

import com.example.securenotes.features.modifynote.ui.utils.ModifyNoteViewsManager.ViewFocus

sealed class ModifyNoteIntention {
    object FetchData : ModifyNoteIntention()
    data class GotFocus(val focusView: ViewFocus) : ModifyNoteIntention()
    object SaveNote : ModifyNoteIntention()
    object BackPressed : ModifyNoteIntention()
    object MinimizedPressed : ModifyNoteIntention()
    data class RemoveNote(val noteId: Int, val displayDialog: Boolean = false) : ModifyNoteIntention()
    object Undo : ModifyNoteIntention()
    object Redo : ModifyNoteIntention()
}