package com.example.securenotes.shared.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.securenotes.core.utils.SPKeys

@Entity(tableName = SPKeys.NOTE_TABLE_NAME)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val lastModified: Long = System.currentTimeMillis(),

    // SHOULD NOT BE MODIFIED
    val createdAt: Long = System.currentTimeMillis()
)
