package com.example.securenotes.features.main.domain.usecase

import com.example.securenotes.features.main.domain.model.Note
import com.example.securenotes.features.main.domain.repository.MainNoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveNotesUseCase @Inject constructor(private val repo: MainNoteRepository) {
    operator fun invoke(): Flow<List<Note>> {
        val notes = repo.getNotes()
        return notes
    }
}