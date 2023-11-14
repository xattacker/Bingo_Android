package com.xattacker.android.bingo.view

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.xattacker.android.bingo.R
import java.lang.ref.WeakReference

interface FlippableViewListener
{
    fun onFlipStarted(flipped: FlippableView)
    fun onFlipEnded(flipped: FlippableView)
}

open class FlippableView: FrameLayout
{
    var isFlipped: Boolean = false
        private set

    var listener: FlippableViewListener?
        get() = this.priListener?.get()
        set(value)
        {
            this.priListener = WeakReference<FlippableViewListener>(value)
        }

    private var frontView: View? = null
    private var backView: View? = null

    private val aniRightOut: AnimatorSet
    private val aniLeftIn: AnimatorSet

    private var priListener: WeakReference<FlippableViewListener>? = null

    constructor(context: Context) : super(context)
    {
        aniRightOut = AnimatorInflater.loadAnimator(context, R.animator.flip_out_animation) as AnimatorSet
        aniLeftIn = AnimatorInflater.loadAnimator(context, R.animator.flip_in_animation) as AnimatorSet

        aniLeftIn.addListener(object : Animator.AnimatorListener
        {
            override fun onAnimationStart(p0: Animator)
            {
                priListener?.get()?.onFlipStarted(this@FlippableView)
            }

            override fun onAnimationEnd(p0: Animator)
            {
                priListener?.get()?.onFlipEnded(this@FlippableView)
            }

            override fun onAnimationCancel(p0: Animator)
            {
            }

            override fun onAnimationRepeat(p0: Animator)
            {
            }
        })
    }

    fun setupView(frontView: View, backView: View)
    {
        this.frontView = frontView
        this.backView = backView

        this.addView(backView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        this.addView(frontView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

//        val distance = 8000
//        val scale = resources.displayMetrics.density * distance
//        frontView.setCameraDistance(scale)
//        backView.setCameraDistance(scale)

        ///android.util.Log.d("aaa", "cameraDistance " + frontView.cameraDistance)
    }

    fun flip(animated: Boolean = true)
    {
        if (animated)
        {
            if (!isFlipped)
            {
                // flip to back view
                aniRightOut.setTarget(this.frontView)
                aniLeftIn.setTarget(this.backView)
            }
            else
            {
                // flip back to front view
                aniRightOut.setTarget(this.backView)
                aniLeftIn.setTarget(this.frontView)
            }

            aniRightOut.start()
            aniLeftIn.start()
        }
        else
        {
            if (!isFlipped)
            {
                this.frontView?.alpha = 1f
                this.backView?.alpha = 0f
            }
            else
            {
                this.frontView?.alpha = 1f
                this.backView?.alpha = 1f
            }
        }

        isFlipped = !isFlipped
    }
}