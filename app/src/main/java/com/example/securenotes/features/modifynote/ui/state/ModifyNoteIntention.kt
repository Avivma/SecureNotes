package com.example.securenotes.features.modifynote.ui.state

sealed class ModifyNoteIntention {
    object FetchData : ModifyNoteIntention()
    object SaveNote : ModifyNoteIntention()
    object BackPressed : ModifyNoteIntention()
    object MinimizedPressed : ModifyNoteIntention()
    data class RemoveNote(val noteId: Int, val displayDialog: Boolean = false) : ModifyNoteIntention()
}