package com.xattacker.android.bingo

import com.xattacker.android.bingo.logic.BingoGrid
import com.xattacker.android.bingo.logic.BingoLogic
import com.xattacker.android.bingo.logic.BingoLogicListener
import com.xattacker.android.bingo.logic.PlayerType
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.lang.ref.WeakReference

enum class GameStatus
{
    PREPARE,
    PLAYING,
    END
}


interface  BingoGridView: BingoGrid
{
    var locX: Int
    var locY: Int
    val clicked: Observable<BingoGridView>
}


class BingoViewModel: BingoLogicListener
{
    val gradeRecord: Observable<GradeRecord>
        get() = this.gradeRecordSubject

    val status: Observable<GameStatus>
        get() = this.statusSubject

    val lineConnected: Observable<Pair<PlayerType, Int>>
        get() = this.lineConnectedSubject

    val onWon: Observable<PlayerType>
        get() = this.wonSubject

    private val gradeRecordSubject: BehaviorSubject<GradeRecord> = BehaviorSubject.create()
    private val statusSubject: BehaviorSubject<GameStatus> = BehaviorSubject.createDefault(GameStatus.PREPARE)
    private val lineConnectedSubject: PublishSubject<Pair<PlayerType, Int>> = PublishSubject.create()
    private val wonSubject: PublishSubject<PlayerType> =  PublishSubject.create()

    private var numDoneCount = 0 // 佈子數, 當玩家把25個數字都佈完後 開始遊戲
    private val recorder = GradeRecorder()
    private var logic: BingoLogic

    constructor(dimension: Int)
    {
        this.logic = BingoLogic(this, dimension)

        this.gradeRecordSubject.onNext(this.recorder)
    }

    override fun onLineConnected(aTurn: PlayerType, aCount: Int)
    {
        this.lineConnectedSubject.onNext(Pair(aTurn, aCount))
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
        this.wonSubject.onNext(aWinner)
    }

    fun addGrid(grid: BingoGridView)
    {
        this.logic.addGrid(grid.type, grid, grid.locX, grid.locY)

        if (grid.type == PlayerType.PLAYER)
        {
            // bind click event
            grid.clicked.subscribe {
                view: BingoGridView ->
                handleGridClick(view)
            }
        }
    }

    fun fillNumber()
    {
        this.logic.fillNumber(PlayerType.PLAYER)
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
        this.logic.fillNumber(PlayerType.COMPUTER)
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