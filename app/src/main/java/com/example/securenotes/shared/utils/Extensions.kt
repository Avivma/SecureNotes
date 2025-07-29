package com.example.securenotes.shared.utils

import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.EditText
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
    val PATTERN = "d.M.yy, H:mm"
    return formatTimestampByPattern(this, PATTERN)
}

private fun formatTimestampByPattern(timestamp: Long, pattern: String): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val dateTime = Instant.ofEpochMilli(timestamp)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
    return formatter.format(dateTime)
}

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


fun View.setOnMotionEventListener(
    motionDown: () -> Unit,
    motionUp: () -> Unit
) {
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                motionDown()
                true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                motionUp()
                true
            }

            else -> false
        }
    }

}

fun EditText.textChangedListener(
    afterTextChanged: (String) -> Unit
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged(s?.toString() ?: "")
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}