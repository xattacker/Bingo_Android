package com.xattacker.android.bingo.logic

enum class PlayerType constructor(private val _value: Int)
{
    COMPUTER(0),
    PLAYER(1);

    fun value(): Int
    {
        return _value
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