package com.example.securenotes.features.modifynote.ui

import IODispatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securenotes.core.utils.L
import com.example.securenotes.features.modifynote.domain.model.ModifyNoteModel
import com.example.securenotes.features.modifynote.domain.usecase.GetNoteUseCase
import com.example.securenotes.features.modifynote.domain.usecase.SaveNoteUseCase
import com.example.securenotes.features.modifynote.ui.state.ModifyNoteIntention
import com.example.securenotes.features.modifynote.ui.state.ModifyNoteState
import com.example.securenotes.features.modifynote.ui.utils.TrackCombineFields
import com.example.securenotes.shared.removenote.domain.usecase.RemoveNoteUseCase
import com.example.securenotes.shared.ui.ViewFieldTracked
import com.example.securenotes.shared.utils.Consts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModifyNoteViewModel @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val removeNoteUseCase: RemoveNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<ModifyNoteState>(ModifyNoteState.Idle)
    val state: StateFlow<ModifyNoteState> = _state

    val data = ViewData

    private var currentNoteId: Int = Consts.NO_ID

    fun setNoteId(noteId: Int) {
        currentNoteId = noteId
    }

    /*    fun initWithDefaults(title: String = "", content: String = "") {
            this.data.title.value = title
            this.data.content.value = content
            changeTracker.setInitialState(Pair(title, content))
            setupChangeDetection()
        }*/

    fun action(intention: ModifyNoteIntention) {
        viewModelScope.launch(ioDispatcher) {
            when (intention) {
                ModifyNoteIntention.FetchData -> fetchData()
                ModifyNoteIntention.SaveNote -> saveNote()
                ModifyNoteIntention.BackPressed -> backPressed()
                is ModifyNoteIntention.RemoveNote -> removeNote(intention.note)
            }
        }
    }

    private suspend fun fetchData() {
        if (currentNoteId != Consts.NO_ID) {
            try {
                val note = getNoteUseCase(currentNoteId)
                if (note != null) {
                    setViewData(note.title, note.content)
                } else {
                    _state.value = ModifyNoteState.Error("Note not found")
                }
            } catch (e: Exception) {
                _state.value = ModifyNoteState.Error("Failed to load note")
            }
        }
    }

    private suspend fun saveNote() {
        val note: ModifyNoteModel = getModelWithCurrentData()
        try {
            val resultedNoteId = saveNoteUseCase(note)
            if (resultedNoteId != Consts.NO_ID) {
                currentNoteId = resultedNoteId
                _state.value = ModifyNoteState.NoteSaved
            } else {
                L.i("Error saving note. Latest noteId = ${currentNoteId}")
                _state.value = ModifyNoteState.Error("Failed to save note")
            }
        } catch (e: Exception) {
            L.e("Error saving note: ${e.message}")
            _state.value = ModifyNoteState.Error("Failed to save note")
        }
    }

    private suspend fun backPressed() {
        if (hasChangedOccurred()) saveNote()
        _state.value = ModifyNoteState.Navigation.NavigateBack
    }

    private suspend fun removeNote(note: ModifyNoteModel) {
        val noteRemoved = removeNoteUseCase(note.id)
        if (!noteRemoved) {
            _state.value = ModifyNoteState.Error("Failed to remove note")
            L.e("Failed to remove note with id: ${note.id}")
            return
        }
        _state.value = ModifyNoteState.NoteRemoved(note.title)
        _state.value = ModifyNoteState.Navigation.NavigateBack
    }

    private fun hasChangedOccurred(): Boolean = data.isSaveEnabled.value == true

    private fun getModelWithCurrentData() = ModifyNoteModel(
        id = currentNoteId,
        title = data.title.current.value.orEmpty(),
        content = data.content.current.value.orEmpty(),
    )

    object ViewData {
        val title = ViewFieldTracked("")
        val content = ViewFieldTracked("")
        val isSaveEnabled: LiveData<Boolean> = TrackCombineFields(title, content).hasChangedOccurred
    }

    private fun setViewData(title: String, content: String) {
        data.title.set(title)
        data.content.set(content)
    }
}


