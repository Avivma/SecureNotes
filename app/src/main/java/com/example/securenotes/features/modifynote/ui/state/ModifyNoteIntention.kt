package com.example.securenotes.features.modifynote.ui.state

import com.example.securenotes.features.modifynote.domain.model.ModifyNoteModel

sealed class ModifyNoteIntention {
    object FetchData : ModifyNoteIntention()
    object SaveNote : ModifyNoteIntention()
    object BackPressed : ModifyNoteIntention()
    data class RemoveNote(val note: ModifyNoteModel) : ModifyNoteIntention()
}