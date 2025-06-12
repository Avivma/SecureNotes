package com.example.securenotes.ui.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.securenotes.R
import com.example.securenotes.data.model.Note
import com.google.android.material.card.MaterialCardView

class NotesAdapter(
    private val onNoteClick: (Note) -> Unit,
    private val onNoteLongClick: (Note) -> Boolean
) : ListAdapter<Note, NotesAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(note)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.noteCard)
        private val titleText: TextView = itemView.findViewById(R.id.noteTitle)
        private val contentText: TextView = itemView.findViewById(R.id.noteContent)
        private val dateText: TextView = itemView.findViewById(R.id.noteDate)

        fun bind(note: Note) {
            titleText.text = note.title
            contentText.text = note.content
            dateText.text = android.text.format.DateFormat.getDateFormat(itemView.context)
                .format(java.util.Date(note.timestamp))

            cardView.setOnClickListener { onNoteClick(note) }
            cardView.setOnLongClickListener { onNoteLongClick(note) }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}
