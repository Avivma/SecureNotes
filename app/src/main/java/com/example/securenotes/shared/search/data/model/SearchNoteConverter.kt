package com.example.securenotes.shared.search.data.model

import com.example.securenotes.shared.domain.model.NoteModel
import com.example.securenotes.shared.search.domain.model.SearchDialogNoteModel

object SearchNoteConverter {
    fun commonToSearchNote(noteModel: NoteModel): SearchDialogNoteModel {
        return SearchDialogNoteModel(
            id = noteModel.id,
            title = noteModel.title,
        )
    }
}