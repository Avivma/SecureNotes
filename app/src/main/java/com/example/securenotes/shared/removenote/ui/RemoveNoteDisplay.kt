package com.example.securenotes.shared.removenote.ui

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.securenotes.shared.ui.DisplayToast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveNoteDisplay @Inject constructor(
    private val displayToast: DisplayToast
) {
    fun showNoteRemovedMessage(noteTitle: String) {
        val title: String = if (noteTitle.isEmpty()) "" else "\"$noteTitle\""
        displayToast("Note $title removed successfully")
    }

    fun showDeleteDialog(context: Context, positiveCallback: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ -> positiveCallback() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}