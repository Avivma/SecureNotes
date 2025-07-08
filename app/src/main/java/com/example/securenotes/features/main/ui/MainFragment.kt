package com.example.securenotes.features.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securenotes.MainActivity
import com.example.securenotes.core.utils.L
import com.example.securenotes.databinding.FragmentMainBinding
import com.example.securenotes.features.main.ui.model.UiNote
import com.example.securenotes.features.main.ui.state.MainIntention
import com.example.securenotes.features.main.ui.state.MainState
import com.example.securenotes.shared.removenote.ui.RemoveNoteDisplay
import com.example.securenotes.shared.utils.requireActivityTyped
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: MainAdapter

    @Inject
    lateinit var displayRemove: RemoveNoteDisplay

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setAdapter(emptyList())
        setListeners()
    }

    private fun setAdapter(notes: List<UiNote>) {
        adapter = MainAdapter(notes)
        adapter.setHasStableIds(true)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setListeners() {
        binding.fab.setOnClickListener {
            viewModel.action(MainIntention.AddNote)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collectLatest { state ->
                        if (state is MainState.Navigation) navigate(state)
                        else render(state)
                    }
                }
                launch {
                    adapter.noteClicked.collectLatest { intention ->
                        if (intention != null)
                            viewModel.action(intention)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startObservingDb()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopObservingDb()
    }

    private fun render(state: MainState) {
        when (state) {
            MainState.Waiting -> handleWaitingState()
            is MainState.DisplayNotes -> handleDisplayNotesState(state)
            is MainState.DisplayRemoveQuestion -> showDeleteDialog(state.note)
            is MainState.NoteRemoved -> handleRemovedNoteState(state.title)
            is MainState.Error -> handleErrorState(state.message)
            else -> {
                L.e("Unhandled state: $state")
                throw IllegalStateException("Unhandled state: $state")
            }
        }
    }

    private fun handleWaitingState() {
        binding.displayNotes = false
    }

    private fun handleDisplayNotesState(state: MainState.DisplayNotes) {
        L.i("$TAG - handleDisplayNotesState - notes: ${state.notes}")
        binding.displayNotes = true
        adapter.setNotes(state.notes)
    }

    private fun showDeleteDialog(note: UiNote) {
        displayRemove.showDeleteDialog(requireContext()) {
            viewModel.action(MainIntention.RemoveNote(note))
        }
    }


    private fun handleRemovedNoteState(noteTitle: String) {
        displayRemove.showNoteRemovedMessage(noteTitle)
    }

    private fun handleErrorState(message: String) {
        Snackbar.make(requireView(), "An error occurred: ${message}", Snackbar.LENGTH_SHORT).show()
    }

    private fun navigate(navigation: MainState.Navigation) {
        if (navigation is MainState.Navigation.NavigateToModifyNote) {
            val direction =
                if (navigation.note == null) MainFragmentDirections.actionMainFragmentToModifyNoteFragment()
                else MainFragmentDirections.actionMainFragmentToModifyNoteFragment(navigation.note.id)
            requireActivityTyped<MainActivity>().getNavController().navigate(direction)
        } else
            L.e("Unhandled navigation state: $navigation")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "MainFragment"
    }
}
