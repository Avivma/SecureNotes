package com.example.securenotes.features.main.data.db

import com.example.securenotes.features.main.domain.model.Note

fun NoteEntity.toNote(): Note {
    return Note(
        id = id,
        title = title,
        content = content
    )
}
