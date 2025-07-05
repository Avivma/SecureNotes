package com.example.securenotes.features.addnote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.securenotes.core.utils.L
import com.example.securenotes.databinding.FragmentAddNoteBinding
import com.example.securenotes.features.addnote.ui.state.AddNoteIntention
import com.example.securenotes.features.addnote.ui.state.AddNoteState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddNoteFragment : Fragment() {
    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddNoteViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        binding.data = viewModel.data
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewModel.initWithDefaults()

        setListeners()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    if (state is AddNoteState.Navigation) navigate(state)
                    else render(state)
                }
            }
        }
    }

    private fun setListeners() {
        binding.btnSave.setOnClickListener {
            viewModel.action(AddNoteIntention.SaveNote)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.action(AddNoteIntention.BackPressed)
            isEnabled = false
        }
    }

    private fun render(state: AddNoteState) {
        when (state) {
            AddNoteState.Idle -> Unit
            AddNoteState.NoteSaved -> displaySavedMessage()
            is AddNoteState.Error -> {
                Snackbar.make(requireView(), "An error while saving has occurred", Snackbar.LENGTH_SHORT).show()
            }

            else -> {
                L.e("Unhandled state: $state")
            }
        }
    }

    private fun navigate(navigation: AddNoteState.Navigation) {
        if (navigation is AddNoteState.Navigation.NavigateBack)
//            findNavController().navigateUp()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        else
            L.e("Unhandled navigation state: $navigation")
    }

    private fun displaySavedMessage() {
        Toast.makeText(requireContext(), "Note saved successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
