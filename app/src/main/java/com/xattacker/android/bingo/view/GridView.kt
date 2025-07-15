package com.xattacker.android.bingo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity

import android.widget.TextView
import com.xattacker.android.bingo.BingoGridView
import com.xattacker.android.bingo.CustomProperties
import com.xattacker.android.bingo.FontType
import com.xattacker.android.bingo.logic.PlayerType
import com.xattacker.android.bingo.logic.ConnectedDirection

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class GridView : TextView, BingoGridView
{
    enum class BorderAngleType
    {
        ANGLE_ROUND,
        ANGLE_RIGHT
    }

    var borderColor = Color.BLACK
    var borderAngleType = BorderAngleType.ANGLE_RIGHT

    var borderWidth = 1f
        set(aWidth)
        {
            field = if (aWidth < 0) 0f else aWidth
        }

    // for BingoGrid implementation
    override var type: PlayerType = PlayerType.PLAYER
        set(aType)
        {
            field = aType
            updateBackgroundColor()
        }

    override var value: Int = 0
        set(value)
        {
            field = value
            text = if (value > 0) value.toString() else ""
            updateBackgroundColor()
        }

    override var isConnected: Boolean = false
        set(value)
        {
            field = value
            updateBackgroundColor()
        }

    override var isSelectedOn: Boolean = false
        set(value)
        {
            field = value
            updateBackgroundColor()
        }

    override var locX: Int = 0
    override var locY: Int = 0

    override val clicked: Observable<BingoGridView>
        get() = this.clickedSubject
    override operator fun get(direction: ConnectedDirection): Boolean
    {
        return this.directions.get(direction.value())
    }

    override operator fun set(direction: ConnectedDirection, connected: Boolean)
    {
        this.directions[direction.value()] = connected

        if (!connected)
        {
            this.isConnected = this.directions.find {dir -> dir} == true
        }
        else
        {
            this.isConnected = connected
        }

        updateBackgroundColor()
        invalidate() // repaint
    }

    private val clickedSubject: BehaviorSubject<BingoGridView> = BehaviorSubject.create()
    private var directions = BooleanArray(4)
    private lateinit var _paint: Paint

    constructor(context: Context) : super(context)
    {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        initView()
    }

    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        _paint.color = borderColor
        _paint.strokeWidth = borderWidth

        when (borderAngleType)
        {
            BorderAngleType.ANGLE_ROUND ->
                canvas.drawRoundRect(RectF(0f, 0f, width - 1, height - 1), 5f, 5f, _paint)

            BorderAngleType.ANGLE_RIGHT ->
                canvas.drawRect(0f, 0f, width - 1, height - 1, _paint)
        }


        _paint.color = 0x400000FF
        _paint.strokeWidth = 6f

        val offset = width / 12f

        // draw connected line
        for (i in directions.indices)
        {
            if (directions[i])
            {
                when (ConnectedDirection.parse(i))
                {
                    ConnectedDirection.LEFT_TOP_RIGHT_BOTTOM ->
                        canvas.drawLine(-offset, -offset, width + offset, height + offset, _paint)

                    ConnectedDirection.RIGHT_TOP_LEFT_BOTTOM ->
                        canvas.drawLine(width + offset, -offset, -offset, height + offset, _paint)

                    ConnectedDirection.HORIZONTAL ->
                        canvas.drawLine(-offset, height / 2, width + offset, height / 2, _paint)

                    ConnectedDirection.VERTICAL ->
                        canvas.drawLine(width / 2, -offset, width / 2, height + offset, _paint)

                    else -> {}
                }
            }
        }
    }

    override fun initial()
    {
        this.isConnected = false
        this.isSelectedOn = false

        for (i in this.directions.indices)
        {
            this.directions[i] = false
        }

        this.value = 0
    }

    private fun updateBackgroundColor()
    {
        setTextColor(Color.BLACK)

        if (this.isConnected)
        {
            setBackgroundColor(Color.RED)
        }
        else if (this.isSelectedOn)
        {
            setBackgroundColor(Color.YELLOW)
        }
        else if (value != 0 && type == PlayerType.PLAYER)
        {
            setBackgroundColor(Color.LTGRAY)
        }
        else
        {
            if (type == PlayerType.COMPUTER)
            {
                setTextColor(Color.TRANSPARENT)
            }

            setBackgroundColor(Color.GRAY)
        }
    }

    private fun initView()
    {
        this.gravity = Gravity.CENTER

        this.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            CustomProperties.getDimensionPxSize(FontType.NORMAL_FONT_SIZE, context).toFloat())

        this.setOnClickListener {
            this.clickedSubject.onNext(this)
        }
        
        this.isConnected = false
        this.isSelectedOn = false

        _paint = Paint()
        _paint.style = Style.STROKE
        _paint.isAntiAlias = true
    }
}
