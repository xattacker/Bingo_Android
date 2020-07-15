package com.xattacker.android.bingo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.util.AttributeSet
import android.view.View

internal class CountView(context: Context, attrs: AttributeSet) : View(context, attrs)
{
    var count: Int = 0
        set(value)
        {
            field = value
            invalidate() // repaint
        }

    var color: Int
        get() = paint.color
        set(value)
        {
            paint.color = value
        }

    private val paint: Paint

    init
    {
        paint = Paint()
        paint.color = Color.BLUE
        paint.style = Style.STROKE
        paint.isAntiAlias = true
    }

    fun addCount()
    {
        if (count < 5)
        {
            count++
            invalidate() // repaint
        }
    }

    fun reset()
    {
        this.count = 0
    }

    override fun onDraw(aCanvas: Canvas)
    {
        super.onDraw(aCanvas)

        val width = if (width > height) height else width
        paint.strokeWidth = width / 10f

        val offset = paint.strokeWidth

        if (count >= 1)
        {
            aCanvas.drawLine(offset/2, offset, width.toFloat() - (offset/2), offset, paint)
        }

        if (count >= 2)
        {
            aCanvas.drawLine((width / 2).toFloat(), offset, (width / 2).toFloat(), height.toFloat() - offset, paint)
        }

        if (count >= 3)
        {
            aCanvas.drawLine((width / 2).toFloat(), (height/2).toFloat(), width.toFloat() - (offset/2), (height/2).toFloat(), paint)
        }

        if (count >= 4)
        {
            aCanvas.drawLine((width / 4).toFloat(), height * 0.5f - offset, (width / 4).toFloat(), height.toFloat() - offset, paint)
        }

        if (count >= 5)
        {
            aCanvas.drawLine(0f, height.toFloat() - offset, width.toFloat(), height.toFloat() - offset, paint)
        }
    }
}
