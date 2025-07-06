package com.example.securenotes.shared.removenote.domain.usecase

import com.example.securenotes.shared.removenote.domain.repository.RemoveNoteRepository
import javax.inject.Inject

class RemoveNoteUseCase @Inject constructor(
    private val repository: RemoveNoteRepository
) {
    suspend operator fun invoke(id: Int): Boolean {
        if (id <= 0) {
            return true
        }
        return repository.deleteNote(id)
    }
}