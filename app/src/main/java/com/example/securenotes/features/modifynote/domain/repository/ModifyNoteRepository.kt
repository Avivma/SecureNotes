package com.example.securenotes.features.modifynote.domain.repository

import com.example.securenotes.features.modifynote.domain.model.ModifyNoteModel

interface ModifyNoteRepository {
    suspend fun getNoteById(noteId: Int): ModifyNoteModel?
    suspend fun insertNote(note: ModifyNoteModel): Int
    suspend fun updateNote(note: ModifyNoteModel): Int
}