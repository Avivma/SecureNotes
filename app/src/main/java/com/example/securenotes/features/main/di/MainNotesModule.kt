package com.example.securenotes.features.main.di

import com.example.securenotes.features.main.data.MainNotesRepositoryImp
import com.example.securenotes.features.main.domain.repository.MainNoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class MainNotesModule {
    @Singleton
    @Binds
    abstract fun bindNoteRepository(noteRepositoryImp: MainNotesRepositoryImp): MainNoteRepository
}