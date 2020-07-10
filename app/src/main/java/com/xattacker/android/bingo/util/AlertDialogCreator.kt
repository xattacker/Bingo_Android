package com.xattacker.android.bingo.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.ListAdapter
import com.xattacker.android.bingo.R

enum class AlertTitleType
{
    NOTIFICATION_ALERT(R.string.NOTIFICATION),
    WARNING_ALERT(R.string.WARNING),
    ERROR_ALERT(R.string.ERROR),
    CONFIRM_ALERT(R.string.CONFIRM);

    val value: Int

    constructor(value: Int)
    {
        this.value = value
    }
}

sealed class AlertButtonStyle
{
    open class TwiceButton(open val button1: Int, open val button2: Int): AlertButtonStyle()
    open class SingleButton(open val button: Int): AlertButtonStyle()

    // object
    object YesNo: TwiceButton(R.string.YES, R.string.NO)
    object OK: SingleButton(R.string.OK)
}


object AlertDialogCreator
{
    fun showDialog(aType: AlertTitleType, aText: String, aContext: Context)
    {
        val builder = createBuilder(aContext)
        builder.setTitle(aContext.getString(aType.value))
        builder.setMessage(aText)

        builder.setPositiveButton(aContext.getString(R.string.OK)) {dialog, which -> dialog.dismiss()}

        builder.create()
        builder.show()
    }

    fun showDialog(
        aType: AlertTitleType,
        aStyle: AlertButtonStyle,
        aText: String,
        aContext: Context,
        aClicked: (dialog: DialogInterface?, which: Int) -> Unit)
    {
        var button1: String? = null
        var button2: String? = null

        val builder = createBuilder(aContext)
        builder.setTitle(aContext.getString(aType.value))
        builder.setMessage(aText)

        when (aStyle)
        {
            is AlertButtonStyle.YesNo ->
            {
                button1 = aContext.getString(aStyle.button1)
                button2 = aContext.getString(aStyle.button2)
            }

            is AlertButtonStyle.OK -> button1 = aContext.getString(aStyle.button)
        }

        val listener = object : DialogInterface.OnClickListener
        {
            override fun onClick(dialog: DialogInterface?, which: Int)
            {
                aClicked.invoke(dialog, which)
            }
        }

        builder.setPositiveButton(button1, listener)

        if (button2 != null)
        {
            builder.setNegativeButton(button2, listener)
        }

        builder.create()
        builder.show()
    }

    fun showDialog(
        aTitle: String,
        aAdapter: ListAdapter,
        aContext: Context,
        aClicked: (dialog: DialogInterface?, which: Int) -> Unit)
    {
        val listener = object : DialogInterface.OnClickListener
        {
            override fun onClick(dialog: DialogInterface?, which: Int)
            {
                aClicked.invoke(dialog, which)
            }
        }

        val builder = createBuilder(aContext)
        builder.setTitle(aTitle)
        builder.setAdapter(aAdapter, listener)

        builder.setPositiveButton(aContext.getString(R.string.CANCEL)) {dialog, which -> dialog.dismiss()}

        builder.create()
        builder.show()
    }

    private fun createBuilder(aContext: Context): AlertDialog.Builder
    {
        val builder = AlertDialog.Builder(aContext)
        builder.setIcon(R.drawable.app_icon)
        builder.setInverseBackgroundForced(true)
        builder.setCancelable(false)

        return builder
    }
}
