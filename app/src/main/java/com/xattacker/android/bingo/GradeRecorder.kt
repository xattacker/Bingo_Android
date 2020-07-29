package com.xattacker.android.bingo


interface GradeRecord
{
    val winCount: Int
    val loseCount: Int
}

class GradeRecorder : GradeRecord
{
    override var winCount: Int = 0
        private set

    override var loseCount: Int = 0
        private set

    fun addWin()
    {
        winCount++
    }

    fun addLose()
    {
        loseCount++
    }

    fun reset()
    {
        winCount = 0
        loseCount = 0
    }
}
