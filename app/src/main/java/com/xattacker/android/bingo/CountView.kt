package com.xattacker.android.bingo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.view.View

internal class CountView(context: Context) : View(context)
{
    private var _count: Int = 0

    init
    {
        _count = 0
    }

    fun setCount(aCount: Int)
    {
        if (aCount >= 0 && aCount <= 5)
        {
            _count = aCount
            invalidate() // repaint
        }
    }

    fun addCount()
    {
        if (_count < 5)
        {
            _count++
            invalidate() // repaint
        }
    }

    override fun onDraw(aCanvas: Canvas)
    {
        super.onDraw(aCanvas)

        val width = width
        val height = height
        val size = 2.5f
        val paint = Paint()
        paint.color = Color.BLUE
        paint.style = Style.STROKE
        paint.strokeWidth = size
        paint.isAntiAlias = true

        if (_count >= 1)
        {
            aCanvas.drawLine(0f, 0f, width.toFloat(), 0f, paint)
        }

        if (_count >= 2)
        {
            aCanvas.drawLine((width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(), paint)
        }

        if (_count >= 3)
        {
            aCanvas.drawLine((width / 2).toFloat(), (height / 3).toFloat(), width.toFloat(), (height / 3).toFloat(), paint)
        }

        if (_count >= 4)
        {
            aCanvas.drawLine((width / 3).toFloat(), (height / 3 * 2).toFloat(), (width / 3).toFloat(), height.toFloat(), paint)
        }

        if (_count >= 5)
        {
            aCanvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), paint)
        }
    }
}
