package com.example.securenotes.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.securenotes.R
import com.example.securenotes.data.model.Note
import java.text.SimpleDateFormat
import java.util.*

class NotesAdapter(
    private val onItemClick: (Note) -> Unit,
    private val onItemLongClick: (Note) -> Unit
) : ListAdapter<Note, NotesAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.noteTitle)
        private val contentTextView: TextView = itemView.findViewById(R.id.noteContent)
        private val dateTextView: TextView = itemView.findViewById(R.id.noteDate)
        
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

        fun bind(note: Note) {
            titleTextView.text = note.title
            contentTextView.text = note.content
            dateTextView.text = dateFormat.format(Date(note.timestamp))
            
            itemView.setOnClickListener { onItemClick(note) }
            itemView.setOnLongClickListener {
                onItemLongClick(note)
                true
            }
        }
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
