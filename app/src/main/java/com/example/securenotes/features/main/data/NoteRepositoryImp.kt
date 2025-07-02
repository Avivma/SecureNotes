package com.example.securenotes.features.main.data

import com.example.securenotes.features.main.domain.model.Note
import com.example.securenotes.features.main.domain.repository.NoteRepository
import javax.inject.Inject

class NoteRepositoryImp @Inject constructor(): NoteRepository {
    override suspend fun getNotes(): List<Note> {
        val sampleNotes = listOf(
            Note(1, "Secure Note 1", "This is your first secure note."),
            Note(2, "Secure Note 2", "Another confidential note here.")
        )
        return sampleNotes
    }
}