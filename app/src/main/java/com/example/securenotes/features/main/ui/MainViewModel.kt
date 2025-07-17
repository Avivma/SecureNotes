package com.example.securenotes.features.main.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securenotes.core.di.IODispatcher
import com.example.securenotes.core.utils.L
import com.example.securenotes.features.main.domain.usecase.GetNotesUseCase
import com.example.securenotes.features.main.domain.usecase.ObserveNotesUseCase
import com.example.securenotes.features.main.ui.model.UiNote
import com.example.securenotes.features.main.ui.model.UiNoteConverter
import com.example.securenotes.features.main.ui.state.MainIntention
import com.example.securenotes.features.main.ui.state.MainState
import com.example.securenotes.shared.removenote.domain.usecase.RemoveNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val observeNotesUseCase: ObserveNotesUseCase,
    private val getNotesUseCase: GetNotesUseCase,
    private val removeNoteUseCase: RemoveNoteUseCase
) : ViewModel() {

    var state: LiveData<MainState> = MutableLiveData()
    private var _state: MutableLiveData<MainState> = state as MutableLiveData<MainState>

    fun observeStateLiveData(owner: LifecycleOwner, observer: Observer<MainState>) {
        state = MutableLiveData<MainState>()
        _state = state as MutableLiveData<MainState>
        state.observe(owner, observer)
    }

    private var dbJob: Job? = null

    fun action(intention: MainIntention) {
        viewModelScope.launch(ioDispatcher) {
            when (intention) {
                is MainIntention.LoadNotes -> loadNotes()
                is MainIntention.RemoveNote -> removeNote(intention.note, intention.displayDialog)
                is MainIntention.AddNote -> goToAddNoteScreen()
                is MainIntention.EditNote -> goToEditNoteScreen(intention.note)
                MainIntention.OpenMenu -> _state.postValue(MainState.OpenMenu)
                MainIntention.OpenSearch -> goToSearchDialog()
            }
        }
    }

    fun startObservingDb() {
        if (dbJob?.isActive == true) return
        dbJob = viewModelScope.launch {
            observeNotesUseCase()
                .map { it.map(UiNoteConverter::fromDomain) }
                .collect { notes ->
                    L.i("$TAG - startObservingDb - collect notes: $notes")
                    _state.postValue(MainState.DisplayNotes(notes))
                }
        }
    }

    fun stopObservingDb() {
        dbJob?.cancel()
        dbJob = null
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _state.postValue(MainState.Waiting)
            delay(1000)
            val notes = getNotesUseCase().map { UiNoteConverter.fromDomain(it) }
            _state.postValue(MainState.DisplayNotes(notes))
        }
    }

    private suspend fun removeNote(note: UiNote, displayDialog: Boolean) {
        if (displayDialog) {
            _state.postValue(MainState.DisplayRemoveQuestion(note))
            return
        }

        val noteRemoved = removeNoteUseCase(note.id)
        if (!noteRemoved) {
            _state.postValue(MainState.Error("Failed to remove note"))
            L.e("Failed to remove note with id: ${note.id}")
            return
        }
        _state.postValue(MainState.NoteRemoved(note.title))
    }

    private fun goToAddNoteScreen() {
        _state.postValue(MainState.Navigation.NavigateToModifyNote())
    }

    private fun goToEditNoteScreen(note: UiNote) {
        _state.postValue(MainState.Navigation.NavigateToModifyNote(note))
    }

    private fun goToSearchDialog() {
        _state.postValue(MainState.Navigation.NavigateToSearchDialog)
    }

    companion object {
        private const val TAG = "MainViewModel"}
}
