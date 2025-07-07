package com.example.securenotes.features.modifynote.domain.usecase

import com.example.securenotes.features.modifynote.domain.model.ModifyNoteModel
import com.example.securenotes.features.modifynote.domain.repository.ModifyNoteRepository
import javax.inject.Inject

class GetNoteUseCase @Inject constructor(
    private val repository: ModifyNoteRepository
) {
    suspend operator fun invoke(noteId: Int): ModifyNoteModel? {
        return repository.getNoteById(noteId)
    }
}