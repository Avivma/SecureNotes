package com.example.securenotes.features.modifynote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.securenotes.MainActivity
import com.example.securenotes.core.utils.L
import com.example.securenotes.databinding.FragmentModifyNoteBinding
import com.example.securenotes.features.modifynote.ui.state.ModifyNoteIntention
import com.example.securenotes.features.modifynote.ui.state.ModifyNoteState
import com.example.securenotes.shared.removenote.ui.RemoveNoteDisplay
import com.example.securenotes.shared.ui.DisplayToast
import com.example.securenotes.shared.utils.requireActivityTyped
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ModifyNoteFragment : Fragment() {
    private var _binding: FragmentModifyNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ModifyNoteViewModel by viewModels()
    private val args: ModifyNoteFragmentArgs by navArgs()
    private lateinit var backPressedCallback: OnBackPressedCallback

    @Inject
    lateinit var displayRemove: RemoveNoteDisplay

    @Inject
    lateinit var displayToast: DisplayToast

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentModifyNoteBinding.inflate(inflater, container, false)
        viewModel.init(args.noteId)
        binding.data = viewModel.data
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.i("$TAG onViewCreated called")

        setListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect { state ->
                        if (state is ModifyNoteState.Navigation) navigate(state)
                        else render(state)
                    }
                }
            }
        }

        viewModel.action(ModifyNoteIntention.FetchData)
    }

    private fun setListeners() {
        binding.btnSave.setOnClickListener {
            viewModel.action(ModifyNoteIntention.SaveNote)
        }

        binding.btnDelete.setOnClickListener {
            viewModel.action(ModifyNoteIntention.RemoveNote(args.noteId, displayDialog = true))
        }

        backPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                L.i("$TAG onBackPressed called - calling viewModel.action(ModifyNoteIntention.BackPressed), enable = false")
                viewModel.action(ModifyNoteIntention.BackPressed)
                isEnabled = false
            }
        }

        requireActivityTyped<MainActivity>().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)
    }

    private fun render(state: ModifyNoteState) {
        when (state) {
            ModifyNoteState.Idle -> Unit
            ModifyNoteState.NoteSaved -> displayToast("Note saved successfully")
            is ModifyNoteState.DisplayRemoveQuestion -> displayRemove.showDeleteDialog(requireContext()) { viewModel.action(
                ModifyNoteIntention.RemoveNote(state.note.id)) }
            is ModifyNoteState.Error -> Snackbar.make(requireView(), "An error while saving has occurred", Snackbar.LENGTH_SHORT).show()
            is ModifyNoteState.NoteRemoved -> displayRemove.showNoteRemovedMessage(state.title)

            else -> {
                L.e("Unhandled state: $state")
                throw IllegalStateException("Unhandled state: $state")
            }
        }
    }

    private fun navigate(navigation: ModifyNoteState.Navigation) {
        L.i("$TAG navigate called")
        if (navigation is ModifyNoteState.Navigation.NavigateBack){
            backPressedCallback.isEnabled = false
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        else
            L.e("Unhandled navigation state: $navigation")
    }

    override fun onStop() {
        L.i("$TAG onStop called")
        viewModel.action(ModifyNoteIntention.MinimizedPressed)
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "ModifyNoteFragment"
    }
}
