package com.example.securenotes.features.main.ui

import IODispatcher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _state = MutableStateFlow<MainState>(MainState.Waiting)
    val state: StateFlow<MainState> = _state
    private var dbJob: Job? = null

    fun action(intention: MainIntention) {
        viewModelScope.launch(ioDispatcher) {
            when (intention) {
                is MainIntention.LoadNotes -> loadNotes()
                is MainIntention.RemoveNote -> removeNote(intention.note, intention.displayDialog)
                is MainIntention.GoToAddNoteScreen -> goToAddNoteScreen()
            }
        }
    }

    fun startObservingDb() {
        if (dbJob?.isActive == true) return
        dbJob = viewModelScope.launch {
            observeNotesUseCase()
                .map { it.map(UiNoteConverter::fromDomain) }
                .collect { notes ->
                    _state.value = MainState.DisplayNotes(notes)
                }
        }
    }

    fun stopObservingDb() {
        dbJob?.cancel()
        dbJob = null
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _state.value = MainState.Waiting
            delay(1000)
            val notes = getNotesUseCase().map { UiNoteConverter.fromDomain(it) }
            _state.value = MainState.DisplayNotes(notes)
        }
    }

    private suspend fun removeNote(note: UiNote, displayDialog: Boolean) {
        if (displayDialog) {
            _state.value = MainState.DisplayRemoveQuestion(note)
            return
        }

        val noteRemoved = removeNoteUseCase(note.id)
        if (!noteRemoved) {
            _state.value = MainState.Error("Failed to remove note")
            L.e("Failed to remove note with id: ${note.id}")
            return
        }
        _state.value = MainState.NoteRemoved(note.title)
    }

    private fun goToAddNoteScreen() {
        _state.value = MainState.Navigation.NavigateToAddNote
    }


}
