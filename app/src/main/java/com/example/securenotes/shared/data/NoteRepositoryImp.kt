package com.example.securenotes.shared.data

import com.example.securenotes.features.addnote.data.model.AddNoteConverter
import com.example.securenotes.features.addnote.domain.model.AddNoteModel
import com.example.securenotes.features.addnote.domain.repository.AddNoteRepository
import com.example.securenotes.features.main.data.model.MainNotesConverter
import com.example.securenotes.features.main.domain.model.Note
import com.example.securenotes.features.main.domain.repository.MainNoteRepository
import com.example.securenotes.shared.data.db.NoteDao
import com.example.securenotes.shared.removenote.domain.repository.RemoveNoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImp @Inject constructor(
    private val noteDao: NoteDao
) : MainNoteRepository, AddNoteRepository, RemoveNoteRepository {
    override fun getNotes(): Flow<List<Note>> {
        return noteDao.getNotes().map { notes -> notes.map(MainNotesConverter::dbToDomain) }
    }

    override suspend fun insertNote(noteModel: AddNoteModel): Int {
        val note = AddNoteConverter.domainToDb(noteModel)
        return noteDao.insertNote(note).toInt()
    }

    override suspend fun deleteNote(id: Int): Boolean {
        return noteDao.deleteNote(id) > 0
    }
}