package com.example.securenotes.features.main.data

import com.example.securenotes.features.main.data.db.NoteDao
import com.example.securenotes.features.main.data.model.DBNoteConverter
import com.example.securenotes.features.main.domain.model.Note
import com.example.securenotes.features.main.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImp @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getNotes(): Flow<List<Note>> {
        return noteDao.getNotes().map { notes -> notes.map(DBNoteConverter::dbToDomain) }
    }
}