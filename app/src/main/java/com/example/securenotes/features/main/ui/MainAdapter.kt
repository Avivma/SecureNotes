package com.example.securenotes.features.main.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.securenotes.databinding.NoteLayoutBinding
import com.example.securenotes.features.main.ui.model.UiNote
import com.example.securenotes.features.main.ui.state.MainIntention
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainAdapter(updatedNotes: List<UiNote>) : RecyclerView.Adapter<MainAdapter.NoteViewHolder>() {
    private val notes: MutableList<UiNote> = ArrayList(updatedNotes)

    private val _noteClicked = MutableStateFlow<MainIntention?>(null)
    val noteClicked: StateFlow<MainIntention?> = _noteClicked

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
                _noteClicked.value = MainIntention.EditNote(note)
            }
            binding.buttonDelete.setOnClickListener {
                _noteClicked.value = MainIntention.RemoveNote(note, displayDialog = true)
            }
        }
    }

    data class NoteView(val title: String, val content: String)
}
