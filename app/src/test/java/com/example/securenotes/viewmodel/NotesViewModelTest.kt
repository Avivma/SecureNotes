package com.example.securenotes.viewmodel

import app.cash.turbine.test
import com.example.securenotes.data.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: NotesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NotesViewModel(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addNote adds a new note`() = runTest {
        // Given
        val title = "Test Title"
        val content = "Test Content"
        
        // When
        viewModel.addNote(title, content)
        
        // Then
        viewModel.notes.test {
            val notes = awaitItem()
            assertEquals(4, notes.size) // 3 from init + 1 new one
            assertEquals(title, notes.last().title)
            assertEquals(content, notes.last().content)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteNote removes the correct note`() = runTest {
        // Given
        val testNotes = listOf(
            Note("1", "Note 1", "Content 1", 1000),
            Note("2", "Note 2", "Content 2", 2000)
        )
        viewModel.setNotes(testNotes)
        
        // When
        viewModel.deleteNote(testNotes[0])
        
        // Then
        viewModel.notes.test {
            val notes = awaitItem()
            assertEquals(1, notes.size)
            assertEquals("Note 2", notes[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }
    
    @Test
    fun `loadNotes sets initial notes and loading state`() = runTest {
        // Given - Initial state
        
        // When - loadNotes is called in init
        
        // Then
        viewModel.isLoading.test {
            // Initial loading state
            assertEquals(true, awaitItem())
            
            // After loading completes
            assertEquals(false, awaitItem())
            
            // Check notes are loaded
            viewModel.notes.test {
                val notes = awaitItem()
                assertEquals(3, notes.size) // 3 sample notes from init
                cancelAndIgnoreRemainingEvents()
            }
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
