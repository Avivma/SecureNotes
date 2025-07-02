package com.example.securenotes.core.utils

import androidx.fragment.app.Fragment

fun <T> Fragment.requireActivityTyped() = requireActivity() as T
fun <K> Fragment.getApplication() = requireActivity().application as K