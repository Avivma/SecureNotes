package com.example.securenotes.features.addnote.ui.state

sealed class AddNoteIntention {
    object SaveNote : AddNoteIntention()
    object BackPressed : AddNoteIntention()
}