package com.example.securenotes.shared.data.model

import com.example.securenotes.shared.data.db.NoteEntity
import com.example.securenotes.shared.domain.model.NoteModel

object NoteModelConverter {
    fun domainToDb(note: NoteModel): NoteEntity {
        return NoteEntity(
            id = note.id,
            title = note.title,
            content = note.content,
            lastModified = note.updatedAt,
            createdAt = note.creationTime
        )
    }

    fun dbToDomain(dbNote: NoteEntity): NoteModel {
        return NoteModel(
            id = dbNote.id,
            title = dbNote.title,
            content = dbNote.content,
            updatedAt = dbNote.lastModified,
            creationTime = dbNote.createdAt
        )
    }
}