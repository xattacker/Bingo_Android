package com.xattacker.android.bingo.logic

// 連線方向定義
enum class ConnectedDirection private constructor(private val _value: Int)
{
    NIL(-1), // 無方向
    OBLIQUE_1(0), // 左上向右下
    OBLIQUE_2(1), // 右上向左下
    HORIZONTAL(2), // 橫向
    VERTICAL(3); // 直向

    fun value(): Int
    {
        return _value
    }

    operator fun next(): ConnectedDirection
    {
        return parse(_value + 1)
    }

    companion object
    {
        fun parse(aValue: Int): ConnectedDirection
        {
            for (direction in ConnectedDirection.values())
            {
                if (direction._value == aValue)
                {
                    return direction
                }
            }

            return NIL
        }
    }
}
