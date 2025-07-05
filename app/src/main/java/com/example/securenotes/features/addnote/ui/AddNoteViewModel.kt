package com.example.securenotes.features.addnote.ui

import IODispatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securenotes.core.utils.Consts
import com.example.securenotes.core.utils.ViewFieldTracked
import com.example.securenotes.features.addnote.domain.model.AddNoteModel
import com.example.securenotes.features.addnote.domain.usecase.AddNoteUseCase
import com.example.securenotes.features.addnote.ui.state.AddNoteIntention
import com.example.securenotes.features.addnote.ui.state.AddNoteState
import com.example.securenotes.features.addnote.ui.utils.TrackCombineFields
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AddNoteState>(AddNoteState.Idle)
    val state: StateFlow<AddNoteState> = _state

    object ViewData {
        val title = ViewFieldTracked("")
        val content = ViewFieldTracked("")
        val isSaveEnabled: LiveData<Boolean> = TrackCombineFields(title, content).hasChangedOccurred
    }
    val data = ViewData

    var currentNoteId: Int = Consts.NO_ID

/*    fun initWithDefaults(title: String = "", content: String = "") {
        this.data.title.value = title
        this.data.content.value = content
        changeTracker.setInitialState(Pair(title, content))
        setupChangeDetection()
    }*/

    fun action(intention: AddNoteIntention) {
        viewModelScope.launch(ioDispatcher) {
            when (intention) {
                is AddNoteIntention.SaveNote -> saveNote()
                is AddNoteIntention.BackPressed -> backPressed()
            }
        }
    }

    private suspend fun saveNote() {
        val note = AddNoteModel(
            id = currentNoteId,
            title = data.title.current.value.orEmpty(),
            content = data.content.current.value.orEmpty(),
        )
        try {
            currentNoteId = addNoteUseCase(note)
            _state.value = AddNoteState.NoteSaved
        } catch (e: Exception) {
            _state.value = AddNoteState.Error("Failed to save note")
        }
    }

    private suspend fun backPressed() {
        if (hasChangedOccurred()) saveNote()
        _state.value = AddNoteState.Navigation.NavigateBack
    }

    private fun hasChangedOccurred(): Boolean = data.isSaveEnabled.value == true
}


