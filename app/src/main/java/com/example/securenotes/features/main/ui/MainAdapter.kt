package com.example.securenotes.features.main.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.securenotes.core.utils.L
import com.example.securenotes.databinding.NoteLayoutBinding
import com.example.securenotes.features.main.ui.model.UiNote
import com.example.securenotes.features.main.ui.state.MainIntention

class MainAdapter(updatedNotes: List<UiNote>, val noteClicked: (MainIntention) -> Unit) : RecyclerView.Adapter<MainAdapter.NoteViewHolder>() {
    private val notes: MutableList<UiNote> = ArrayList(updatedNotes)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = NoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    fun setNotes(updatedNotes: List<UiNote>) {
        this.notes.clear()
        this.notes.addAll(updatedNotes)
        this.notifyDataSetChanged()
    }

    fun clear() {
        setNotes(mutableListOf())
    }

    inner class NoteViewHolder(private val binding: NoteLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: UiNote) {
            val noteView = NoteView(note.title, note.content)
            binding.noteView = noteView
            binding.rowLayout.setOnClickListener {
                noteClicked(MainIntention.EditNote(note))
            }
            binding.buttonDelete.setOnClickListener {
                L.i("$TAG - buttonDelete clicked for note: $note")
                noteClicked(MainIntention.RemoveNote(note, displayDialog = true))
            }
        }
    }

    data class NoteView(val title: String, val content: String)

    companion object {
        private const val TAG = "MainAdapter"
    }
}
