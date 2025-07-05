package com.example.securenotes.features.main.domain.usecase

import com.example.securenotes.features.main.domain.model.Note
import com.example.securenotes.features.main.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNotesUseCase @Inject constructor(private val repo: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> {
        val notes = repo.getNotes()
        return notes
    }
}