package com.xattacker.android.bingo.logic;

public enum ConnectedDirection
{
	NIL(-1), // 無方向
	OBLIQUE_1(0), // 左上向右下
	OBLIQUE_2(1), // 右上向左下
	HORIZONTAL(2), // 橫向
	VERTICAL(3); // 直向
	
	
	private int _value;

	private ConnectedDirection(int aValue)
	{
		_value = aValue;
	}

	public int value()
	{
		return _value;
	}
	
	public ConnectedDirection next()
	{
		return parse(_value + 1);
	}
	
	public static ConnectedDirection parse(int aValue)
	{
		for (ConnectedDirection direction : ConnectedDirection.values())
		{
			if (direction._value == aValue)
			{
				return direction;
			}
		}

		return NIL;
	}
}
