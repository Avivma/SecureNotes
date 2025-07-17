package com.example.securenotes.shared.search.data

import com.example.securenotes.shared.domain.model.NoteModel
import com.example.securenotes.shared.domain.repository.NoteRepository
import com.example.securenotes.shared.search.data.model.SearchNoteConverter
import com.example.securenotes.shared.search.domain.model.SearchDialogNoteModel
import com.example.securenotes.shared.search.domain.repository.SearchNotesRepository
import javax.inject.Inject

class SearchNotesRepositoryImp @Inject constructor(
    private val commonNoteRepo: NoteRepository
): SearchNotesRepository {
    override suspend fun findNotesMatchesQuery(query: String): List<SearchDialogNoteModel> {
        val notesModel: List<NoteModel> = commonNoteRepo.searchNotes(query)
        return notesModel.map { SearchNoteConverter.commonToSearchNote(it) }
    }
}

