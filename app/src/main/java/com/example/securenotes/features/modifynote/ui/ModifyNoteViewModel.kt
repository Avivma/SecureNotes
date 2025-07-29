package com.example.securenotes.features.modifynote.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securenotes.core.di.IODispatcher
import com.example.securenotes.core.di.MainDispatcher
import com.example.securenotes.core.utils.L
import com.example.securenotes.features.modifynote.domain.model.ModifyNoteModel
import com.example.securenotes.features.modifynote.domain.usecase.GetNoteUseCase
import com.example.securenotes.features.modifynote.domain.usecase.SaveNoteUseCase
import com.example.securenotes.features.modifynote.ui.state.ModifyNoteIntention
import com.example.securenotes.features.modifynote.ui.state.ModifyNoteState
import com.example.securenotes.features.modifynote.ui.utils.ContinuousButtonOperationManager
import com.example.securenotes.features.modifynote.ui.utils.ModifyNoteViewsManager
import com.example.securenotes.features.modifynote.ui.utils.ModifyNoteViewsManager.ViewData
import com.example.securenotes.features.modifynote.ui.utils.ModifyNoteViewsManager.ViewFocus
import com.example.securenotes.shared.removenote.domain.usecase.RemoveNoteUseCase
import com.example.securenotes.shared.utils.Consts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ModifyNoteViewModel @Inject constructor(
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val removeNoteUseCase: RemoveNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase
) : ViewModel() {

    var state: SharedFlow<ModifyNoteState> = MutableSharedFlow()
    private var _state: MutableSharedFlow<ModifyNoteState> =
        state as MutableSharedFlow<ModifyNoteState>

    private val viewsManager = ModifyNoteViewsManager()

    var data: ViewData = viewsManager.getData()

    private var justBeenDeleted = false
    private var currentNoteId: Int = Consts.NO_ID

    private var continuousUndoManager: ContinuousButtonOperationManager
    private var continuousRedoManager: ContinuousButtonOperationManager

    init{
        this.continuousUndoManager = ContinuousButtonOperationManager(scope = viewModelScope, continuouslyAdditionCondition = data.canUndo)
        this.continuousRedoManager = ContinuousButtonOperationManager(scope = viewModelScope, continuouslyAdditionCondition = data.canRedo)
    }

    fun setNoteId(noteId: Int) {
        this.currentNoteId = noteId
    }

    fun action(intention: ModifyNoteIntention) {
        viewModelScope.launch(ioDispatcher) {
            when (intention) {
                is ModifyNoteIntention.FetchData -> fetchData(intention.searchText)
                is ModifyNoteIntention.GotFocus -> gotFocus(intention.focusView)
                is ModifyNoteIntention.TitleChanged -> titleChanged(intention.text)
                is ModifyNoteIntention.ContentChanged -> contentChanged(intention.text)
                ModifyNoteIntention.SaveNote -> saveNote()
                ModifyNoteIntention.MinimizedPressed,
                ModifyNoteIntention.BackPressed -> backPressed()

                is ModifyNoteIntention.RemoveNote -> removeNote(
                    intention.noteId,
                    intention.displayDialog
                )

                ModifyNoteIntention.Undo -> undo()
                ModifyNoteIntention.UndoContinuously -> undoContinuously()
                ModifyNoteIntention.UndoStop -> undoStop()
                ModifyNoteIntention.Redo -> redo()
                ModifyNoteIntention.RedoContinuously -> redoContinuously()
                ModifyNoteIntention.RedoStop -> redoStop()
                ModifyNoteIntention.OpenMenu -> openMenu()
                ModifyNoteIntention.RevealSearch -> revealSearch()
                ModifyNoteIntention.HideSearch -> hideSearch()
                ModifyNoteIntention.ClearSearch -> clearSearch()
            }
        }
    }

    private suspend fun titleChanged(text: String) {
        withContext(mainDispatcher) {
            viewsManager.title.update(text)
        }
    }

    private suspend fun contentChanged(text: String) {
        withContext(mainDispatcher) {
            viewsManager.content.update(text)
        }
    }

    private suspend fun fetchData(searchText: String) {
        if (isNoteIdValid(currentNoteId)) {
            try {
                val note: ModifyNoteModel? = getNoteUseCase(currentNoteId)
                if (note != null) {
                    setData(note.title, note.content, note.updatedAt, note.createdAt)
                    if (searchText.isNotEmpty()) {
                        delay(150) // I used this hack to avoid rise-condition with the search bar
                        setSearchParams(true)
                        _state.emit(ModifyNoteState.DisplaySearchBarWithQuery(searchText))
                    }
                } else {
                    _state.emit(ModifyNoteState.Error("Note not found"))
                }
            } catch (e: Exception) {
                _state.emit(ModifyNoteState.Error("Failed to load note"))
            }
        }
    }

    private suspend fun gotFocus(focusView: ViewFocus) {
        withContext(mainDispatcher) {
            viewsManager.gotFocus(focusView)
        }
    }

    private suspend fun saveNote() {
        var note: ModifyNoteModel = getModelWithCurrentData()
        if (justBeenDeleted) {
            L.i("saveNote - Note has just been deleted - DO NOT SAVE. Current note = $note")
            return
        }

        try {
            val resultedNoteId = saveNoteUseCase(note)
            if (isNoteIdValid(resultedNoteId)) {
                L.i("saveNote - save note = $note")
                currentNoteId = resultedNoteId
                updateData(note)
                _state.emit(ModifyNoteState.NoteSaved)
            } else {
                L.i("Error saving note. Latest noteId = ${currentNoteId}")
                _state.emit(ModifyNoteState.Error("Failed to save note"))
            }
        } catch (e: Exception) {
            L.e("Error saving note: ${e.message}")
            _state.emit(ModifyNoteState.Error("Failed to save note"))
        }
    }

    private suspend fun backPressed() {
        if (hasChangedOccurred()) saveNote()
        _state.emit(ModifyNoteState.Navigation.NavigateBack)
    }

    private suspend fun removeNote(noteId: Int, displayDialog: Boolean) {
        if (!isNoteIdValid(noteId)) {
            justBeenDeleted = true
            _state.emit(ModifyNoteState.Navigation.NavigateBack)
            return
        }

        val note = getModelWithCurrentData()

        if (displayDialog) {
            _state.emit(ModifyNoteState.DisplayRemoveQuestion(note))
            return
        }

        val noteRemoved = removeNoteUseCase(note.id)
        if (!noteRemoved) {
            _state.emit(ModifyNoteState.Error("Failed to remove note"))
            L.e("Failed to remove note with id: ${note.id}")
            return
        }
        justBeenDeleted = true
        _state.emit(ModifyNoteState.NoteRemoved(note.title))
        _state.emit(ModifyNoteState.Navigation.NavigateBack)
    }

    private suspend fun undo() {
        withContext(mainDispatcher) {
            viewsManager.undo()
        }
    }

    private suspend fun redo() {
        withContext(mainDispatcher) {
            viewsManager.redo()
        }
    }

    private fun undoContinuously() {
        continuousUndoManager.start {
            viewsManager.undo()
        }
    }

    private fun undoStop() {
        continuousUndoManager.stop()
    }

    private fun redoContinuously() {
        continuousRedoManager.start {
            viewsManager.redo()
        }
    }

    private fun redoStop() {
        continuousRedoManager.stop()
    }

    private suspend fun openMenu() {
        _state.emit(ModifyNoteState.DisplayMenu)
    }

    private suspend fun revealSearch() {
        data.isSearchBarVisible = true
        _state.emit(ModifyNoteState.DisplaySearchBar)
    }
    private suspend fun hideSearch() {
        setSearchParams(false)
        _state.emit(ModifyNoteState.HideSearchBar)
    }
    private suspend fun clearSearch() {
        setSearchActive(false)
        _state.emit(ModifyNoteState.ClearSearch)
    }

    private fun setSearchParams(value: Boolean) {
        data.isSearchBarVisible = value
        setSearchActive(value)
    }
    fun setSearchActive(active: Boolean) {
        data.searchActive = active
    }

    private fun hasChangedOccurred(): Boolean = data.isSaveEnabled.value == true

    private fun isNoteIdValid(id: Int): Boolean = id != Consts.NO_ID

    private fun getModelWithCurrentData() = ModifyNoteModel(
        id = currentNoteId,
        title = data.title.value.orEmpty(),
        content = data.content.value.orEmpty(),
        updatedAt = System.currentTimeMillis(),
        createdAt = viewsManager.createdTimestamp
    )

    private suspend fun updateData(note: ModifyNoteModel) {
        withContext(mainDispatcher) {
            viewsManager.updateOrigin()
            viewsManager.updateModified(note)
        }
    }

    private suspend fun setData(title: String, content: String, updatedAt: Long, createdAt: Long) {
        withContext(mainDispatcher) {
            viewsManager.set(title, content, updatedAt, createdAt)
        }
    }

    fun isSearchActive(): Boolean = data.searchActive

    companion object {
        private const val TAG = "ModifyNoteViewModel"
    }
}


