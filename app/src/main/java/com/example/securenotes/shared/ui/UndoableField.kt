package com.example.securenotes.shared.ui

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

@MainThread
class UndoableField<T>(initialValue: T) {
    val current: MutableLiveData<T> = MutableLiveData(initialValue)
    private var originValue: T = initialValue
    private var previousValue: T = initialValue

    private val undoStack = ArrayDeque<T>()
    private val redoStack = ArrayDeque<T>()

    private val _canUndo = MediatorLiveData<Boolean>()
    val canUndo: LiveData<Boolean> get() = _canUndo

    private val _canRedo = MediatorLiveData<Boolean>()
    val canRedo: LiveData<Boolean> get() = _canRedo

    val hasChanged: MediatorLiveData<Boolean> = MediatorLiveData()

    init {
        hasChanged.addSource(current) {
            checkChange()
            update()
        }
        hasChanged.value = false
        _canUndo.value = false
        _canRedo.value = false
    }

    private fun update() {
        val oldValue = previousValue
        if (oldValue != current.value) {
            undoStack.addLast(oldValue!!)
            redoStack.clear()
            updateFlags()
            previousValue = current.value!!
        }
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            val currentValue = current.value!!
            redoStack.addLast(currentValue)
            val updatedValue = undoStack.removeLast()
            previousValue = updatedValue
            updateFlags()
            current.value = updatedValue
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            val currentValue = current.value!!
            undoStack.addLast(currentValue)
            val updatedValue = redoStack.removeLast()
            previousValue = updatedValue
            updateFlags()
            current.value = updatedValue
        }
    }

    fun get(): T = current.value!!

    private fun updateFlags() {
        _canUndo.value = undoStack.isNotEmpty()
        _canRedo.value = redoStack.isNotEmpty()
    }

    private fun checkChange() {
        hasChanged.value = current.value != originValue
    }

    fun set(newOrigin: T) {
        undoStack.clear()
        redoStack.clear()
        originValue = newOrigin
        previousValue = newOrigin
        hasChanged.value = false
        updateFlags()
        current.value = newOrigin
    }

    fun updateOrigin() {
        originValue = current.value!!
        hasChanged.value = false
    }
}