package com.example.securenotes.di

import android.content.Context
import androidx.room.Room
import com.example.securenotes.data.local.NoteDatabase
import com.example.securenotes.data.repository.NoteRepository
import com.example.securenotes.data.repository.NoteRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideNoteDatabase(
        @ApplicationContext context: Context
    ): NoteDatabase = Room.databaseBuilder(
        context,
        NoteDatabase::class.java,
        "notes_db"
    ).build()
    
    @Provides
    @Singleton
    fun provideNoteRepository(
        database: NoteDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): NoteRepository = NoteRepositoryImpl(database.noteDao(), ioDispatcher)
    
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
