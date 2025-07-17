package com.example.securenotes.shared.utils

import android.content.res.Resources
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment

fun <T> Fragment.requireActivityTyped() = requireActivity() as T
fun <K> Fragment.getApplication() = requireActivity().application as K

fun View.slideDown(duration: Long = 200) {
    if (isGone) {
        val animate = TranslateAnimation(0f, 0f, -height.toFloat(), 0f)
        animate.duration = duration
        animate.fillAfter = true
        visibility = View.VISIBLE
        this.clearAnimation()
        startAnimation(animate)
    }
}

fun View.slideUp(duration: Long = 150) {
    if (isVisible) {
        val animate = TranslateAnimation(0f, 0f, 0f, -height.toFloat())
        animate.duration = duration
        animate.fillAfter = true
        this.clearAnimation()
        startAnimation(animate)
        postDelayed({ visibility = View.GONE }, duration)
    }
}

fun View.animateInvisibleVisible(show: Boolean) {
    if (show) {
        visibility = View.VISIBLE
        animate().alpha(1f).setDuration(200).start()
    } else {
        animate().alpha(0f).setDuration(200).withEndAction {
            visibility = View.INVISIBLE
        }.start()
    }
}


fun View.animateGoneVisible(show: Boolean) {
    if (show && !isVisible) {
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .setDuration(200)
            .start()
    } else if (!show && !isGone) {
        animate()
            .alpha(0f)
            .setDuration(200)
            .withEndAction {
                visibility = View.GONE
            }
            .start()
    }
}

fun Int.dpToPx(): Float =
    (this * Resources.getSystem().displayMetrics.density)