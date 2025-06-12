package com.example.securenotes.data.repository

import com.example.securenotes.data.local.NoteDao
import com.example.securenotes.data.model.Note
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val ioDispatcher: CoroutineDispatcher
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    override suspend fun getNoteById(id: String): Note? = withContext(ioDispatcher) {
        noteDao.getNoteById(id)
    }

    override suspend fun insertNote(note: Note) = withContext(ioDispatcher) {
        noteDao.insertNote(note)
    }
    
    override suspend fun updateNote(note: Note) = withContext(ioDispatcher) {
        noteDao.updateNote(note)
    }

    override suspend fun deleteNote(note: Note) = withContext(ioDispatcher) {
        noteDao.deleteNote(note)
    }

    override suspend fun deleteAllNotes() = withContext(ioDispatcher) {
        noteDao.deleteAllNotes()
    }
}
