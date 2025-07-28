package com.example.securenotes.features.modifynote.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.securenotes.MainActivity
import com.example.securenotes.R
import com.example.securenotes.core.utils.L
import com.example.securenotes.databinding.FragmentModifyNoteBinding
import com.example.securenotes.features.modifynote.ui.state.ModifyNoteIntention
import com.example.securenotes.features.modifynote.ui.state.ModifyNoteState
import com.example.securenotes.features.modifynote.ui.utils.MenuHelper
import com.example.securenotes.features.modifynote.ui.utils.ModifyNoteViewsManager.ViewFocus
import com.example.securenotes.shared.removenote.ui.RemoveNoteDisplay
import com.example.securenotes.shared.search.SearchHelper
import com.example.securenotes.shared.ui.DisplayToast
import com.example.securenotes.shared.utils.animateGoneVisible
import com.example.securenotes.shared.utils.animateInvisibleVisible
import com.example.securenotes.shared.utils.requireActivityTyped
import com.example.securenotes.shared.utils.setOnMotionEventListener
import com.example.securenotes.shared.utils.slideDown
import com.example.securenotes.shared.utils.slideUp
import com.example.securenotes.shared.utils.textChangedListener
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
    private lateinit var searchHelper: SearchHelper

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

        makeBottomButtonsBarAdjustableAboveKeyboard()

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

        viewModel.action(ModifyNoteIntention.FetchData(args.searchText))
    }

    private fun makeBottomButtonsBarAdjustableAboveKeyboard() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.timeContainer) { view, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom

            view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = if (imeVisible) imeHeight - 160 else 0
            }

            insets
        }
    }

    private fun setListeners() {
        binding.menuButton.setOnClickListener { view ->
            viewModel.action(ModifyNoteIntention.OpenMenu)
        }

        binding.btnSave.setOnClickListener {
            viewModel.action(ModifyNoteIntention.SaveNote)
        }

        binding.textTitle.textChangedListener {
            viewModel.action(ModifyNoteIntention.TitleChanged(it))
        }

        binding.textContent.textChangedListener {
            viewModel.action(ModifyNoteIntention.ContentChanged(it))
        }

        binding.textTitle.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                viewModel.action(ModifyNoteIntention.GotFocus(ViewFocus.TitleFocused))
            }
        }

        binding.textContent.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                viewModel.action(ModifyNoteIntention.GotFocus(ViewFocus.ContentFocused))
            }
        }

        binding.btnUndo.setOnMotionEventListener(
            motionDown = { viewModel.action(ModifyNoteIntention.UndoContinuously) },
            motionUp = { viewModel.action(ModifyNoteIntention.UndoStop) }
        )

        binding.btnRedo.setOnMotionEventListener(
            motionDown = { viewModel.action(ModifyNoteIntention.RedoContinuously) },
            motionUp = { viewModel.action(ModifyNoteIntention.RedoStop) }
        )

        setSearchListener()
        setBackPressedListener()
    }

    private fun setSearchListener() {
        searchHelper = SearchHelper(requireContext(), binding.textTitle, binding.textContent) {
            matchText ->
            binding.searchMatchCounter.text = matchText
            // Animate the navigation bar to appear when there are matches
            val SHOW = true
            if (matchText.isEmpty()) {
                searchHelper.clearSearch()
                displayToast("No matches found")
                binding.searchNavigationLayout.animateGoneVisible(!SHOW)
            } else {
                binding.searchNavigationLayout.animateGoneVisible(SHOW)
            }
        }

        binding.cancelSearchButton.setOnClickListener {
            if (binding.searchContainer.isVisible) {
                binding.searchContainer.slideUp()
            }
        }

        binding.searchButton.setOnClickListener {
            searchHelper.performSearch(binding.searchEditText.text.toString())
        }

        binding.clearSearchButton.setOnClickListener {
            binding.searchEditText.text.clear()
            searchHelper.clearSearch()
        }

        binding.searchNextButton.setOnClickListener {
            searchHelper.goToNextMatch()
        }

        binding.searchPrevButton.setOnClickListener {
            searchHelper.goToPreviousMatch()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()
                binding.searchHasText = hasText

                binding.clearSearchButton.animateInvisibleVisible(hasText)

                if (!hasText) {
                    searchHelper.clearSearch()
                    binding.searchNavigationLayout.animateGoneVisible(false)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

// Keyboard "Search" action:
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchHelper.performSearch(binding.searchEditText.text.toString())
                true
            } else {
                false
            }
        }

    }

    private fun setBackPressedListener() {
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
            ModifyNoteState.NoteSaved -> displayToast("Note saved successfully")
            is ModifyNoteState.DisplayRemoveQuestion -> displayRemove.showDeleteDialog(requireContext()) { viewModel.action(
                ModifyNoteIntention.RemoveNote(state.note.id)) }
            is ModifyNoteState.Error -> Snackbar.make(requireView(), "An error while saving has occurred", Snackbar.LENGTH_SHORT).show()
            is ModifyNoteState.NoteRemoved -> displayRemove.showNoteRemovedMessage(state.title)
            ModifyNoteState.DisplayMenu -> createMenu()
            ModifyNoteState.DisplaySearchBar -> revealSearchBar()
            is ModifyNoteState.DisplaySearchBarWithQuery -> revealSearchBarWithQuery(state.searchText)

            else -> {
                L.e("Unhandled state: $state")
                throw IllegalStateException("Unhandled state: $state")
            }
        }
    }

    private fun createMenu() {
        val popup = PopupMenu(requireContext(), binding.menuButton)
        popup.menuInflater.inflate(R.menu.modify_note_menu, popup.menu)
        // Force icons to show
        MenuHelper.makeMenuShowIcons(popup)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_search -> {
                    viewModel.action(ModifyNoteIntention.RevealSearch)
                    true
                }
                R.id.action_delete -> {
                    viewModel.action(ModifyNoteIntention.RemoveNote(args.noteId, displayDialog = true))
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun revealSearchBar() {
        if (binding.searchContainer.isGone) {
            binding.searchContainer.slideDown()
        }
    }

    private fun revealSearchBarWithQuery(searchText: String) {
        binding.searchEditText.setText(searchText)
        binding.searchContainer.slideDown()
        searchHelper.performSearch(binding.searchEditText.text.toString())
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
