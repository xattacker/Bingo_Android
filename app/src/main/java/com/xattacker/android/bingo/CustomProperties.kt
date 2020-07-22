package com.xattacker.android.bingo

import android.content.Context
import android.util.DisplayMetrics
import android.view.Display
import com.xattacker.android.bingo.util.AppProperties
import java.util.Hashtable

object CustomProperties
{
    private val FONT_SIZE = Hashtable<FontType, Int>()

    fun getDimensionPxSize(aType: FontType, aContext: Context): Int
    {
        var size = 0

        if (FONT_SIZE.contains(aType))
        {
            size = FONT_SIZE[aType] ?: 0
        }
        else // not found, create new one
        {
            var res = 0

            when (aType)
            {
                FontType.VERY_LARGE_FONT_SIZE -> res = R.dimen.VERY_LARGE_FONT_SIZE
                FontType.LARGE_FONT_SIZE -> res = R.dimen.LARGE_FONT_SIZE
                FontType.LARGE_NORMAL_FONT_SIZE -> res = R.dimen.LARGE_NORMAL_FONT_SIZE
                FontType.NORMAL_FONT_SIZE -> res = R.dimen.NORMAL_FONT_SIZE
                FontType.SMALL_NORMAL_FONT_SIZE -> res = R.dimen.SMALL_NORMAL_FONT_SIZE
                FontType.SMALL_FONT_SIZE -> res = R.dimen.SMALL_FONT_SIZE
                FontType.VERY_SMALL_FONT_SIZE -> res = R.dimen.VERY_SMALL_FONT_SIZE
            }

            size = aContext.resources.getDimensionPixelSize(res)
            FONT_SIZE[aType] = size
        }

        return size
    }

    fun getScreenWidth(aRatio: Float): Int
    {
        var width = 0
        val display = AppProperties.screenDisplay
        if (display != null)
        {
            val dm = DisplayMetrics()
            display.getMetrics(dm)
            width = (dm.widthPixels.toFloat() * aRatio).toInt()
        }

        return width
    }

    fun getScreenHeight(aRatio: Float): Int
    {
        var height = 0
        val display = AppProperties.screenDisplay
        if (display != null)
        {
            val dm = DisplayMetrics()
            display.getMetrics(dm)
            height = (dm.heightPixels.toFloat() * aRatio).toInt()
        }

        return height
    }
}
