package com.example.securenotes.features.main.domain.usecase

import com.example.securenotes.features.main.domain.model.Note
import com.example.securenotes.features.main.domain.repository.NoteRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetNotesUseCase @Inject constructor(private val repo: NoteRepository) {
    suspend operator fun invoke(): List<Note> {
        val notes = repo.getNotes().first()
//        return notes.sortedByDescending { it.updatedAt }
        return notes
    }
}