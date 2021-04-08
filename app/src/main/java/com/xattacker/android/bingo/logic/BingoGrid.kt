package com.xattacker.android.bingo.logic

interface BingoGrid
{
    var type: PlayerType
    var value: Int
    var isSelectedOn: Boolean // could not use name isSelected: it will cause accidental override with class View
    var isConnected: Boolean

    operator fun get(direction: ConnectedDirection): Boolean
    operator fun set(direction: ConnectedDirection, connected: Boolean)

    fun initial()
}
