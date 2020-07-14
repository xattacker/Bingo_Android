package com.xattacker.android.bingo.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.TextView
import com.xattacker.android.bingo.logic.BingoGrid
import com.xattacker.android.bingo.logic.PlayerType
import com.xattacker.android.bingo.logic.ConnectedDirection

class GridView : TextView, BingoGrid
{
    enum class BorderAngleType
    {
        ANGLE_ROUND, ANGLE_RIGHT
    }

    var borderColor = Color.BLACK
    var borderAngleType = BorderAngleType.ANGLE_RIGHT

    var borderWidth = 1f
        set(aWidth)
        {
            field = if (aWidth < 0) 0f else aWidth
        }

    private lateinit var _paint: Paint

    // for BingoGrid implementation
    override var type: PlayerType = PlayerType.PLAYER
        set(aType)
        {
            field = aType
            updateBackgroundColor()
        }

    var locX: Int = 0
    var locY: Int = 0

    private var _selected: Boolean = false
    private var _connected: Boolean = false
    private var _directions = BooleanArray(4)

    override var value: Int
        get() = id
        set(aValue)
        {
            id = aValue
            text = if (aValue > 0) aValue.toString() else ""
            updateBackgroundColor()
        }

    override var isConnected: Boolean
        get() = _connected
        set(aConnected)
        {
            _connected = aConnected
            updateBackgroundColor()
        }

    override var isSelectedOn: Boolean
        get() = _selected
        set(aValue)
        {
            _selected = aValue
            updateBackgroundColor()
        }

    constructor(context: Context) : super(context)
    {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    {
        initView()
    }

    override fun onDraw(aCanvas: Canvas)
    {
        super.onDraw(aCanvas)

        val width = width
        val height = height

        _paint.color = borderColor
        _paint.strokeWidth = borderWidth

        when (borderAngleType)
        {
            BorderAngleType.ANGLE_ROUND ->
                aCanvas.drawRoundRect(RectF(0f, 0f, (width - 1).toFloat(), (height - 1).toFloat()), 5f, 5f, _paint)

            BorderAngleType.ANGLE_RIGHT ->
                aCanvas.drawRect(0f, 0f, (width - 1).toFloat(), (height - 1).toFloat(), _paint)
        }


        _paint.color = 0x400000FF
        _paint.strokeWidth = 5f

        // draw connected line
        for (i in _directions.indices)
        {
            if (_directions[i])
            {
                when (ConnectedDirection.parse(i))
                {
                    ConnectedDirection.OBLIQUE_1 ->
                        aCanvas.drawLine(width.toFloat(), 0f, 0f, height.toFloat(), _paint)

                    ConnectedDirection.OBLIQUE_2 ->
                        aCanvas.drawLine(0f, 0f, width.toFloat(), height.toFloat(), _paint)

                    ConnectedDirection.HORIZONTAL ->
                        aCanvas.drawLine((width / 2).toFloat(), 0f, (width / 2).toFloat(), height.toFloat(), _paint)

                    ConnectedDirection.VERTICAL ->
                        aCanvas.drawLine(0f, (height / 2).toFloat(), width.toFloat(), (height / 2).toFloat(), _paint)

                    else ->
                    {
                    }
                }
            }
        }
    }

    override fun initial()
    {
        _connected = false
        _selected = false

        for (i in _directions.indices)
        {
           _directions[i] = false
        }

        value = if (type == PlayerType.COMPUTER) locX * 5 + (locY + 1) else 0
    }

    override fun isLineConnected(aDirection: ConnectedDirection): Boolean
    {
        return _directions.get(aDirection.value())
    }

    override fun setConnectedLine(aDirection: ConnectedDirection, aConnected: Boolean)
    {
        _directions.let {
            it[aDirection.value()] = aConnected

            if (!aConnected)
            {
                var dirs = it.size

                for (i in it.indices)
                {
                    if (it[i])
                    {
                        break
                    }
                    else
                    {
                        dirs--
                    }
                }

                // it means there is no any connection line in the grid,
                // the connected state should be false
                if (dirs == 0)
                {
                    _connected = false
                }
            }
            else
            {
                _connected = aConnected
            }
        }

        updateBackgroundColor()
        invalidate() // repaint
    }

    private fun updateBackgroundColor()
    {
        setTextColor(Color.BLACK)

        if (_connected)
        {
            setBackgroundColor(Color.RED)
        }
        else if (_selected)
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
        _connected = false
        _selected = _connected

        _paint = Paint()
        _paint.style = Style.STROKE
        _paint.isAntiAlias = true
    }
}
