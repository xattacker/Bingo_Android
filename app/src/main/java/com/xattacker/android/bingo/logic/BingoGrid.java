package com.xattacker.android.bingo.logic;

import com.xattacker.android.bingo.logic.BingoLogic.PlayerType;

public interface BingoGrid
{
	void initial();
	
	PlayerType getType();
	void setType(PlayerType aType);
	
	int getValue();
	void setValue(int aValue);
	
	boolean isSelected();
	void setSelected(boolean aSelected);
	
	boolean isConnected();
	void setConnected(boolean aConnected);
	
	boolean isLineConnected(ConnectedDirection aDirection);
	void setConnectedLine(ConnectedDirection aDirection, boolean aConnected);
}
