package com.example.securenotes.shared.removenote.ui

import android.content.Context
import com.example.securenotes.shared.ui.DisplayToast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoveNoteDisplay @Inject constructor(
    private val context: Context,
    private val displayToast: DisplayToast
) {
    fun showNoteRemovedMessage(noteTitle: String) {
        displayToast("Note \"$noteTitle\" removed successfully")
    }

    fun showDeleteDialog(positiveCallback: () -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ -> positiveCallback() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}