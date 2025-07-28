package com.example.securenotes.shared.ui

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.securenotes.core.utils.L

@MainThread
class UndoableField<T>(initialValue: T) {
    private val _current: MutableLiveData<T> = MutableLiveData(initialValue)
    val current: LiveData<T> get() = _current

    private var originValue: T = initialValue

    private val undoStack = ArrayDeque<T>()
    private val redoStack = ArrayDeque<T>()

    private val _canUndo = MediatorLiveData<Boolean>()
    val canUndo: LiveData<Boolean> get() = _canUndo

    private val _canRedo = MediatorLiveData<Boolean>()
    val canRedo: LiveData<Boolean> get() = _canRedo

    val hasChanged: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        hasChanged.addSource(_current) {
            checkChange()
        }
        hasChanged.value = false
        _canUndo.value = false
        _canRedo.value = false
    }

    fun update(newValue: T) {
        val oldValue = _current.value
        if (oldValue != newValue) {
            L.i("$TAG - update() - oldValue($oldValue) != current.value(${_current.value})")
            undoStack.addLast(oldValue!!)
            _current.value = newValue
            redoStack.clear()
            updateFlags()
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            L.i("$TAG - undo() - not empty")
            val currentValue = _current.value!!
            redoStack.addLast(currentValue)
            val updatedValue = undoStack.removeLast()
            updateFlags()
            _current.value = updatedValue
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val currentValue = _current.value!!
            undoStack.addLast(currentValue)
            val updatedValue = redoStack.removeLast()
            updateFlags()
            _current.value = updatedValue
        }
    }

    fun get(): T = _current.value!!

    private fun updateFlags() {
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
    }

    private fun checkChange() {
        hasChanged.value = _current.value != originValue
    }

    fun set(newOrigin: T) {
        undoStack.clear()
        redoStack.clear()
        originValue = newOrigin
        hasChanged.value = false
        updateFlags()
        _current.value = newOrigin
    }

    fun updateOrigin() {
        originValue = _current.value!!
        hasChanged.value = false
    }

    companion object {
        private const val TAG = "UndoableField"
    }
}