package com.xattacker.android.bingo

import com.xattacker.android.bingo.logic.BingoGrid
import com.xattacker.android.bingo.logic.ConnectedDirection
import com.xattacker.android.bingo.logic.PlayerType
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class MockBingoGrid : BingoGridView
{
    override var type: PlayerType = PlayerType.NONE
    override var value: Int = 0
    override var isSelectedOn: Boolean = false
    override var isConnected: Boolean = false

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
    }

    private val clickedSubject: BehaviorSubject<BingoGridView> = BehaviorSubject.create()
    private var directions = BooleanArray(4)

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

    fun click()
    {
        this.clickedSubject.onNext(this)
    }
}