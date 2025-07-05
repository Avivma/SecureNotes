package com.example.securenotes.features.main.data.model

import com.example.securenotes.features.main.domain.model.Note
import com.example.securenotes.shared.data.db.NoteEntity

object MainNotesConverter {
    fun domainToDb(note: Note): NoteEntity {
        return NoteEntity(
            id = note.id,
            title = note.title,
            content = note.content
        )
    }

    fun dbToDomain(dbNote: NoteEntity): Note {
        return Note(
            id = dbNote.id,
            title = dbNote.title,
            content = dbNote.content
        )
    }
}