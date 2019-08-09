package com.xattacker.android.bingo

class GradeRecorder
{
    var winCount: Int = 0
        private set
    var lostCount: Int = 0
        private set

    init
    {
        lostCount = 0
        winCount = lostCount
    }

    fun addWinCount()
    {
        winCount++
    }

    fun addLoseCount()
    {
        lostCount++
    }
}
