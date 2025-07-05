package com.example.securenotes.features.addnote.domain.model

data class AddNoteModel (
    val id: Int,
    val title: String,
    val content: String,
    val updatedAt: Long = System.currentTimeMillis(),
)