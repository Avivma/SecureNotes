package com.example.securenotes.shared.data

import com.example.securenotes.shared.data.db.NoteDao
import com.example.securenotes.shared.data.db.NoteEntity
import com.example.securenotes.shared.data.model.NoteModelConverter
import com.example.securenotes.shared.domain.model.NoteModel
import com.example.securenotes.shared.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImp @Inject constructor(
    private val noteDao: NoteDao
) : NoteRepository {
    override fun getNotesStream(): Flow<List<NoteModel>> {
        return noteDao.getNotesStream().map { notes -> notes.map(NoteModelConverter::dbToDomain) }
    }

    override suspend fun getNotes(): List<NoteModel> {
        return noteDao.getNotes().map { notes -> NoteModelConverter.dbToDomain(notes) }
    }

    override suspend fun getNote(noteId: Int): NoteModel? {
        val note = noteDao.getNote(noteId)
        if (note == null) return null
        return NoteModelConverter.dbToDomain(note)
    }

    override suspend fun insertNote(noteModel: NoteModel): Long {
        val note = NoteModelConverter.domainToDb(noteModel)
        return noteDao.insertNote(note)
    }

    override suspend fun updateNote(noteModel: NoteModel): Int {
        return noteDao.updateNote(
            id = noteModel.id,
            title = noteModel.title,
            content = noteModel.content,
            lastModified = noteModel.updatedAt
        )
    }

    override suspend fun deleteNote(id: Int): Int {
        return noteDao.deleteNote(id)
    }

    override suspend fun searchNotes(query: String): List<NoteModel> {
        val notes: List<NoteEntity> = noteDao.searchNotes(query)
        if (notes.isEmpty()) return emptyList()
        return notes.map {NoteModelConverter.dbToDomain(it)}
    }
}