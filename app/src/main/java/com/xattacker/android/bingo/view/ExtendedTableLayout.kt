package com.xattacker.android.bingo.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TableLayout

open class ExtendedTableLayout : TableLayout
{
    private var enable: Boolean = true

    constructor(aContext: Context) : super(aContext)
    {
    }

    constructor(aContext: Context, aAttrSet: AttributeSet?) : super(aContext, aAttrSet)
    {
    }

    override fun setEnabled(aEnable: Boolean)
    {
        super.setEnabled(aEnable)

        enable = aEnable
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean
    {
        // to disable all subviews touch event
        return !enable
    }
}
