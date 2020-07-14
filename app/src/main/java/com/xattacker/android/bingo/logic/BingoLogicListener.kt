package com.xattacker.android.bingo.logic

interface BingoLogicListener
{
    fun onLineConnected(aType: PlayerType, aCount: Int)
    fun onWon(aWinner: PlayerType)
}
