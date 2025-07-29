package com.example.securenotes.shared.ui

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Shader
import android.graphics.drawable.Drawable

class TileDrawable(
    private val drawable: Drawable,
    private val tileMode: Shader.TileMode
) : Drawable() {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = BitmapShader(drawableToBitmap(drawable), tileMode, tileMode)
    }
    private var currentAlpha: Int = 255

    override fun draw(canvas: Canvas) = canvas.drawPaint(paint)

    override fun setAlpha(alpha: Int) {
        currentAlpha = alpha
        paint.alpha = alpha
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getOpacity(): Int {
        // adjust based on whether alpha < 255
        return if (currentAlpha == 255) PixelFormat.OPAQUE else PixelFormat.TRANSLUCENT
    }
}

private fun drawableToBitmap(d: Drawable): Bitmap {
    val bmp = Bitmap.createBitmap(d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    d.setBounds(0, 0, canvas.width, canvas.height)
    d.draw(canvas)
    return bmp
}
