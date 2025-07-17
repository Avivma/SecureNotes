package com.example.securenotes.shared.search.di

import com.example.securenotes.shared.search.data.SearchNotesRepositoryImp
import com.example.securenotes.shared.search.domain.repository.SearchNotesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
abstract class SearchNotesModule {
    @Singleton
    @Binds
    abstract fun bindRemoveNoteRepository(repository: SearchNotesRepositoryImp): SearchNotesRepository
}