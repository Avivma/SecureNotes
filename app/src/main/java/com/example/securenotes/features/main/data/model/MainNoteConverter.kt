package com.example.securenotes.features.main.data.model

import com.example.securenotes.features.main.domain.model.MainNote
import com.example.securenotes.shared.domain.model.NoteModel

object MainNoteConverter {
/*
    fun mainToCommon(note: MainNote): NoteModel {
        return NoteModel(
            id = note.id,
            title = note.title,
            content = note.content,
            updatedAt = System.currentTimeMillis() // Assuming updatedAt is the current time for simplicity
        )
    }
*/

    fun commonToMain(noteModel: NoteModel): MainNote {
        return MainNote(
            id = noteModel.id,
            title = noteModel.title,
            content = noteModel.content
        )
    }
}