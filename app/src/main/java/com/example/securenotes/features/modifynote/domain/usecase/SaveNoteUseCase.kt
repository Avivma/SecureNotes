package com.example.securenotes.features.modifynote.domain.usecase

import com.example.securenotes.features.modifynote.domain.model.ModifyNoteModel
import com.example.securenotes.features.modifynote.domain.repository.ModifyNoteRepository
import javax.inject.Inject

class SaveNoteUseCase @Inject constructor(
    private val getNote: GetNoteUseCase,
    private val repository: ModifyNoteRepository
) {
    suspend operator fun invoke(note: ModifyNoteModel): Int {
        val noteFound = getNote(note.id)
        if (noteFound == null) {
            return insertNote(note)
        } else {
            val result = updateNote(note)
            return if (result > 0) note.id else 0
        }
    }

    private suspend fun insertNote(note: ModifyNoteModel): Int {
        return repository.insertNote(note)
    }

    private suspend fun updateNote(note: ModifyNoteModel): Int {
        return repository.updateNote(note)
    }
}
