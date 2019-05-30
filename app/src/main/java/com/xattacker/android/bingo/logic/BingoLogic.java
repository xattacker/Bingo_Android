package com.xattacker.android.bingo.logic;

public class BingoLogic
{
	private final static int PLAYER = 1;
	private final static int COMPUTER = 0;
	
	public enum PlayerType
	{
		COMPUTER(0),
		PLAYER(1);
		
		
		private int _value;

		private PlayerType(int aValue)
		{
			_value = aValue;
		}

		public int value()
		{
			return _value;
		}
		
		public static PlayerType parse(int aValue)
		{
			for (PlayerType type : PlayerType.values())
			{
				if (type._value == aValue)
				{
					return type;
				}
			}

			return PLAYER;
		}
	}
	
	private BingoLogicListener _listener;
	private int _locX, _locY /* 下棋位置 */, _connected; /* 連棋數 */
	private int[] _connects; // 雙方連線數
	private int[] _weight; // 權重
	private PlayerType _turn;
	private boolean _gameOver;
	private BingoGrid[][][] _grids;
	
	public BingoLogic(BingoLogicListener aListener)
	{
		_connects = new int[2];
		_weight = new int[3];
		_connected = 0;
		_gameOver = false;
		_grids = new BingoGrid[2][5][5];
		_listener = aListener;
	}
	
	public void restart()
	{
		_connected = 0;
		_weight[2] = 0;
		_gameOver = false;
		
		for (int i = 0; i < 2; i++)
		{
			_connects[i] = 0;

			for (int j = 0; j < 5; j++)
			{
				for (int k = 0; k < 5; k++)
				{
					_grids[i][j][k].initial();
				}
			}
		}
	}
	
	public void addGrid(PlayerType aType, BingoGrid aGrid, int aX, int aY)
	{
		int index = -1;

		switch (aType)
		{
			case COMPUTER:
				index = COMPUTER;
				break;
				
			case PLAYER:
				index = PLAYER;
				break;
		}
		
		_grids[index][aX][aY] = aGrid;
		_grids[index][aX][aY].setType(aType);
	}

	public int getConnectionCount(PlayerType aType)
	{
		return _connects[aType.value()];
	}
	
	public PlayerType getWinner()
	{
		return _turn;
	}

	public void resetComputer()
	{
		int temp_value = 0, x = 0, y = 0;
		
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				_grids[COMPUTER][i][j].setValue(i * 5 + (j + 1));
			}
		}
		
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				temp_value = _grids[COMPUTER][i][j].getValue();

				x = (int) (Math.random() * 5);
				y = (int) (Math.random() * 5);

				_grids[COMPUTER][i][j].setValue(_grids[COMPUTER][x][y].getValue());
				_grids[COMPUTER][x][y].setValue(temp_value);
			}
		}
	}
	
	public void winCheck(int aX, int aY)
	{
		winCheck(PlayerType.PLAYER, aX, aY, true);
	}
	
	private void winCheck(PlayerType aType, int aX, int aY, boolean aRedo)
	{
		if (!_gameOver)
		{
			_turn = aType;
			_locX = aX;
			_locY = aY;
			
			winCheck(ConnectedDirection.OBLIQUE_1);
			
			if (!_gameOver && aRedo)
			{
				reDo(_grids[_turn.value()][aX][aY].getValue());
			
				if (aType == PlayerType.PLAYER && !_gameOver)
				{
					runAI();
				}
			}
		}
	}
	
	private void winCheck(ConnectedDirection aDirection)
	{
		if (aDirection == ConnectedDirection.NIL)
		{
			return;
		}
		

		int[] offset = getOffsetValue(aDirection);
		int x = _locX + offset[0];
		int y = _locY + offset[1];
		
		_connected = 1;

		while (
				x >= 0 && x < 5 && y >= 0 && y < 5 && 
				_grids[_turn.value()][x][y].isSelected()
				)
		{
			_connected = _connected + 1;
			x = x + offset[0];
			y = y + offset[1];
		}

		x = _locX - offset[0];
		y = _locY - offset[1];

		while (
				x >= 0 && x < 5 && y >= 0 && y < 5 && 
				_grids[_turn.value()][x][y].isSelected()
				)
		{
			_connected = _connected + 1;
			x = x - offset[0];
			y = y - offset[1];
		}

		
		if (_connected >= 5)
		{
			x = _locX;
			y = _locY;

			while (
					x >= 0 && x < 5 && y >= 0 && y < 5 && 
					_grids[_turn.value()][x][y].isSelected()
					)
			{
				_grids[_turn.value()][x][y].setConnectedLine(aDirection, true);
				x = x + offset[0];
				y = y + offset[1];
			}

			x = _locX - offset[0];
			y = _locY - offset[1];

			while (
					x >= 0 && x < 5 && y >= 0 && y < 5 && 
					_grids[_turn.value()][x][y].isSelected()
					)
			{
				_grids[_turn.value()][x][y].setConnectedLine(aDirection, true);
				x = x - offset[0];
				y = y - offset[1];
			}

			_connects[_turn.value()] = _connects[_turn.value()] + 1;
			
			if (_listener != null)
			{
				_listener.onLineConnected(_turn, _connects[_turn.value()]);
			}

			if (_connects[_turn.value()] >= 5 && !_gameOver)
			{
				if (_listener != null)
				{
					_listener.onWon(_turn);
				}
				
				_gameOver = true;
			}
		}

		if (!_gameOver)
		{
			winCheck(aDirection.next());
		}
	}
	
	private int[] getOffsetValue(ConnectedDirection aDirection)
	{
		int[] offset = new int[2];

		switch (aDirection)
		{
			case OBLIQUE_1:
				offset[0] = 1;
				offset[1] = -1;
				break;
	
			case OBLIQUE_2:
				offset[0] = offset[1] = 1;
				break;
	
			case HORIZONTAL:
				offset[0] = 1;
				offset[1] = 0;
				break;
	
			case VERTICAL:
				offset[0] = 0;
				offset[1] = 1;
				break;
				
			default:
				break;
		}

		return offset;
	}
	
	// after the one side done, the other side do the same value
	private void reDo(int aValue)
	{
		_turn = (_turn == PlayerType.PLAYER) ? PlayerType.COMPUTER : PlayerType.PLAYER;

		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				if (_grids[_turn.value()][i][j].getValue() == aValue)
				{
					_grids[_turn.value()][i][j].setSelected(true);
					winCheck(_turn, i, j, false);
					
					break;
				}
			}
		}
	}
	
	private void runAI()
	{
		_turn = PlayerType.COMPUTER;
		
		for (int i = 0; i < 5; i++)
		{
			for (int j = 0; j < 5; j++)
			{
				if (!_grids[_turn.value()][i][j].isSelected())
				{
					runAI2(i, j);
				}
			}
		}
		
		if (_weight[2] > 1)
		{
			_weight[2] = 0;
			_grids[_turn.value()][_weight[0]][_weight[1]].setSelected(true);
			winCheck(_turn, _weight[0], _weight[1], true);
		}
		else
		{
			randomAI();
		}
	}
	
	private void runAI2(int aX, int aY)
	{
		_locX = aX;
		_locY = aY;

		int offset_x = 0, offset_y = 0;
		int w = 0;
		ConnectedDirection dir = ConnectedDirection.OBLIQUE_1;

		do
		{
			switch (dir)
			{
				case OBLIQUE_1:
					offset_x = 1;
					offset_y = -1;
					break;
	
				case OBLIQUE_2:
					offset_x = offset_y = 1;
					break;
	
				case HORIZONTAL:
					offset_x = 1;
					offset_y = 0;
					break;
	
				case VERTICAL:
					offset_x = 0;
					offset_y = 1;
					break;
					
				default:
					break;
			}

			w = w + calculateWeight(offset_x, offset_y);
			dir = dir.next();
			
		} while (dir != ConnectedDirection.NIL);

		if (w > _weight[2])
		{
			_weight[0] = _locX;
			_weight[1] = _locY;
			_weight[2] = w;
		}
	}
	
	private void randomAI()
	{
		int x = 0, y = 0;

		if (!_grids[_turn.value()][2][2].isSelected())// the first priority is center
		{
			x = y = 2;
		}
		else
		{
			do
			{
				x = (int) (Math.random() * 5);
				y = (int) (Math.random() * 5);
				
			} while (_grids[_turn.value()][x][y].isSelected());
		}

		_grids[_turn.value()][x][y].setSelected(true);
		winCheck(_turn, x, y, true);
	}
	
	private int calculateWeight(int aOffsetX, int aOffsetY)
	{
		int w = 0;
		_connected = 0;

		int x = _locX, y = _locY;

		while (x >= 0 && x < 5 && y >= 0 && y < 5)
		{
			if (_grids[COMPUTER][x][y].isSelected())
			{
				w = w + 1;
			}
			
			_connected = _connected + 1;
			x = x + aOffsetX;
			y = y + aOffsetY;
		}

		x = _locX - aOffsetX;
		y = _locY - aOffsetY;

		while (x >= 0 && x < 5 && y >= 0 && y < 5)
		{
			if (_grids[COMPUTER][x][y].isSelected())
			{
				w = w + 1;
			}
			
			_connected = _connected + 1;
			x = x - aOffsetX;
			y = y - aOffsetY;
		}

		if (w == 4)// 加重已有四個被選擇的行列權重
		{
			w = w + 1;
		}
		
		return (_connected == 5) ? w * w : 0;
	}
}
