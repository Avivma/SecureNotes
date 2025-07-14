package com.example.securenotes.shared.ui

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("setAlpha")
fun setAlpha(view: View, checked: Boolean) {
    view.alpha = if (checked) 1.0f else 0.5f
}