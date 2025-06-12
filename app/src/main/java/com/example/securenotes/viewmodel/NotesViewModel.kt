package com.example.securenotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securenotes.data.model.Note
import com.example.securenotes.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {
    
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    init {
        loadNotes()
    }
    
    internal fun loadNotes() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.getAllNotes()
                    .catch { e ->
                        _error.value = e.message
                        _isLoading.value = false
                    }
                    .collectLatest { notes ->
                        _notes.value = notes
                        _isLoading.value = false
                        _error.value = null
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "An unknown error occurred"
                _isLoading.value = false
            }
        }
    }
    
    fun addNote(title: String, content: String) {
        if (title.isBlank() || content.isBlank()) {
            _error.value = "Title and content cannot be empty"
            return
        }
        
        viewModelScope.launch {
            try {
                val newNote = Note(
                    title = title,
                    content = content
                )
                repository.insertNote(newNote)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to add note"
            }
        }
    }
    
    fun deleteNote(note: Note) {
        viewModelScope.launch {
            try {
                repository.deleteNote(note)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete note"
            }
        }
    }
    
    fun deleteAllNotes() {
        viewModelScope.launch {
            try {
                repository.deleteAllNotes()
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete all notes"
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun updateNote(updatedNote: Note) {
        viewModelScope.launch {
            try {
                repository.updateNote(updatedNote)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update note"
            }
        }
    }
}
