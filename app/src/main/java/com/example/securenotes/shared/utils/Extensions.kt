package com.example.securenotes.shared.utils

import androidx.fragment.app.Fragment

fun <T> Fragment.requireActivityTyped() = requireActivity() as T
fun <K> Fragment.getApplication() = requireActivity().application as K