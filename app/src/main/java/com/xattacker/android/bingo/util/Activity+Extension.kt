package com.xattacker.android.bingo

import android.app.Activity
import android.os.Handler
import android.os.Looper

import com.xattacker.android.bingo.util.AlertButtonStyle
import com.xattacker.android.bingo.util.AlertDialogCreator
import com.xattacker.android.bingo.util.AlertTitleType

val Any.isMainThread : Boolean
    get() = Looper.getMainLooper().thread == Thread.currentThread()

fun Any.delay(milliSecond: Long, call: () -> Unit)
{
    if (!isMainThread)
    {
        // Can't create handler inside thread that has not called Looper.prepare()
        Looper.prepare()
    }

    val handler = Handler()
    handler.postDelayed(
        object: Runnable {
            override fun run()
            {
                call.invoke()
            }
        },
        milliSecond)
}

fun Activity.showConfirmDialog(
    aText: String,
    aClicked: ((which: Int) -> Unit)? = null)
{
    AlertDialogCreator.showDialog(
        AlertTitleType.Confirm,
        AlertButtonStyle.YesNo,
        getString(R.string.CONFIRM_EXIT),
        this) {
        dialog, which ->
        aClicked?.invoke(which)
        dialog?.dismiss()
    }
}

fun Activity.showDialog(aType: AlertTitleType, aText: String)
{
    AlertDialogCreator.showDialog(aType, aText, this)
}