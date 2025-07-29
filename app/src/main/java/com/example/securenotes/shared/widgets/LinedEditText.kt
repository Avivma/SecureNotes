package com.example.securenotes.shared.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.graphics.toColorInt
import kotlin.math.roundToInt

class LinedEditText(context: Context, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = "#445566".toColorInt()
        strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
    }
    private val exactLineHeightPx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, 24f, resources.displayMetrics
    ).roundToInt()

    init {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        includeFontPadding = false
        setLineSpacing(0f, 1f)
        lineHeight = exactLineHeightPx
        setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas) {
        val count = height / exactLineHeightPx
        var y = paddingTop + exactLineHeightPx.toFloat()
        val startX = paddingLeft.toFloat()
        val stopX = (width - paddingRight).toFloat()
        for (i in 0 until count) {
            canvas.drawLine(startX, y, stopX, y, linePaint)
            y += (exactLineHeightPx + 9.5f) // 9.5f is an extra spacing added to each line (achieved by try-and-error)
        }
        super.onDraw(canvas)
    }
}