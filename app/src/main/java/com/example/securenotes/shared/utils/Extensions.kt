package com.example.securenotes.shared.utils

import android.content.res.Resources
import android.view.View
import android.view.animation.TranslateAnimation
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun <T> Fragment.requireActivityTyped() = requireActivity() as T
fun <K> Fragment.getApplication() = requireActivity().application as K

fun Long.formatDate(): String {
    val PATTERN = "d.M.yy"
    return formatTimestampByPattern(this, PATTERN)
}

fun Long.formatDateTime(): String {
//    val PATTERN = "d.M.yy '::' H:mm"
    val PATTERN = "d.M.yy  H:mm"
    return formatTimestampByPattern(this, PATTERN)
}

private fun formatTimestampByPattern(timestamp: Long, pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val dateTime = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
    return formatter.format(dateTime)
}

fun View.slideDown(duration: Long = 200, extraDp: Int = 0) {
    if (isGone) {
        val totalHeight = if (extraDp > 0) height + extraDp.dpToPx() else height // extraDp - Extra space to ensure the view is fully hidden
        val animate = TranslateAnimation(0f, 0f, -totalHeight.toFloat(), 0f)
        animate.duration = duration
        animate.fillAfter = true
        visibility = View.VISIBLE
        this.clearAnimation()
        startAnimation(animate)
    }
}

fun View.slideUp(duration: Long = 150, extraDp: Int = 0) {
    if (isVisible) {
        val totalHeight = if (extraDp > 0) height + extraDp.dpToPx() else height // extraDp - Extra space to ensure the view is fully hidden
        val animate = TranslateAnimation(0f, 0f, 0f, -totalHeight.toFloat())
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