package com.xattacker.android.bingo.logic

enum class PlayerType constructor(private val _value: Int)
{
    COMPUTER(0),
    PLAYER(1);

    fun value(): Int
    {
        return _value
    }

    fun opposite() : PlayerType
    {
        return if (this == PlayerType.PLAYER) PlayerType.COMPUTER else PlayerType.PLAYER
    }

    companion object
    {
        fun parse(aValue: Int): PlayerType
        {
            for (type in PlayerType.values())
            {
                if (type._value == aValue)
                {
                    return type
                }
            }

            return PLAYER
        }
    }
}