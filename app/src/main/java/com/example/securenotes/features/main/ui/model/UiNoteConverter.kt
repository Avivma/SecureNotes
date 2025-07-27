package com.example.securenotes.features.main.ui.model

import com.example.securenotes.features.main.domain.model.MainNote
import com.example.securenotes.shared.utils.formatDate
import com.example.securenotes.shared.utils.formatDateTime

object UiNoteConverter {

    fun fromDomain(note: MainNote): UiNote {
        val lastModifiedText = note.lastModified.formatDateTime()
        val lastModifiedTextShort = note.lastModified.formatDate()
        return UiNote(
            id = note.id,
            title = note.title,
            content = note.content,
            lastModified = note.lastModified,
            lastModifiedText = lastModifiedText,
            lastModifiedTextShort = lastModifiedTextShort
        )
    }

    fun toDomain(uiNote: UiNote): MainNote {
        return MainNote(
            id = uiNote.id,
            title = uiNote.title,
            content = uiNote.content,
            lastModified = uiNote.lastModified
        )
    }
}