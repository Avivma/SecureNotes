package com.example.securenotes.shared.domain.model

data class NoteModel (
    val id: Int = 0,
    val title: String,
    val content: String,
    val updatedAt: Long,
    val creationTime: Long
)