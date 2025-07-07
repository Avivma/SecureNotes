package com.example.securenotes.shared.removenote.data

import com.example.securenotes.shared.domain.repository.NoteRepository
import com.example.securenotes.shared.removenote.domain.repository.RemoveNoteRepository
import javax.inject.Inject

class RemoveNoteRepositoryImp @Inject constructor(
    private val commonNoteRepo: NoteRepository,
): RemoveNoteRepository {
    override suspend fun deleteNote(id: Int): Boolean {
        return commonNoteRepo.deleteNote(id) > 0
    }
}