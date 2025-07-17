package com.example.securenotes.shared.search.domain.repository

import com.example.securenotes.shared.search.domain.model.SearchDialogNoteModel

interface SearchNotesRepository {
    suspend fun findNotesMatchesQuery(query: String): List<SearchDialogNoteModel>
}