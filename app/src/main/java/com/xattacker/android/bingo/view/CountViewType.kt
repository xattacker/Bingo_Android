package com.xattacker.android.bingo.view

interface CountViewType
{
    var count: Int
    var countColor: Int
}

val CountViewType.maxCount: Int
    get() = 5

fun CountViewType.reset()
{
    this.count = 0
}