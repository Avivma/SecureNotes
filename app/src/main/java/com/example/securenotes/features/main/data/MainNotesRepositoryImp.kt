package com.example.securenotes.features.main.data

import com.example.securenotes.features.main.data.model.MainNoteConverter
import com.example.securenotes.features.main.domain.model.MainNote
import com.example.securenotes.features.main.domain.repository.MainNoteRepository
import com.example.securenotes.shared.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainNotesRepositoryImp @Inject constructor(
    private val noteRepository: NoteRepository
): MainNoteRepository {
    override fun getNotes(): Flow<List<MainNote>> {
        return noteRepository.getNotesStream().map {
            notes -> notes.map { MainNoteConverter.commonToMain(it)}
        }
    }
}