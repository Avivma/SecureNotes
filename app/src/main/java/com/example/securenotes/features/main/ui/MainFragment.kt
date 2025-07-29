package com.example.securenotes.features.main.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.securenotes.MainActivity
import com.example.securenotes.R
import com.example.securenotes.core.di.MainDispatcher
import com.example.securenotes.core.utils.L
import com.example.securenotes.databinding.FragmentMainBinding
import com.example.securenotes.features.main.ui.model.UiNote
import com.example.securenotes.features.main.ui.state.MainIntention
import com.example.securenotes.features.main.ui.state.MainState
import com.example.securenotes.shared.removenote.ui.RemoveNoteDisplay
import com.example.securenotes.shared.utils.dpToPx
import com.example.securenotes.shared.utils.requireActivityTyped
import com.google.android.material.snackbar.Snackbar
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    private var mainAdapterManager = MainAdapterManager()
    private val adapter get() = mainAdapterManager.getAdapter()

    private var _moreMenu: PowerMenu? = null
    private val moreMenu get() = _moreMenu!!

    @Inject
    @MainDispatcher
    lateinit var mainDispatcher: CoroutineDispatcher


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
        L.i("$TAG - onViewCreated")
        setAdapter(emptyList())
        setListeners()
    }

    private fun setAdapter(notes: List<UiNote>) {
        mainAdapterManager.set(notes, binding.recyclerView) { intention: MainIntention ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.action(intention)
            }
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }


    private fun setListeners() {
        binding.fab.setOnClickListener {
            viewModel.action(MainIntention.OpenMenu)
        }

        // this work because observeStateLiveData reset the livedata each time. That way we don't collect previous clicks
        viewModel.observeStateLiveData(viewLifecycleOwner, Observer { state ->
            if (state is MainState.Navigation) navigate(state)
            else render(state)
        })
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
            MainState.OpenMenu -> openMenu()
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

    private fun openMenu() {
        val SEARCH = 0
        val ADD_NOTE = 1
        _moreMenu = PowerMenu.Builder(requireContext())
            .addItem(
                PowerMenuItem(
                    title = resources.getString(R.string.search),
                    iconRes = R.drawable.ic_light_search
                )
            )
            .addItem(
                PowerMenuItem(
                    title = resources.getString(R.string.add_note),
                    iconRes = R.drawable.ic_add
                )
            )
            .setAnimation(MenuAnimation.ELASTIC_CENTER)
            .setMenuRadius(8.dpToPx())
            .setMenuShadow(6.dpToPx())
            .setLifecycleOwner(viewLifecycleOwner)
            .setOnMenuItemClickListener { position, item ->
                moreMenu.dismiss()
                when (position) {
                    SEARCH -> viewModel.action(MainIntention.OpenSearch)
                    ADD_NOTE -> viewModel.action(MainIntention.AddNote)
                }
            }
            .build()
        binding.fab.post {
            moreMenu.showAsDropDown(binding.fab, -50, - (300 + binding.fab.height))
        }
    }

    private fun navigate(navigation: MainState.Navigation) {
        L.i("$TAG - navigate - navigation: $navigation")
        lifecycleScope.launch(mainDispatcher) {
            val direction = when (navigation) {
                is MainState.Navigation.NavigateToModifyNote ->
                    if (navigation.note == null)
                        MainFragmentDirections.actionMainFragmentToModifyNoteFragment()
                    else
                        MainFragmentDirections.actionMainFragmentToModifyNoteFragment(navigation.note.id)
                MainState.Navigation.NavigateToSearchDialog ->
                    MainFragmentDirections.actionMainFragmentToSearchDialogFragment()
            }
            requireActivityTyped<MainActivity>().getNavController().navigate(direction)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        L.i("$TAG - onDestroyView")
        resetAdapter()
        _moreMenu = null
        _binding = null
    }

    private fun resetAdapter() {
        mainAdapterManager.reset()
        binding.recyclerView.adapter = null
    }

    companion object {
        private const val TAG = "MainFragment"
    }
}
