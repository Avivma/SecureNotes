package com.example.securenotes.features.modifynote.ui.utils

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.securenotes.features.modifynote.ui.utils.ModifyNoteViewsManager.ViewFocus.ContentFocused
import com.example.securenotes.features.modifynote.ui.utils.ModifyNoteViewsManager.ViewFocus.TitleFocused
import com.example.securenotes.shared.ui.UndoableField

@MainThread
class ModifyNoteViewsManager(titleOrigin: String = "", contentOrigin: String = "") {
    private val title = UndoableField(titleOrigin)
    private val content = UndoableField(contentOrigin)
    private val isSaveEnabled: MediatorLiveData<Boolean> = MediatorLiveData(false)
    private val canRedo: MediatorLiveData<Boolean> = MediatorLiveData(false)
    private val canUndo: MediatorLiveData<Boolean> = MediatorLiveData(false)
    private var focusView: ViewFocus? = null

    data class ViewData(
        val title: MutableLiveData<String>,
        val content: MutableLiveData<String>,
        val isSaveEnabled: LiveData<Boolean>,
        val canRedo: LiveData<Boolean>,
        val canUndo: LiveData<Boolean>
    )

    fun getData(): ViewData {
        return ViewData(
            title = title.current,
            content = content.current,
            isSaveEnabled = isSaveEnabled,
            canRedo = canRedo,
            canUndo = canUndo
        )
    }

    init {
        isSaveEnabled.apply {
            addSource(title.hasChanged) { checkChanges() }
            addSource(content.hasChanged) { checkChanges() }
        }

        canRedo.apply {
            addSource(title.canRedo) { updateCanRedo(focusView) }
            addSource(content.canRedo) { updateCanRedo(focusView) }
        }
        canUndo.apply {
            addSource(title.canUndo) { updateCanUndo(focusView) }
            addSource(content.canUndo) { updateCanUndo(focusView) }
        }
    }

    private fun checkChanges() {
        isSaveEnabled.value = title.hasChanged.value == true || content.hasChanged.value == true
    }

    private fun updateCanRedo(focusView: ViewFocus?) {
        when (focusView) {
            TitleFocused -> canRedo.value = title.canRedo.value == true
            ContentFocused -> canRedo.value = content.canRedo.value == true
            null -> {}
        }
    }

    private fun updateCanUndo(focusView: ViewFocus?) {
        when (focusView) {
            TitleFocused -> canUndo.value = title.canUndo.value == true
            ContentFocused -> canUndo.value = content.canUndo.value == true
            null -> {}
        }
    }

    fun gotFocus(focusView: ViewFocus) {
        this.focusView = focusView
        updateCanRedo(focusView)
        updateCanUndo(focusView)
    }

    fun undo() {
        when (focusView) {
            TitleFocused -> title.undo()
            ContentFocused -> content.undo()
            null -> {}
        }
    }

    fun redo() {
        when (focusView) {
            TitleFocused -> title.redo()
            ContentFocused -> content.redo()
            null -> {}
        }
    }

    fun set(titleOrigin: String, contentOrigin: String) {
        title.set(titleOrigin)
        content.set(contentOrigin)
    }

    fun updateOrigin() {
        title.updateOrigin()
        content.updateOrigin()
    }

    sealed class ViewFocus {
        object TitleFocused : ViewFocus()
        object ContentFocused : ViewFocus()
    }
}