package com.example.securenotes.features.modifynote.ui.state

import com.example.securenotes.features.modifynote.domain.model.ModifyNoteModel

sealed class ModifyNoteState {
    object NoteSaved : ModifyNoteState()
    data class DisplayRemoveQuestion(val note: ModifyNoteModel): ModifyNoteState()
    data class NoteRemoved(val title: String) : ModifyNoteState()
    data class Error(val message: String) : ModifyNoteState()
    object DisplayMenu: ModifyNoteState()
    object DisplaySearchBar: ModifyNoteState()

    sealed class Navigation: ModifyNoteState() {
        object NavigateBack: ModifyNoteState.Navigation()
    }
}