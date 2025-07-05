package com.example.securenotes.features.addnote.domain.usecase

import com.example.securenotes.features.addnote.domain.model.AddNoteModel
import com.example.securenotes.features.addnote.domain.repository.AddNoteRepository
import javax.inject.Inject

class AddNoteUseCase @Inject constructor(
    private val repository: AddNoteRepository
) {
    suspend operator fun invoke(note: AddNoteModel): Int {
        return repository.insertNote(note)
    }
}
