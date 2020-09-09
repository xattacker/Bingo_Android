package com.xattacker.android.bingo

import com.xattacker.android.bingo.logic.BingoGrid
import com.xattacker.android.bingo.logic.BingoLogic
import com.xattacker.android.bingo.logic.BingoLogicListener
import com.xattacker.android.bingo.logic.PlayerType
import io.reactivex.subjects.BehaviorSubject
import java.lang.ref.WeakReference

enum class GameStatus
{
    PREPARE,
    PLAYING,
    END
}

class BingoViewModel: BingoLogicListener
{
    val gradeRecordBinding: BehaviorSubject<GradeRecord> = BehaviorSubject.create()
    val statusBinding: BehaviorSubject<GameStatus> = BehaviorSubject.create()

    private var status: GameStatus = GameStatus.PREPARE
        set(value)
        {
            field = value
            statusBinding.onNext(value)
        }

    private var numDoneCount = 0 // 佈子數, 當玩家把25個數字都佈完後 開始遊戲
    private val recorder = GradeRecorder()
    private var logic: BingoLogic
    private var logicListener: WeakReference<BingoLogicListener>

    constructor(listener: BingoLogicListener, dimension: Int)
    {
        this.logic = BingoLogic(this, dimension)
        this.logicListener = WeakReference(listener)
    }

    override fun onLineConnected(aTurn: PlayerType, aCount: Int)
    {
        // bypass to another listener
        this.logicListener.get()?.onLineConnected(aTurn, aCount)
    }

    override fun onWon(aWinner: PlayerType)
    {
        if (aWinner == PlayerType.COMPUTER)
        {
            this.recorder.addLose()
        }
        else // player won
        {
            this.recorder.addWin()
        }

        this.gradeRecordBinding.onNext(recorder)
        this.status = GameStatus.END

        // bypass to another listener
        this.logicListener.get()?.onWon(aWinner)
    }

    fun addGrid(type: PlayerType, grid: BingoGrid, x: Int, y: Int)
    {
        this.logic.addGrid(type, grid, x, y)
    }

    fun fillNumber(type: PlayerType)
    {
        this.logic.fillNumber(type)
    }

    fun handleGridClick(grid: BingoGrid, x: Int, y: Int)
    {
        when (this.status)
        {
            GameStatus.PREPARE ->
            {
                if (grid.value <= 0)
                {
                    numDoneCount++
                    grid.value = numDoneCount

                    if (grid.value >= this.logic.maxGridValue)
                    {
                        startPlaying()
                    }
                }
            }

            GameStatus.PLAYING ->
            {
                if (!grid.isSelectedOn)
                {
                    grid.isSelectedOn = true
                    this.logic.winCheck(x, y)
                }
            }

            GameStatus.END ->
                restart()
        }
    }

    fun restart()
    {
        this.numDoneCount = 0
        this.status = GameStatus.PREPARE
        this.logic.restart()
    }

    fun startPlaying()
    {
        this.logic.fillNumber()
        this.status = GameStatus.PLAYING
    }
}