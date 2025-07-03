package com.example.securenotes.features.main.data

import com.example.securenotes.features.main.data.db.NoteDao
import com.example.securenotes.features.main.data.db.toNote
import com.example.securenotes.features.main.domain.model.Note
import com.example.securenotes.features.main.domain.repository.NoteRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NoteRepositoryImp @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override suspend fun getNotes(): List<Note> {
        return noteDao.getNotes().first().map { it.toNote() }
    }
}