package com.xattacker.android.bingo

class GradeRecorder
{
    var winCount: Int = 0
        private set

    var loseCount: Int = 0
        private set

    fun addWinCount()
    {
        winCount++
    }

    fun addLoseCount()
    {
        loseCount++
    }

    fun reset()
    {
        winCount = 0
        loseCount = 0
    }
}
