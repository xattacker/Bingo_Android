package com.xattacker.android.bingo.logic

interface BingoLogicListener
{
    fun onLineConnected(aTurn: PlayerType, aCount: Int)
    fun onWon(aWinner: PlayerType)
}
