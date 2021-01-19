package com.xattacker.android.bingo.view

import android.content.Context
import android.graphics.Color
import android.view.View
import com.xattacker.android.bingo.BingoGridView
import com.xattacker.android.bingo.logic.ConnectedDirection
import com.xattacker.android.bingo.logic.PlayerType
import io.reactivex.Observable

class FlippableCardView: FlippableView, BingoGridView
{
    private val gridView: GridView

    constructor(context: Context) : super(context)
    {
        this.gridView = GridView(context)

        val back_frame = View(context)
        back_frame.setBackgroundColor(Color.GRAY)

        this.setupView(back_frame, this.gridView)
    }

    override var locX: Int
        get() = this.gridView.locX
        set(value)
        {
            this.gridView.locX = value
        }

    override var locY: Int
        get() = this.gridView.locY
        set(value)
        {
            this.gridView.locY = value
        }

    override val clicked: Observable<BingoGridView>
        get() = this.gridView.clicked

    override var type: PlayerType
        get() =  this.gridView.type
        set(value)
        {
            this.gridView.type = value
        }

    override var value: Int
        get() =  this.gridView.value
        set(value)
        {
            this.gridView.value = value
        }

    override var isSelectedOn: Boolean
        get() =  this.gridView.isSelectedOn
        set(value)
        {
            if (!this.isFlipped && value)
            {
                this.flip()
            }

            this.gridView.isSelectedOn = value
        }

    override var isConnected: Boolean
        get() =  this.gridView.isConnected
        set(value)
        {
            this.gridView.isConnected = value
        }

    override fun initial()
    {
        this.gridView.initial()

        if (this.isFlipped)
        {
            this.flip(false)
        }
    }

    override fun isLineConnected(aDirection: ConnectedDirection): Boolean
    {
        return  this.gridView.isLineConnected(aDirection)
    }

    override fun setConnectedLine(aDirection: ConnectedDirection, aConnected: Boolean)
    {
        this.gridView.setConnectedLine(aDirection, aConnected)
    }
}