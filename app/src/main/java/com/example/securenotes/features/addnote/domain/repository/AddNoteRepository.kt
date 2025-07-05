package com.example.securenotes.features.addnote.domain.repository

import com.example.securenotes.features.addnote.domain.model.AddNoteModel

interface AddNoteRepository {
    suspend fun insertNote(note: AddNoteModel): Int
}