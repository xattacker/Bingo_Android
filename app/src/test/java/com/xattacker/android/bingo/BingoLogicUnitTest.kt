package com.xattacker.android.bingo

import com.xattacker.android.bingo.logic.*
import org.junit.Test

import org.junit.Assert.*
import kotlin.math.log

/**
 Unit Test for BingoLogic and related classes
 */
open class BingoLogicUnitTest : BingoLogicListener
{
    companion object
    {
        val GRID_DIMENSION = 5
    }

    private var winner: PlayerType? = null
    private var count: Int = 0

    @Test
    fun testLogic()
    {
        val logic = BingoLogic(this, GRID_DIMENSION)

        setupGrids(PlayerType.COMPUTER) {
            grid, x, y, type ->
            logic.addGrid(type, grid, x, y)
        }

        val grids = setupGrids(PlayerType.PLAYER) {
            grid, x, y, type ->
            logic.addGrid(type, grid, x, y)
        }

        logic.fillNumber(PlayerType.COMPUTER)
        logic.fillNumber(PlayerType.PLAYER)

        do
        {
            for (sub in grids)
            {
                for (grid in sub)
                {
                    if (!grid.isSelectedOn)
                    {
                        grid.isSelectedOn = true
                        logic.winCheck(grid.locX, grid.locY)

                        if (winner != null)
                        {
                            break
                        }
                    }
                }

                if (winner != null)
                {
                    break
                }
            }
        } while (winner == null)


        assertTrue("winner is wrong", this.winner != null)
        assertTrue("connected count is wrong", this.count == GRID_DIMENSION)
    }

    override fun onLineConnected(aTurn: PlayerType, aCount: Int)
    {
        this.count = aCount
    }

    override fun onWon(aWinner: PlayerType)
    {
        this.winner = aWinner
    }

    protected fun setupGrids(
        type: PlayerType,
        added: (grid: MockBingoGrid, x: Int, y: Int, type: PlayerType) -> Unit): ArrayList<ArrayList<MockBingoGrid>>
    {
        val grids = ArrayList<ArrayList<MockBingoGrid>>()

        for (i in 0 .. GRID_DIMENSION - 1)
        {
            val sub_array = ArrayList<MockBingoGrid>()

            for (j in 0 .. GRID_DIMENSION - 1)
            {
                val grid = MockBingoGrid()
                grid.type = type
                grid.value = 0
                grid.locX = i
                grid.locY = j
                sub_array.add(grid)
                added(grid, i, j, type)
            }

            grids.add(sub_array)
        }

        return grids
    }

    fun method(callback: () -> Unit){
        callback.invoke()
    }
}
