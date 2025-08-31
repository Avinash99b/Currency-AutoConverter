package com.avinash.autocurrencyconverter.Components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

class SelectionView(context: Context, val onSelectionComplete: (Rect) -> Unit) : View(context) {

    private var startX = 0f
    private var startY = 0f
    private var endX = 0f
    private var endY = 0f

    private val dimPaint = Paint().apply {
        color = Color.argb(180, 0, 0, 0) // Dark transparent
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                endX = startX
                endY = startY
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                endX = event.x
                endY = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                val rect = Rect(
                    min(startX, endX).toInt(),
                    min(startY, endY).toInt(),
                    max(startX, endX).toInt(),
                    max(startY, endY).toInt()
                )
                onSelectionComplete(rect)
            }
        }
        return true
    }
    // 3. Optional: Draw white border
    val borderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 1. Dim the whole screen
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dimPaint)

        // 2. Clear the selected area (like a window)
        canvas.drawRect(startX, startY, endX, endY, clearPaint)


        canvas.drawRect(startX, startY, endX, endY, borderPaint)
    }

}
