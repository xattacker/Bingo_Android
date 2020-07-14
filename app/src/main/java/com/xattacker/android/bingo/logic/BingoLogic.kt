package com.xattacker.android.bingo.logic

class BingoLogic(private val _listener: BingoLogicListener?)
{
    private var _locX: Int = 0
    private var _locY: Int = 0 /* 下棋位置 */
    private var _connected: Int = 0 /* 連棋數 */
    private val _connects: IntArray // 雙方連線數
    private val _weight: IntArray // 權重

    val winner: PlayerType
         get() = _turn

    private var _turn: PlayerType = PlayerType.PLAYER

    private var _gameOver: Boolean = false
    private val _grids: Array<Array<Array<BingoGrid?>>>

    init
    {
        _connects = IntArray(2)
        _weight = IntArray(3)
        _connected = 0
        _gameOver = false
        _grids = Array(2) {Array(5) {arrayOfNulls<BingoGrid>(5)}}
    }

    fun restart()
    {
        _connected = 0
        _weight[2] = 0
        _gameOver = false

        for (i in 0 .. 1)
        {
            _connects[i] = 0

            for (j in 0 .. 4)
            {
                for (k in 0 .. 4)
                {
                    _grids[i][j][k]?.initial()
                }
            }
        }
    }

    fun addGrid(aType: PlayerType, aGrid: BingoGrid, aX: Int, aY: Int)
    {
        _grids[aType.value()][aX][aY] = aGrid
        _grids[aType.value()][aX][aY]?.type = aType
    }

    fun getConnectionCount(aType: PlayerType): Int
    {
        return _connects[aType.value()]
    }

    fun fillNumber(type: PlayerType = PlayerType.COMPUTER)
    {
        val tag = type.value()
        var temp_value = 0
        var x = 0
        var y = 0

        for (i in 0 .. 4)
        {
            for (j in 0 .. 4)
            {
                _grids[tag][i][j]?.value = i * 5 + (j + 1)
            }
        }

        for (i in 0 .. 4)
        {
            for (j in 0 .. 4)
            {
                temp_value = _grids[tag][i][j]?.value ?: 0

                x = (Math.random() * 5).toInt()
                y = (Math.random() * 5).toInt()

                _grids[tag][i][j]?.value = _grids[tag][x][y]?.value ?: 0
                _grids[tag][x][y]?.value = temp_value
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

            winCheck(ConnectedDirection.OBLIQUE_1)

            if (!_gameOver && aRedo)
            {
                redo(_grids[_turn.value()][aX][aY]?.value ?: 0)

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

        while (x >= 0 && x < 5 && y >= 0 && y < 5 && _grids[_turn.value()][x][y]?.isSelectedOn == true)
        {
            _connected = _connected + 1
            x = x + offset.first
            y = y + offset.second
        }

        x = _locX - offset.first
        y = _locY - offset.second

        while (x >= 0 && x < 5 && y >= 0 && y < 5 && _grids[_turn.value()][x][y]?.isSelectedOn == true)
        {
            _connected = _connected + 1
            x = x - offset.first
            y = y - offset.second
        }


        if (_connected >= 5)
        {
            x = _locX
            y = _locY

            while (x >= 0 && x < 5 && y >= 0 && y < 5 && _grids[_turn.value()][x][y]?.isSelectedOn == true)
            {
                _grids[_turn.value()][x][y]?.setConnectedLine(aDirection, true)
                x = x + offset.first
                y = y + offset.second
            }

            x = _locX - offset.first
            y = _locY - offset.second

            while (x >= 0 && x < 5 && y >= 0 && y < 5 && _grids[_turn.value()][x][y]?.isSelectedOn == true)
            {
                _grids[_turn.value()][x][y]?.setConnectedLine(aDirection, true)
                x = x - offset.first
                y = y - offset.second
            }

            _connects[_turn.value()] = _connects[_turn.value()] + 1

            _listener?.onLineConnected(_turn, _connects[_turn.value()])

            if (_connects[_turn.value()] >= 5 && !_gameOver)
            {
                _listener?.onWon(_turn)
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

        for (i in 0 .. 4)
        {
            for (j in 0 .. 4)
            {
                if (_grids[_turn.value()][i][j]?.value == aValue)
                {
                    _grids[_turn.value()][i][j]?.isSelectedOn = true
                    winCheck(_turn, i, j, false)

                    break
                }
            }
        }
    }

    private fun runAI()
    {
        _turn = PlayerType.COMPUTER

        for (i in 0 .. 4)
        {
            for (j in 0 .. 4)
            {
                if (_grids[_turn.value()][i][j]?.isSelectedOn == false)
                {
                    runAI2(i, j)
                }
            }
        }

        if (_weight[2] > 1)
        {
            _weight[2] = 0
            _grids[_turn.value()][_weight[0]][_weight[1]]?.isSelectedOn = true
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
        var dir = ConnectedDirection.OBLIQUE_1

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

        if (_grids[_turn.value()][2][2]?.isSelectedOn == false) // the first priority is center
        {
            y = 2
            x = y
        }
        else
        {
            do
            {
                x = (Math.random() * 5).toInt()
                y = (Math.random() * 5).toInt()

            } while (_grids[_turn.value()][x][y]?.isSelectedOn == true)
        }

        _grids[_turn.value()][x][y]?.isSelectedOn = true
        winCheck(_turn, x, y, true)
    }

    private fun calculateWeight(aOffsetX: Int, aOffsetY: Int): Int
    {
        var w = 0
        var x = _locX
        var y = _locY
        
        _connected = 0

        while (x >= 0 && x < 5 && y >= 0 && y < 5)
        {
            if (_grids[PlayerType.COMPUTER.value()][x][y]?.isSelectedOn == true)
            {
                w = w + 1
            }

            _connected = _connected + 1
            x = x + aOffsetX
            y = y + aOffsetY
        }

        x = _locX - aOffsetX
        y = _locY - aOffsetY

        while (x >= 0 && x < 5 && y >= 0 && y < 5)
        {
            if (_grids[PlayerType.COMPUTER.value()][x][y]?.isSelectedOn == true)
            {
                w = w + 1
            }

            _connected = _connected + 1
            x = x - aOffsetX
            y = y - aOffsetY
        }

        if (w == 4) // 加重已有四個被選擇的行列權重
        {
            w = w + 1
        }

        return if (_connected == 5) w * w else 0
    }
}
