package com.example.securenotes.features.main.domain.repository

import com.example.securenotes.features.main.domain.model.MainNote
import kotlinx.coroutines.flow.Flow

interface MainNoteRepository {
    fun getNotes(): Flow<List<MainNote>>
}