package com.xattacker.android.bingo

import com.xattacker.android.bingo.logic.BingoGrid
import com.xattacker.android.bingo.logic.BingoLogic
import com.xattacker.android.bingo.logic.BingoLogicListener
import com.xattacker.android.bingo.logic.PlayerType
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.lang.ref.WeakReference

enum class GameStatus
{
    PREPARE,
    PLAYING,
    END
}


interface  BingoGridView: BingoGrid
{
    val locX: Int
    val locY: Int
    val clicked: Observable<BingoGridView>
}


class BingoViewModel: BingoLogicListener
{
    val gradeRecord: Observable<GradeRecord>
        get() = this.gradeRecordSubject
    
    val status: Observable<GameStatus>
        get() = this.statusSubject

    private val gradeRecordSubject: BehaviorSubject<GradeRecord> = BehaviorSubject.create()
    private val statusSubject: BehaviorSubject<GameStatus> = BehaviorSubject.createDefault(GameStatus.PREPARE)
    private var numDoneCount = 0 // 佈子數, 當玩家把25個數字都佈完後 開始遊戲
    private val recorder = GradeRecorder()
    private var logic: BingoLogic
    private var logicListener: WeakReference<BingoLogicListener>

    constructor(listener: BingoLogicListener, dimension: Int)
    {
        this.logic = BingoLogic(this, dimension)
        this.logicListener = WeakReference(listener)

        this.gradeRecordSubject.onNext(this.recorder)
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

        this.gradeRecordSubject.onNext(recorder)
        this.statusSubject.onNext(GameStatus.END)

        // bypass to another listener
        this.logicListener.get()?.onWon(aWinner)
    }

    fun addGrid(grid: BingoGridView)
    {
        this.logic.addGrid(grid.type, grid, grid.locX, grid.locY)

        if (grid.type == PlayerType.PLAYER)
        {
            // bind click event
            grid.clicked.subscribe {
                grid: BingoGridView ->
                handleGridClick(grid)
            }
        }
    }

    fun fillNumber(type: PlayerType)
    {
        this.logic.fillNumber(type)
        this.startPlaying()
    }

    fun restart()
    {
        this.numDoneCount = 0
        this.statusSubject.onNext(GameStatus.PREPARE)
        this.logic.restart()
    }

    private fun startPlaying()
    {
        this.logic.fillNumber()
        this.statusSubject.onNext(GameStatus.PLAYING)
    }

    private fun handleGridClick(grid: BingoGridView)
    {
        when (this.statusSubject.value)
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
                    this.logic.winCheck(grid.locX, grid.locY)
                }
            }

            GameStatus.END ->
                restart()
        }
    }
}