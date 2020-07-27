package com.xattacker.android.bingo

import android.widget.Toast
import com.xattacker.android.bingo.logic.BingoLogic
import com.xattacker.android.bingo.logic.BingoLogicListener
import com.xattacker.android.bingo.logic.PlayerType

enum class GameStatus
{
    PREPARE,
    PLAYING,
    END
}

class BingoViewModel: BingoLogicListener
{
    val recorder = GradeRecorder()
    var onStatusUpdated: ((status: GameStatus) -> Void)? = null

    private var status: GameStatus = GameStatus.PREPARE
    private var numDoneCount = 0 // 佈子數, 當玩家把25個數字都佈完後 開始遊戲
    private var logic: BingoLogic

    constructor(listener: BingoLogicListener, dimension: Int)
    {
        this.logic = BingoLogic(listener, dimension)
    }

    override fun onLineConnected(aTurn: PlayerType, aCount: Int)
    {
    }

    override fun onWon(aWinner: PlayerType)
    {
    }

    private fun restart()
    {
        this.status = GameStatus.PREPARE
        this.numDoneCount = 0
        this.logic.restart()

        onStatusUpdated?.invoke(this.status)
    }

    private fun startPlaying()
    {
        this.logic.fillNumber()
        this.status = GameStatus.PLAYING

        onStatusUpdated?.invoke(this.status)
    }
}