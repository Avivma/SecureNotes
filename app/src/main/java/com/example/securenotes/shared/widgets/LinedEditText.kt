package com.example.securenotes.shared.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatEditText
import kotlin.math.roundToInt

class LinedEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#445566")
        strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
    }
    private val exactLineHeightPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, 24f, resources.displayMetrics
    ).roundToInt()

    init {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        includeFontPadding = false
        setLineSpacing(0f, 1f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setLineHeight(exactLineHeightPx)
        }
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas) {
        val count = height / exactLineHeightPx
        var y = paddingTop + exactLineHeightPx.toFloat()
        val startX = paddingLeft.toFloat()
        val stopX = (width - paddingRight).toFloat()
        for (i in 0 until count) {
            canvas.drawLine(startX, y, stopX, y, linePaint)
            y += (exactLineHeightPx + 9.5f) // 9.5f is the extra spacing added to each line (achieved by try-and-error)
        }
        super.onDraw(canvas)
    }
}

/*
class LinedEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#55445566") // soft dark-mode ruled line
        strokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics
        )
    }

    init {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        setLineSpacing(0f, 1f)
        setBackgroundColor(Color.TRANSPARENT)
        isVerticalScrollBarEnabled = false
    }

    override fun onDraw(canvas: Canvas) {
        // Calculate line height based on 16sp text size
        val lh = lineHeight
        // number of visible rows
        val count = height / lh

        val startX = paddingLeft.toFloat()
        val stopX = (width - paddingRight).toFloat()

        var y = paddingTop + lh.toFloat()
        for (i in 0 until count) {
            canvas.drawLine(startX, y, stopX, y, linePaint)
            y += lh
        }

        super.onDraw(canvas) // draw text over lines
    }
}
*/
