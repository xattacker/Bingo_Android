package com.xattacker.android.bingo.view

interface CountViewInterface
{
    var count: Int
    var countColor: Int
}

val CountViewInterface.maxCount: Int
    get() = 5

fun CountViewInterface.reset()
{
    this.count = 0
}