package com.example.securenotes.features.main.ui

import IODispatcher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securenotes.features.main.domain.usecase.GetNotesUseCase
import com.example.securenotes.features.main.ui.model.UiNoteConverter
import com.example.securenotes.features.main.ui.state.MainIntent
import com.example.securenotes.features.main.ui.state.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getNotesUseCase: GetNotesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Waiting)
    val state: StateFlow<MainState> = _state

    fun action(intent: MainIntent) {
        viewModelScope.launch(ioDispatcher) {
            when (intent) {
                is MainIntent.LoadNotes -> loadNotes()
            }
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _state.value = MainState.Waiting
            delay(1000)
            val notes = getNotesUseCase().map { UiNoteConverter.fromDomain(it) }
            _state.value = MainState.DisplayNotes(notes)
        }
    }
}
