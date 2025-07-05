package com.example.securenotes.features.main.data.model

import com.example.securenotes.features.main.data.db.NoteEntity
import com.example.securenotes.features.main.domain.model.Note

object DBNoteConverter {
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