package com.example.securenotes.shared.search.domain.usecase

import com.example.securenotes.shared.search.domain.model.SearchDialogNoteModel
import com.example.securenotes.shared.search.domain.repository.SearchNotesRepository
import javax.inject.Inject

class SearchNotesUseCase @Inject constructor(
    private val repository: SearchNotesRepository
) {
    suspend operator fun invoke(searchText: String): List<SearchDialogNoteModel> {
        return repository.findNotesMatchesQuery(searchText)
    }
}
