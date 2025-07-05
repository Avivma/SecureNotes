package com.example.securenotes.features.addnote.di

import com.example.securenotes.features.addnote.domain.repository.AddNoteRepository
import com.example.securenotes.shared.data.NoteRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class AddNotesModule {
    @Singleton
    @Binds
    abstract fun bindNoteRepository(noteRepositoryImp: NoteRepositoryImp): AddNoteRepository
}