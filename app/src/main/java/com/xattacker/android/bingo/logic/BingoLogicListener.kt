package com.xattacker.android.bingo.logic

import com.xattacker.android.bingo.logic.BingoLogic.PlayerType

interface BingoLogicListener
{
    fun onLineConnected(aType: PlayerType, aCount: Int)
    fun onWon(aWinner: PlayerType?)
}
