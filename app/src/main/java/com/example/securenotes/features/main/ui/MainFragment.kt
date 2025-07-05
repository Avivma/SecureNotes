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
import com.example.securenotes.databinding.FragmentMainBinding
import com.example.securenotes.features.main.ui.state.MainState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    render(state)
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
            is MainState.Waiting -> handleWaitingState()
            is MainState.DisplayNotes -> handleDisplayNotesState(state)
            is MainState.Error -> handleErrorState()
        }
    }

    private fun handleWaitingState() {
        binding.displayNotes = false
    }

    private fun handleDisplayNotesState(state: MainState.DisplayNotes) {
        binding.displayNotes = true
        binding.recyclerView.adapter = MainAdapter(state.notes)
    }

    private fun handleErrorState() {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
