package com.example.securenotes.ui.notes

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.securenotes.R
import com.example.securenotes.adapter.NotesAdapter
import com.example.securenotes.data.model.Note
import com.example.securenotes.databinding.FragmentNotesBinding
import com.example.securenotes.viewmodel.NotesViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.Callback.DISMISS_EVENT_ACTION
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: NotesViewModel by viewModels()
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentNotesBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        setupObservers()
        setupSwipeRefresh()
        setupSwipeToDelete()
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(
            onNoteClick = { note ->
                showNoteDetails(note)
            },
            onNoteLongClick = { note ->
                showDeleteConfirmation(note)
                true
            }
        )
        
        binding.notesRecyclerView.apply {
            adapter = notesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            // Add item decoration if needed
            // addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

    private fun setupClickListeners() {
        binding.addNoteFab.setOnClickListener {
            // Navigate to add/edit note screen
            showAddNoteDialog()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.notes.collectLatest { notes ->
                notesAdapter.submitList(notes)
                binding.progressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
                
                // Show empty state if no notes
                if (notes.isEmpty()) {
                    // You can show an empty state view here
                    binding.emptyStateText.visibility = View.VISIBLE
                } else {
                    binding.emptyStateText.visibility = View.GONE
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading && notesAdapter.itemCount == 0) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
                
                // Disable swipe refresh when loading from initial load
                if (isLoading && notesAdapter.itemCount == 0) {
                    binding.swipeRefreshLayout.isEnabled = false
                } else {
                    binding.swipeRefreshLayout.isEnabled = true
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest { error ->
                error?.let {
                    showErrorSnackbar(it)
                    viewModel.clearError()
                }
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadNotes()
        }
    }

    private fun setupSwipeToDelete() {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            private var recentlyDeletedNote: Note? = null
            
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = notesAdapter.currentList[position]
                
                // Store the deleted note for possible undo
                recentlyDeletedNote = note
                
                // Delete the note from the database
                viewModel.deleteNote(note)
                
                // Show undo snackbar
                showUndoSnackbar()
            }
            
            private fun showUndoSnackbar() {
                val note = recentlyDeletedNote ?: return
                
                Snackbar.make(
                    binding.root,
                    getString(R.string.delete_note_message, note.title),
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.undo) {
                    // Undo delete - add the note back
                    viewModel.addNote(note.title, note.content)
                }.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (event != DISMISS_EVENT_ACTION) {
                            // User didn't click undo, clear the reference
                            recentlyDeletedNote = null
                        }
                    }
                }).show()
            }
        }


        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.notesRecyclerView)
    }

    private fun showAddNoteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val titleInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.titleInput)
        val contentInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.contentInput)
        
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_new_note)
            .setView(dialogView)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            
        dialog.setOnShowListener {
            val saveButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val title = titleInput.text.toString().trim()
                val content = contentInput.text.toString().trim()
                
                if (title.isBlank()) {
                    titleInput.error = getString(R.string.title_required)
                    return@setOnClickListener
                }
                
                if (content.isBlank()) {
                    contentInput.error = getString(R.string.content_required)
                    return@setOnClickListener
                }
                
                viewModel.addNote(title, content)
                dialog.dismiss()
            }
        }
        
        dialog.show()
    }

    private fun showNoteDetails(note: Note) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_view_note, null)
        val titleText = dialogView.findViewById<TextView>(R.id.titleText)
        val contentText = dialogView.findViewById<TextView>(R.id.contentText)
        val dateText = dialogView.findViewById<TextView>(R.id.dateText)
        
        titleText.text = note.title
        contentText.text = note.content
        dateText.text = android.text.format.DateFormat.getDateFormat(requireContext())
            .format(java.util.Date(note.timestamp))
        
        MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(R.string.edit) { _, _ ->
                showEditNoteDialog(note)
            }
            .show()
    }
    
    private fun showEditNoteDialog(note: Note) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val titleInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.titleInput)
        val contentInput = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.contentInput)
        
        titleInput.setText(note.title)
        contentInput.setText(note.content)
        
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.edit_note)
            .setView(dialogView)
            .setPositiveButton(R.string.save, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
            
        dialog.setOnShowListener {
            val saveButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val title = titleInput.text.toString().trim()
                val content = contentInput.text.toString().trim()
                
                if (title.isBlank()) {
                    titleInput.error = getString(R.string.title_required)
                    return@setOnClickListener
                }
                
                if (content.isBlank()) {
                    contentInput.error = getString(R.string.content_required)
                    return@setOnClickListener
                }
                
                val updatedNote = note.copy(title = title, content = content)
                viewModel.updateNote(updatedNote)
                dialog.dismiss()
            }
        }
        
        dialog.show()
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).setAction("Dismiss") {
            // Dismiss action
        }.show()
    }
    
    private fun showDeleteConfirmation(note: Note) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete '${note.title}'?")
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteNote(note)
                Snackbar.make(
                    binding.root,
                    getString(R.string.note_deleted, note.title),
                    Snackbar.LENGTH_LONG
                ).show()
            }
            .setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.dismiss()
                // Refresh the list to reset any swiped items
                notesAdapter.notifyDataSetChanged()
            }
            .show()
    }

    override fun onDestroyView() {
        // Clear references to views to prevent memory leaks
        binding.notesRecyclerView.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = NotesFragment()
    }
}
