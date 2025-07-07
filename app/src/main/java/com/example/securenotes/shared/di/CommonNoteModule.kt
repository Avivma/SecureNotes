package com.example.securenotes.shared.di

import com.example.securenotes.shared.data.NoteRepositoryImp
import com.example.securenotes.shared.domain.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
abstract class CommonNoteModule {
    @Singleton
    @Binds
    abstract fun bindCommonNoteModule(repository: NoteRepositoryImp): NoteRepository
}