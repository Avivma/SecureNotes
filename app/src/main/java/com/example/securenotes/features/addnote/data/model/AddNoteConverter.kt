package com.example.securenotes.features.addnote.data.model

import com.example.securenotes.features.addnote.domain.model.AddNoteModel
import com.example.securenotes.shared.data.db.NoteEntity

object AddNoteConverter {
    fun domainToDb(note: AddNoteModel): NoteEntity {
        return NoteEntity(
            id = note.id,
            title = note.title,
            content = note.content,
            lastModified = note.updatedAt
        )
    }

    fun dbToDomain(dbNote: NoteEntity): AddNoteModel {
        return AddNoteModel(
            id = dbNote.id,
            title = dbNote.title,
            content = dbNote.content,
            updatedAt = dbNote.lastModified
        )
    }
}