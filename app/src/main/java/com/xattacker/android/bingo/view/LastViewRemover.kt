package com.xattacker.android.bingo.view

import android.graphics.Color
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener

class LastViewRemover(private val _lastView: View?) : AnimationListener
{
    private var _count: Int = 0

    init
    {
        _count = 0
    }

    override fun onAnimationStart(arg0: Animation)
    {
        _lastView?.setBackgroundColor(Color.RED)
    }

    override fun onAnimationEnd(arg0: Animation)
    {
        _lastView?.setBackgroundColor(Color.TRANSPARENT)


        _count++

        if (_count < 2)
        {
            // repeat 2 times
            val handler = Handler()
            handler.postDelayed({_lastView!!.startAnimation(arg0)}, 300)
        }
    }

    override fun onAnimationRepeat(arg0: Animation)
    {
    }
}
