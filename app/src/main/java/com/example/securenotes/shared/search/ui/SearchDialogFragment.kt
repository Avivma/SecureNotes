package com.example.securenotes.shared.search.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securenotes.MainActivity
import com.example.securenotes.core.di.MainDispatcher
import com.example.securenotes.core.utils.L
import com.example.securenotes.databinding.FragmentSearchDialogBinding
import com.example.securenotes.shared.search.domain.model.SearchDialogNoteModel
import com.example.securenotes.shared.search.ui.state.SearchDialogIntention
import com.example.securenotes.shared.search.ui.state.SearchDialogState
import com.example.securenotes.shared.search.ui.state.SearchDialogState.DisplayMessage
import com.example.securenotes.shared.ui.DisplayToast
import com.example.securenotes.shared.utils.animateInvisibleVisible
import com.example.securenotes.shared.utils.requireActivityTyped
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchDialogFragment : DialogFragment() {
    private var _binding: FragmentSearchDialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchDialogViewModel by viewModels()
    private var _adapter: SearchDialogAdapter? = null
    private val adapter get() = _adapter!!

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher

    @Inject
    lateinit var displayMessage: DisplayToast

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchDialogBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.searchText = viewModel.searchText
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter(emptyList())
        setListeners()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        if (state is SearchDialogState.Navigation) navigate(state)
                        else render(state)
                    }
                }
                // Collect LiveData as Flow
                launch {
                    adapter.noteClicked
                        .asFlow()
                        .collect { intention ->
                            viewModel.action(intention)
                        }
                }

                launch {
                    viewModel.action(SearchDialogIntention.RefreshSearch)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setAdapter(notes: List<SearchDialogNoteModel>) {
        _adapter = SearchDialogAdapter(notes)
        binding.notesRecyclerView.adapter = adapter
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setListeners() {
        binding.clearSearchButton.setOnClickListener {
            viewModel.action(SearchDialogIntention.ClearSearch)
        }

        binding.searchButton.setOnClickListener {
            viewModel.action(SearchDialogIntention.Search)
        }

        // Keyboard "Search" action:
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.action(SearchDialogIntention.Search)
                true
            } else {
                false
            }
        }
    }

    fun render(state: SearchDialogState) {
        when (state) {
            is SearchDialogState.DisplayNotes -> {
                L.i("$TAG - render - DisplayNotes: ${state.notes}")
                adapter.setNotes(state.notes)
            }
            is SearchDialogState.ToggleSearchButtons -> {
                enableSearchButton(state.isEnabled)
            }
            DisplayMessage.SearchEmpty -> {
                L.i("$TAG - render - SearchEmpty")
                displayMessage("Please enter a search text")
            }
            DisplayMessage.NoResults -> {
                L.i("$TAG - render - NoResults")
                displayMessage("No results found")
            }
            else -> {
                L.e("$TAG - Unhandled state: $state")
                throw IllegalStateException("Unhandled state: $state")
            }
        }

    }

    private fun enableSearchButton(isEnabled: Boolean) {
        binding.searchButton.animateInvisibleVisible(isEnabled)
        binding.clearSearchButton.animateInvisibleVisible(isEnabled)
    }

    private fun navigate(navigation: SearchDialogState.Navigation) {
        L.i("$TAG - navigate - navigation: $navigation")
        if (navigation is SearchDialogState.Navigation.NavigateToModifyNote) {
            val direction = SearchDialogFragmentDirections.actionSearchDialogFragmentToModifyNoteFragment(noteId = navigation.noteId, searchText = navigation.searchText)
            lifecycleScope.launch(mainDispatcher) {
                this@SearchDialogFragment.dismiss()
                requireActivityTyped<MainActivity>().getNavController().navigate(direction)
            }
        } else
            L.e("Unhandled navigation state: $navigation")
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        _adapter = null
        binding.notesRecyclerView.adapter = null
    }

    companion object {
        private const val TAG = "SearchDialogFragment"
    }
}