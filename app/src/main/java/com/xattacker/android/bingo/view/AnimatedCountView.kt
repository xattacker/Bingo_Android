package com.xattacker.android.bingo.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.xattacker.android.bingo.R

class AnimatedCountView: View, CountViewInterface
{
    override var count: Int = 0
        set(value)
        {
            field = value
            invalidate() // repaint
        }

    override var countColor: Int
        set(value)
        {
            field = value
            invalidate() // repaint
        }

    private var paths: ArrayList<Path> = ArrayList()

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    {
        for (i in 0 .. this.maxCount - 1)
        {
            val path = Path()
            this.paths.add(path)
        }
        

        val array = context.obtainStyledAttributes(attrs, R.styleable.CountView)
        this.countColor = array.getColor(R.styleable.CountView_countColor, Color.BLUE)

        array.recycle()
    }
}