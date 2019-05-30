package com.xattacker.android.bingo;

public final class GradeRecorder
{
	private int _win, _lose;
	
	public GradeRecorder()
	{
		_win = _lose = 0;
	}
	
	public int getWinCount()
	{
		return _win;
	}
	
	public void addWinCount()
	{
		_win++;
	}
	
	public int getLostCount()
	{
		return _lose;
	}
	
	public void addLoseCount()
	{
		_lose++;
	}
}
