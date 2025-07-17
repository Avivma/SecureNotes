package com.example.securenotes.shared.domain.repository

import com.example.securenotes.shared.domain.model.NoteModel
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotesStream(): Flow<List<NoteModel>>
    suspend fun getNotes(): List<NoteModel>
    suspend fun getNote(noteId: Int): NoteModel?
    suspend fun insertNote(noteModel: NoteModel): Long
    suspend fun updateNote(noteModel: NoteModel): Int
    suspend fun deleteNote(id: Int): Int
    suspend fun searchNotes(query: String): List<NoteModel>
}