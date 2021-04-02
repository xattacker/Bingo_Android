package com.xattacker.android.bingo.logic

import com.xattacker.android.bingo.BingoActivity
import java.lang.ref.WeakReference

class BingoLogic
{
    inner class GridHolder
    {
        var connection: Int = 0 /* 連棋數 */
        lateinit var grids: Array<Array<BingoGrid?>>
    }

    val maxGridValue: Int
        get() = Math.pow(dimension.toDouble(), 2.0).toInt()

    private var _locX: Int = 0
    private var _locY: Int = 0 /* 下棋位置 */
    private var _connected: Int = 0 /* 連棋數 */
    private val _weight: IntArray = IntArray(3) // 權重

    private var _turn: PlayerType = PlayerType.PLAYER
    private var _gameOver: Boolean = false
    private val _grids: Array<GridHolder>
    private var listener: WeakReference<BingoLogicListener>? = null
    private var dimension: Int = 0

    constructor(listener: BingoLogicListener, dimension: Int)
    {
        this.listener = WeakReference(listener)
        this.dimension = dimension

        val grid1 = GridHolder()
        grid1.grids = Array(this.dimension) {arrayOfNulls<BingoGrid>(this.dimension)}

        val grid2 = GridHolder()
        grid2.grids = Array(this.dimension) {arrayOfNulls<BingoGrid>(this.dimension)}

        _grids = arrayOf(grid1, grid2)
    }

    fun restart()
    {
        _connected = 0
        _weight[2] = 0
        _gameOver = false

        for (i in 0 .. 1)
        {
            _grids[i].connection = 0

            for (j in 0 .. this.dimension - 1)
            {
                for (k in 0 .. this.dimension - 1)
                {
                    _grids[i].grids[j][k]?.initial()
                }
            }
        }
    }

    fun addGrid(aType: PlayerType, aGrid: BingoGrid, aX: Int, aY: Int)
    {
        _grids[aType.value()].grids[aX][aY] = aGrid
    }

    fun getConnectionCount(aType: PlayerType): Int
    {
        return _grids[aType.value()].connection
    }

    fun fillNumber(type: PlayerType)
    {
        val tag = type.value()
        var temp_value = 0
        var x = 0
        var y = 0

        for (i in 0 ..  this.dimension - 1)
        {
            for (j in 0 .. this.dimension - 1)
            {
                _grids[tag].grids[i][j]?.value = (i * this.dimension) + (j + 1)
            }
        }

        for (i in 0 .. this.dimension - 1)
        {
            for (j in 0 .. this.dimension - 1)
            {
                temp_value = _grids[tag].grids[i][j]?.value ?: 0

                x = (Math.random() * this.dimension).toInt()
                y = (Math.random() * this.dimension).toInt()

                _grids[tag].grids[i][j]?.value = _grids[tag].grids[x][y]?.value ?: 0
                _grids[tag].grids[x][y]?.value = temp_value
            }
        }
    }

    fun winCheck(aX: Int, aY: Int)
    {
        winCheck(PlayerType.PLAYER, aX, aY, true)
    }

    private fun winCheck(aType: PlayerType, aX: Int, aY: Int, aRedo: Boolean)
    {
        if (!_gameOver)
        {
            _turn = aType
            _locX = aX
            _locY = aY

            winCheck(ConnectedDirection.LEFT_TOP_RIGHT_BOTTOM)

            if (!_gameOver && aRedo)
            {
                redo(_grids[_turn.value()].grids[aX][aY]?.value ?: 0)

                if (aType == PlayerType.PLAYER && !_gameOver)
                {
                    runAI()
                }
            }
        }
    }

    private fun winCheck(aDirection: ConnectedDirection)
    {
        if (aDirection == ConnectedDirection.NIL)
        {
            return
        }


        val offset = aDirection.offset()
        var x = _locX + offset.first
        var y = _locY + offset.second

        _connected = 1

        while (x >= 0 && x < this.dimension && y >= 0 && y < this.dimension && _grids[_turn.value()].grids[x][y]?.isSelectedOn == true)
        {
            _connected = _connected + 1
            x = x + offset.first
            y = y + offset.second
        }

        x = _locX - offset.first
        y = _locY - offset.second

        while (x >= 0 && x < this.dimension && y >= 0 && y < this.dimension && _grids[_turn.value()].grids[x][y]?.isSelectedOn == true)
        {
            _connected = _connected + 1
            x = x - offset.first
            y = y - offset.second
        }


        if (_connected >= this.dimension)
        {
            x = _locX
            y = _locY

            while (x >= 0 && x < this.dimension && y >= 0 && y < this.dimension && _grids[_turn.value()].grids[x][y]?.isSelectedOn == true)
            {
                _grids[_turn.value()].grids[x][y]?.setConnectedLine(aDirection, true)
                x = x + offset.first
                y = y + offset.second
            }

            x = _locX - offset.first
            y = _locY - offset.second

            while (x >= 0 && x < this.dimension && y >= 0 && y < this.dimension && _grids[_turn.value()].grids[x][y]?.isSelectedOn == true)
            {
                _grids[_turn.value()].grids[x][y]?.setConnectedLine(aDirection, true)
                x = x - offset.first
                y = y - offset.second
            }

            _grids[_turn.value()].connection += 1

            listener?.get()?.onLineConnected(_turn, _grids[_turn.value()].connection)

            if (_grids[_turn.value()].connection >= this.dimension && !_gameOver)
            {
                listener?.get()?.onWon(_turn)
                _gameOver = true
            }
        }

        if (!_gameOver)
        {
            winCheck(aDirection.next())
        }
    }

    // after the one side done, the other side do the same value
    private fun redo(aValue: Int)
    {
        _turn = _turn.opposite()

        for (i in 0 .. this.dimension - 1)
        {
            for (j in 0 .. this.dimension - 1)
            {
                if (_grids[_turn.value()].grids[i][j]?.value == aValue)
                {
                    _grids[_turn.value()].grids[i][j]?.isSelectedOn = true
                    winCheck(_turn, i, j, false)

                    break
                }
            }
        }
    }

    private fun runAI()
    {
        _turn = PlayerType.COMPUTER

        for (i in 0 .. this.dimension - 1)
        {
            for (j in 0 .. this.dimension - 1)
            {
                if (_grids[_turn.value()].grids[i][j]?.isSelectedOn == false)
                {
                    runAI2(i, j)
                }
            }
        }

        if (_weight[2] > 1)
        {
            _weight[2] = 0
            _grids[_turn.value()].grids[_weight[0]][_weight[1]]?.isSelectedOn = true
            winCheck(_turn, _weight[0], _weight[1], true)
        }
        else
        {
            randomAI()
        }
    }

    private fun runAI2(aX: Int, aY: Int)
    {
        _locX = aX
        _locY = aY

        var w = 0
        var dir = ConnectedDirection.LEFT_TOP_RIGHT_BOTTOM

        do
        {
            val offset = dir.offset()
            w = w + calculateWeight(offset.first, offset.second)
            dir = dir.next()

        } while (dir != ConnectedDirection.NIL)

        if (w > _weight[2])
        {
            _weight[0] = _locX
            _weight[1] = _locY
            _weight[2] = w
        }
    }

    private fun randomAI()
    {
        var x = 0
        var y = 0
        val center = this.dimension / 2

        if (_grids[_turn.value()].grids[center][center]?.isSelectedOn == false) // the first priority is center
        {
            x = center
            y = center
        }
        else
        {
            do
            {
                x = (Math.random() * this.dimension).toInt()
                y = (Math.random() * this.dimension).toInt()

            } while (_grids[_turn.value()].grids[x][y]?.isSelectedOn == true)
        }

        _grids[_turn.value()].grids[x][y]?.isSelectedOn = true
        winCheck(_turn, x, y, true)
    }

    private fun calculateWeight(aOffsetX: Int, aOffsetY: Int): Int
    {
        var w = 0
        var x = _locX
        var y = _locY
        
        _connected = 0

        while (x >= 0 && x < this.dimension && y >= 0 && y < this.dimension)
        {
            if (_grids[PlayerType.COMPUTER.value()].grids[x][y]?.isSelectedOn == true)
            {
                w = w + 1
            }

            _connected = _connected + 1
            x = x + aOffsetX
            y = y + aOffsetY
        }

        x = _locX - aOffsetX
        y = _locY - aOffsetY

        while (x >= 0 && x < this.dimension && y >= 0 && y < this.dimension)
        {
            if (_grids[PlayerType.COMPUTER.value()].grids[x][y]?.isSelectedOn == true)
            {
                w = w + 1
            }

            _connected = _connected + 1
            x = x - aOffsetX
            y = y - aOffsetY
        }

        if (w == this.dimension - 1) // 加重已有四個被選擇的行列權重
        {
            w = w + 1
        }

        return if (_connected == this.dimension) w * w else 0
    }
}
