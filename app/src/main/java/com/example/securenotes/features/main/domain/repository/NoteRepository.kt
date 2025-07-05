package com.example.securenotes.features.main.domain.repository

import com.example.securenotes.features.main.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(): Flow<List<Note>>
}