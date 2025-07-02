package com.example.securenotes.features.main.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.securenotes.databinding.NoteLayoutBinding
import com.example.securenotes.features.main.ui.model.UiNote

class MainAdapter(private val notes: List<UiNote>) : RecyclerView.Adapter<MainAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    inner class NoteViewHolder(private val binding: NoteLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: UiNote) {
            val noteView = NoteView(note.title, note.content)
            binding.noteView = noteView
        }
    }

    data class NoteView(val title: String, val content: String)
}
