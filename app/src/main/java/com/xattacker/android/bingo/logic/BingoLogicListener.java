package com.xattacker.android.bingo.logic;

import com.xattacker.android.bingo.logic.BingoLogic.PlayerType;

public interface BingoLogicListener
{
	void onLineConnected(PlayerType aType, int aCount);
	void onWon(PlayerType aWinner);
}
