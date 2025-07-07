package com.example.securenotes.features.modifynote.data

import com.example.securenotes.features.modifynote.data.model.ModifyNoteConverter
import com.example.securenotes.features.modifynote.domain.model.ModifyNoteModel
import com.example.securenotes.features.modifynote.domain.repository.ModifyNoteRepository
import com.example.securenotes.shared.domain.repository.NoteRepository
import javax.inject.Inject

class ModifyNoteRepositoryImp @Inject constructor(
    private val repository: NoteRepository
): ModifyNoteRepository {
    override suspend fun getNoteById(noteId: Int): ModifyNoteModel? {
        val noteModel = repository.getNote(noteId)
        return if (noteModel == null) null else ModifyNoteConverter.commonToNote(noteModel)
    }

    override suspend fun insertNote(note: ModifyNoteModel): Int {
        return repository.insertNote(ModifyNoteConverter.noteToCommon(note)).toInt()
    }

    override suspend fun updateNote(note: ModifyNoteModel): Int {
        return repository.updateNote(ModifyNoteConverter.noteToCommon(note))
    }
}