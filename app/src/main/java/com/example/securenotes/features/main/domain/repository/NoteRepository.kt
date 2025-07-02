package com.example.securenotes.features.main.domain.repository

import com.example.securenotes.features.main.domain.model.Note

interface NoteRepository {
    suspend fun getNotes(): List<Note>
}