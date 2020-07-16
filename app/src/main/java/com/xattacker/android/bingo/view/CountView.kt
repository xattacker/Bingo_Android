package com.xattacker.android.bingo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.util.AttributeSet
import android.view.View
import com.xattacker.android.bingo.R

internal class CountView: View
{
    var count: Int = 0
        set(value)
        {
            field = value
            invalidate() // repaint
        }

    var lineColor: Int
        get() = paint.color
        set(value)
        {
            paint.color = value
            invalidate() // repaint
        }

    private val paint: Paint

    init
    {
        paint = Paint()
        paint.color = Color.BLUE
        paint.style = Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.isAntiAlias = true
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        val array = context.obtainStyledAttributes(attrs, R.styleable.CountView)

        this.lineColor = array.getColor(R.styleable.CountView_lineColor, Color.BLUE)

        array.recycle()
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
            aCanvas.drawLine(offset, offset, width.toFloat() - offset, offset, paint)
        }

        if (count >= 2)
        {
            aCanvas.drawLine((width/2).toFloat(), offset, (width/2).toFloat(), height.toFloat() - offset, paint)
        }

        if (count >= 3)
        {
            aCanvas.drawLine((width/2).toFloat(), (height/2).toFloat(), width.toFloat() - offset, (height/2).toFloat(), paint)
        }

        if (count >= 4)
        {
            aCanvas.drawLine((width/4).toFloat(), height * 0.5f - offset, (width/4).toFloat(), height.toFloat() - offset, paint)
        }

        if (count >= 5)
        {
            aCanvas.drawLine(offset/2, height.toFloat() - offset, width.toFloat() - (offset/2), height.toFloat() - offset, paint)
        }
    }
}
