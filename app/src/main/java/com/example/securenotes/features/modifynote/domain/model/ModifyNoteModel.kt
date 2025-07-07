package com.example.securenotes.features.modifynote.domain.model

data class ModifyNoteModel (
    val id: Int,
    val title: String,
    val content: String,
    val updatedAt: Long = System.currentTimeMillis(),
)