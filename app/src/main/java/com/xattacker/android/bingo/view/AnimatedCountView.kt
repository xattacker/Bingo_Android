package com.xattacker.android.bingo.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.xattacker.android.bingo.R

class AnimatedCountView: View, CountViewType
{
    override var count: Int = 0
        set(value)
        {
            field = value

            if (value == 0)
            {
                val orig_alpha = this.alpha

                val anim = createValueAnimator(this.alpha, 0f, 200,
                                    {
                                            this.alpha = it
                                    })

                anim.addListener(
                object: AnimatorListenerAdapter()
                {
                    override fun onAnimationEnd(animation: Animator)
                    {
                        for ((i, path) in this@AnimatedCountView.paths.withIndex())
                        {
                            path.reset()
                        }

                        invalidate()
                        this@AnimatedCountView.alpha = orig_alpha
                    }
                })

                anim.start()
            }
            else
            {
                for ((i, path) in this.paths.withIndex())
                {
                    if (i == this.count - 1)
                    {
                        path.animate {
                            invalidate() // repaint
                        }

                        break
                    }
                }
            }
        }

    override var countColor: Int
        get() = paint.color
        set(value)
        {
            paint.color = value
            invalidate() // repaint
        }

    private var paint: Paint
    private var paths: ArrayList<LinePath> = ArrayList()

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs)
    {
        paint = Paint()
        paint.color = Color.BLUE
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND // set the join to round you want
      //  paint.setPathEffect(CornerPathEffect(10f) )
        paint.isAntiAlias = true

        val array = context.obtainStyledAttributes(attrs, R.styleable.CountView)
        this.countColor = array.getColor(R.styleable.CountView_countColor, Color.BLUE)
        array.recycle()


        postDelayed({

            val width = if (width > height) height else width
            paint.strokeWidth = width / 10f

            val offset = paint.strokeWidth
            //android.util.Log.d("aaa", "offset: " + offset)
            var path: LinePath? = null

            for (i in 0 .. this.maxCount - 1)
            {
                when (i)
                {
                    0 ->
                    {
                        path = LinePath(PointF(offset, offset), PointF(width.toFloat() - offset, offset))
                    }

                    1 ->
                    {
                        path = LinePath(PointF((width/2).toFloat(), offset), PointF((width/2).toFloat(), height.toFloat() - offset))
                    }

                    2 ->
                    {
                        path = LinePath(PointF((width/2).toFloat(), (height/2).toFloat()), PointF(width.toFloat() - offset, (height/2).toFloat()))
                    }

                    3 ->
                    {
                        path = LinePath(PointF((width/4).toFloat(), height * 0.5f -( offset/2)), PointF((width/4).toFloat(), height.toFloat() - offset))
                    }

                    4 ->
                    {
                        path = LinePath(PointF(offset/2, height.toFloat() - offset), PointF(width.toFloat() - (offset/2), height.toFloat() - offset))
                    }
                }

                if (path != null)
                {
                    this.paths.add(path)
                }
            }
        },
        200)
    }

    override fun onDraw(aCanvas: Canvas)
    {
        super.onDraw(aCanvas)

        for ((i, path) in this.paths.withIndex())
        {
            if (i < this.count)
            {
                path.draw(aCanvas, this.paint)
            }
        }
    }

    internal class LinePath constructor(private val start: PointF, private val end: PointF)
    {
        private val offset: PointF

        init
        {
            this.offset = PointF(start.x, start.y)
        }

        fun animate(repaint: () -> Unit)
        {
            val duration = 200L
            val set = AnimatorSet()

            val anim1 = createValueAnimator(this.start.x, this.end.x, duration) {
                                    value ->
                                    this.offset.x = value
                                    repaint()
                                }

            val anim2 = createValueAnimator(this.start.y, this.end.y, duration) {
                                    value ->
                                    this.offset.y= value
                                    repaint()
                                }

            set.playTogether(anim1, anim2)
            set.start()
        }

        fun reset()
        {
            this.offset.x = this.start.x
            this.offset.y = this.start.y
        }

        fun draw(aCanvas: Canvas, paint: Paint)
        {
            val path = Path()
            path.moveTo(this.start.x, this.start.y)
            path.lineTo(this.offset.x, this.offset.y)
            path.close()
            aCanvas.drawPath(path, paint)
        }
    }
}


fun Any.createValueAnimator(from: Float, to: Float, duration: Long, monitor: ((value: Float) -> Unit)): ValueAnimator
{
    val anim = ValueAnimator.ofFloat(from, to)

    anim.addUpdateListener {
        aValueAnimator ->
        val value = aValueAnimator.animatedValue as Float
        monitor.invoke(value)
    }

    anim.duration = duration
    anim.interpolator = LinearInterpolator()
    anim.repeatCount = 0

    return anim
}
