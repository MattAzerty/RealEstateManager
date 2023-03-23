package fr.melanoxy.realestatemanager.ui.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

    fun intToBitmap(size: Int): Bitmap {

        val sizeInText = if (size < 10) {"[0$size]"} else "[$size]"
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = 44f
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.LEFT
        val baseline = -paint.ascent() // ascent() is negative

        val width = (paint.measureText(sizeInText) + 5.0f).toInt() // round + offset

        val height = (baseline + paint.descent() + 0.0f).toInt()
        val image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(image)
        canvas.drawText(sizeInText, 5.0F, baseline, paint)

        return image

    }