package com.example.securenotes.features.main.ui.model

import com.example.securenotes.features.main.domain.model.MainNote

object UiNoteConverter {

    fun fromDomain(note: MainNote): UiNote {
        return UiNote(
            id = note.id,
            title = note.title,
            content = note.content
        )
    }

    fun toDomain(uiNote: UiNote): MainNote {
        return MainNote(
            id = uiNote.id,
            title = uiNote.title,
            content = uiNote.content
        )
    }
}