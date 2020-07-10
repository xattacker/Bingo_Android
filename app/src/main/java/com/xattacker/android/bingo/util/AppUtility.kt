package com.xattacker.android.bingo.util

import java.util.ArrayList
import android.content.Context

object AppUtility
{
    fun getString(aContext: Context, aResId: Int, vararg aParameters: String): String
    {
        var str = aContext.getString(aResId)

        if (aParameters != null && aParameters.size > 0)
        {
            var index = -1
            var replaced: String? = null
            var para: String? = null
            val builder = StringBuilder(str)
            var i = 0
            val size = aParameters.size
            while (i < size)
            {
                para = aParameters[i]
                if (para != null)
                {
                    replaced = "[$i]"

                    index = builder.indexOf(replaced)
                    if (index != -1)
                    {
                        builder.replace(index, index + replaced.length, para)
                    }
                }
                i++
            }

            str = builder.toString()
        }

        return str
    }

    fun getString(aContext: Context, aResId: Int, vararg aResParameters: Int): String
    {
        var str = aContext.getString(aResId)

        if (aResParameters.size > 0)
        {
            var index = -1
            var replaced: String? = null
            var res = 0
            val builder = StringBuilder(str)
            var i = 0
            val size = aResParameters.size
            while (i < size)
            {
                res = aResParameters[i]
                if (res != 0)
                {
                    replaced = "[$i]"

                    index = builder.indexOf(replaced)
                    if (index != -1)
                    {
                        builder.replace(index, index + replaced.length, aContext.getString(res))
                    }
                }
                i++
            }

            str = builder.toString()
        }

        return str
    }

    fun getString(aContext: Context, aResId: Int, aParameters: ArrayList<String>): String
    {
        val array = aParameters.toTypedArray()

        return getString(aContext, aResId, *array)
    }
} // in order to hide constructor
