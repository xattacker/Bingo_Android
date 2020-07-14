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

    fun offset(): Pair<Int, Int>
    {
        val offset = IntArray(2)

        when (this)
        {
            ConnectedDirection.OBLIQUE_1 ->
            {
                offset[0] = 1
                offset[1] = -1
            }

            ConnectedDirection.OBLIQUE_2 ->
            {
                offset[0] = 1
                offset[1] = 1
            }

            ConnectedDirection.HORIZONTAL ->
            {
                offset[0] = 1
                offset[1] = 0
            }

            ConnectedDirection.VERTICAL ->
            {
                offset[0] = 0
                offset[1] = 1
            }

            else ->
            {
            }
        }

        return Pair(offset[0], offset[1])
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
