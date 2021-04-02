package com.xattacker.android.bingo

import com.xattacker.android.bingo.logic.PlayerType
import io.reactivex.disposables.CompositeDisposable
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


        var got_status: GameStatus? = null
        var disposable = viewModel.status.subscribe {
            status: GameStatus ->

            got_status = status
        }
        this.disposableBag.add(disposable)

        var count = 0
        disposable = viewModel.lineConnected.subscribe {
            connected: Pair<PlayerType, Int> ->
            count = connected.second
        }
        this.disposableBag.add(disposable)


        var got_winner: PlayerType? = null
        disposable = viewModel.onWon.subscribe {
            winner: PlayerType ->
            got_winner = winner
        }
        this.disposableBag.add(disposable)


        viewModel.fillNumber()
        Assert.assertTrue("game status is wrong", got_status == GameStatus.PLAYING)

        do
        {
            for (sub in grids)
            {
                for (grid in sub)
                {
                    if (!grid.isSelectedOn)
                    {
                        grid.click()

                        if (got_winner != null)
                        {
                            break
                        }
                    }
                }

                if (got_winner != null)
                {
                    break
                }
            }
        } while (got_winner == null)

        Assert.assertTrue("winner is wrong", got_winner != null)
        Assert.assertTrue("connected count is wrong", count == GRID_DIMENSION)

        viewModel.restart()
        Assert.assertTrue("game status is wrong", got_status == GameStatus.PREPARE)
    }

    @After
    fun end()
    {
        disposableBag.clear()
        disposableBag.dispose()
    }
}