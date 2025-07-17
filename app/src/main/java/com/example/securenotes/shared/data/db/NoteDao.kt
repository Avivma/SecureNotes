package com.example.securenotes.shared.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.securenotes.shared.utils.SPKeys.NOTE_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes")
    fun getNotesStream(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM $NOTE_TABLE_NAME")
    suspend fun getNotes(): List<NoteEntity>

    @Query("SELECT * FROM $NOTE_TABLE_NAME WHERE id = :noteId")
    suspend fun getNote(noteId: Int): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Query(
        """
        UPDATE notes 
        SET title = :title,
            content = :content,
            lastModified = :lastModified
        WHERE id = :id
        """
    )
    suspend fun updateNote(id: Int, title: String, content: String, lastModified: Long): Int

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: Int): Int

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%' ORDER BY lastModified DESC")
    suspend fun searchNotes(query: String): List<NoteEntity>
}