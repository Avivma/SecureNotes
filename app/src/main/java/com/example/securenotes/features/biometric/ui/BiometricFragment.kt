package com.example.securenotes.features.biometric.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.securenotes.MainActivity
import com.example.securenotes.core.ui.DisplayToast
import com.example.securenotes.core.utils.L
import com.example.securenotes.core.utils.requireActivityTyped
import com.example.securenotes.databinding.FragmentBiometricBinding
import com.example.securenotes.features.biometric.ui.state.BiometricIntention
import com.example.securenotes.features.biometric.ui.state.BiometricState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import javax.inject.Inject

@AndroidEntryPoint
class BiometricFragment : Fragment() {
    private var _binding: FragmentBiometricBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BiometricViewModel by viewModels()

    @Inject
    lateinit var displayToast: DisplayToast

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBiometricBinding.inflate(inflater, container, false)
        handleOnBackPressed()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.action(BiometricIntention.AuthenticateUser(getAuthenticationParams()))
            }
        }

        viewModel.authenticationResultLiveData.observe(viewLifecycleOwner) {
            if (it is BiometricState.Navigation) navigate(it)
            render(it)
        }
    }

    private fun render(state: BiometricState) {
        when (state) {
            BiometricState.AskingForBiometric -> TODO("Not yet implemented")
            BiometricState.DisplayFailedMessage -> displayToast("Authentication failed")
            BiometricState.DisplayUnavailableMessage -> displayToast("Authentication unavailable")
            else -> {
                L.e("Unhandled state: $state")
                throw IllegalStateException("Unhandled state: $state")
            }
        }
    }

    private fun navigate(state: BiometricState.Navigation) {
        when (state) {
            BiometricState.Navigation.NavigateToMainScreen -> navigateToMain()
        }
    }

    private fun navigateToMain() {
        val direction = BiometricFragmentDirections.actionBiometricFragmentToMainFragment()
        requireActivityTyped<MainActivity>().getNavController().navigate(direction)
    }

    private fun getAuthenticationParams(): Triple<FragmentActivity, Executor, BiometricPrompt.PromptInfo> {
        val executor = ContextCompat.getMainExecutor(requireContext())
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Fingerprint Authentication")
            .setSubtitle("Touch the sensor to authenticate")
            .setNegativeButtonText("Cancel")
            .build()

        return Triple(requireActivity(), executor, promptInfo)
    }

    private fun handleOnBackPressed() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onStop() {
        super.onStop()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.action(BiometricIntention.CancelAuthentication)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}