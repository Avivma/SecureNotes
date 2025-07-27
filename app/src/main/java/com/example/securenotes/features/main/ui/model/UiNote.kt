package com.example.securenotes.features.main.ui.model

data class UiNote(
    val id: Int,
    val title: String,
    val content: String,
    val lastModified: Long,
    val lastModifiedText: String,
    val lastModifiedTextShort: String
)