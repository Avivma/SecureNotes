package com.example.securenotes.features.modifynote.data.model

import com.example.securenotes.features.modifynote.domain.model.ModifyNoteModel
import com.example.securenotes.shared.domain.model.NoteModel

object ModifyNoteConverter {
    fun noteToCommon(note: ModifyNoteModel): NoteModel {
        return NoteModel(
            id = note.id,
            title = note.title,
            content = note.content,
            updatedAt = note.updatedAt,
            creationTime = System.currentTimeMillis()
        )
    }

    fun commonToNote(noteModel: NoteModel): ModifyNoteModel {
        return ModifyNoteModel(
            id = noteModel.id,
            title = noteModel.title,
            content = noteModel.content,
            updatedAt = noteModel.updatedAt
        )
    }
}