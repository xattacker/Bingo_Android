package com.xattacker.android.bingo.logic

interface BingoGrid
{
    var type: PlayerType
    var value: Int
    var isSelectedOn: Boolean // could not use name isSelected: it will cause accidental override with class View
    var isConnected: Boolean

    fun initial()
    fun isLineConnected(aDirection: ConnectedDirection): Boolean
    fun setConnectedLine(aDirection: ConnectedDirection, aConnected: Boolean)
}
