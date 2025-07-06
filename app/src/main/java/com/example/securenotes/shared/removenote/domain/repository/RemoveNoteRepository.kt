package com.example.securenotes.shared.removenote.domain.repository

interface RemoveNoteRepository {
    suspend fun deleteNote(id: Int): Boolean
}