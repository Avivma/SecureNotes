package com.example.securenotes.shared.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.securenotes.databinding.SearchNoteLayoutBinding
import com.example.securenotes.shared.search.ui.SearchDialogAdapter.NoteViewHolder
import com.example.securenotes.shared.search.domain.model.SearchDialogNoteModel
import com.example.securenotes.shared.search.ui.state.SearchDialogIntention

class SearchDialogAdapter(initNotes: List<SearchDialogNoteModel> = emptyList()) : RecyclerView.Adapter<NoteViewHolder>() {
    private val notes: MutableList<SearchDialogNoteModel> = ArrayList(initNotes)

    var noteClicked: LiveData<SearchDialogIntention> = MutableLiveData()
    private var _noteClicked: MutableLiveData<SearchDialogIntention> = noteClicked as MutableLiveData<SearchDialogIntention>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = SearchNoteLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    fun setNotes(updatedNotes: List<SearchDialogNoteModel>) {
        this.notes.clear()
        this.notes.addAll(updatedNotes)
        this.notifyDataSetChanged()
    }

    fun clear() {
        setNotes(mutableListOf())
    }

    inner class NoteViewHolder(private val binding: SearchNoteLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: SearchDialogNoteModel) {
            binding.title = note.title
            binding.rowLayout.setOnClickListener {
                _noteClicked.value = SearchDialogIntention.NoteClicked(note)
            }
        }
    }

    companion object {
        private const val TAG = "SearchDialogAdapter"
    }
}