package com.example.securenotes.features.modifynote.di

import com.example.securenotes.features.modifynote.data.ModifyNoteRepositoryImp
import com.example.securenotes.features.modifynote.domain.repository.ModifyNoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class ModifyNoteModule {
    @Singleton
    @Binds
    abstract fun bindNoteRepository(noteRepositoryImp: ModifyNoteRepositoryImp): ModifyNoteRepository
}