package com.example.securenotes.features.main.ui.model

import com.example.securenotes.features.main.domain.model.Note

object UiNoteConverter {

    fun fromDomain(note: Note): UiNote {
        return UiNote(
            id = note.id,
            title = note.title,
            content = note.content
        )
    }

    fun toDomain(uiNote: UiNote): Note {
        return Note(
            id = uiNote.id,
            title = uiNote.title,
            content = uiNote.content
        )
    }
}