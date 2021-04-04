package com.xattacker.android.bingo

import com.xattacker.android.bingo.logic.PlayerType
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.TestObserver
import org.junit.After
import org.junit.Assert
import org.junit.Test

class BingoViewModelUnitTest : BingoLogicUnitTest()
{
    companion object
    {
        private val GRID_DIMENSION = 5
    }

    private val disposableBag = CompositeDisposable()

    @Test
    fun testViewModel()
    {
        val viewModel = BingoViewModel(GRID_DIMENSION)

        setupGrids(PlayerType.COMPUTER) {
            grid, x, y, type ->
            viewModel.addGrid(grid)
        }

        val grids = setupGrids(PlayerType.PLAYER) {
            grid, x, y, type ->
            viewModel.addGrid(grid)
        }


        val statusObserver = TestObserver<GameStatus>()
        viewModel.status.subscribe(statusObserver)
        this.disposableBag.add(statusObserver)

        val countObserver = TestObserver<Int>()
        viewModel.lineConnected.map { connected: Pair<PlayerType, Int> -> connected.second }.subscribe(countObserver)
        this.disposableBag.add(countObserver)


        val winnerObserver = TestObserver<PlayerType>()
        viewModel.onWon.subscribe(winnerObserver)
        this.disposableBag.add(winnerObserver)


        viewModel.fillNumber()
        Assert.assertTrue("game status is wrong", statusObserver.values().last() == GameStatus.PLAYING)

        do
        {
            for (sub in grids)
            {
                for (grid in sub)
                {
                    if (!grid.isSelectedOn)
                    {
                        grid.click()

                        if (winnerObserver.isTerminated)
                        {
                            break
                        }
                    }
                }

                if (winnerObserver.isTerminated)
                {
                    break
                }
            }
        } while (winnerObserver.isTerminated)

        Assert.assertTrue("winner is wrong", winnerObserver.values().last() != PlayerType.NONE)
        //countObserver.assertResult(GRID_DIMENSION)
        Assert.assertTrue("connected count is wrong", countObserver.values().last() == GRID_DIMENSION)

        viewModel.restart()
        Assert.assertTrue("game status is wrong", statusObserver.values().last() == GameStatus.PREPARE)
    }

    @After
    fun end()
    {
        disposableBag.clear()
        disposableBag.dispose()
    }
}