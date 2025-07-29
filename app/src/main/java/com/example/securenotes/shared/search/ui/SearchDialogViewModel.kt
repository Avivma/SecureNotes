package com.example.securenotes.shared.search.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.securenotes.core.di.IODispatcher
import com.example.securenotes.core.utils.L
import com.example.securenotes.shared.search.domain.model.SearchDialogNoteModel
import com.example.securenotes.shared.search.domain.usecase.SearchNotesUseCase
import com.example.securenotes.shared.search.ui.state.SearchDialogIntention
import com.example.securenotes.shared.search.ui.state.SearchDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchDialogViewModel  @Inject constructor(
    application: Application,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val searchNotesUseCase: SearchNotesUseCase
) : AndroidViewModel(application) {
    var state: SharedFlow<SearchDialogState> = MutableSharedFlow()
    private var _state: MutableSharedFlow<SearchDialogState> = state as MutableSharedFlow<SearchDialogState>

    private var hasSearchResult = false

    val searchText = object : MutableLiveData<String>("") {
        override fun setValue(value: String?) {
            super.setValue(value)
            hasSearchText()
        }
    }

    fun action(intention: SearchDialogIntention) {
        viewModelScope.launch(ioDispatcher) {
            when (intention) {
                is SearchDialogIntention.NoteClicked -> noteClicked(intention.note)
                SearchDialogIntention.ClearSearch -> clearSearch()
                SearchDialogIntention.Search -> search()
                SearchDialogIntention.RefreshSearch -> refreshSearch()
            }
        }
    }

    private fun clearSearch() {
        hasSearchResult = false
        searchText.postValue("")
    }

    private fun hasSearchText() {
        L.i("$TAG - hasSearchText: ${searchText.value}")
        viewModelScope.launch(ioDispatcher) {
            val text = searchText.value!!
            _state.emit(SearchDialogState.ToggleSearchButtons(text.isNotEmpty()))
            if (text.isEmpty()) {
                _state.emit(SearchDialogState.DisplayNotes(emptyList<SearchDialogNoteModel>()))
            }
        }
    }

    private suspend fun search() {
        hasSearchResult = false
        if (searchText.value!!.isEmpty()) {
            _state.emit(SearchDialogState.DisplayMessage.SearchEmpty)
            return
        }
        val notes: List<SearchDialogNoteModel> = searchNotesUseCase(searchText.value!!)
        if (notes.isEmpty()) {
            _state.emit(SearchDialogState.DisplayMessage.NoResults)
        } else {
            hasSearchResult = true
            _state.emit(SearchDialogState.DisplayNotes(notes))
        }
    }

    private suspend fun refreshSearch() {
        if (hasSearchResult) {
            search()
        }
    }

    private suspend fun noteClicked(model: SearchDialogNoteModel) {
        _state.emit(SearchDialogState.Navigation.NavigateToModifyNote(model.id, searchText.value!!))
    }

    companion object {
        private const val TAG = "SearchDialogViewModel"
    }

}