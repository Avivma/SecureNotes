package com.example.securenotes.shared.removenote.di

import com.example.securenotes.shared.data.NoteRepositoryImp
import com.example.securenotes.shared.removenote.domain.repository.RemoveNoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class RemoveNoteModule {
    @Singleton
    @Binds
    abstract fun bindRemoveNoteRepository(repository: NoteRepositoryImp): RemoveNoteRepository
}