package com.xattacker.android.bingo.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.Display
import android.view.Window
import android.view.WindowManager

object AppProperties
{
    private var _activity: Activity? = null
    val appPath: String
        get() = _activity!!.filesDir.absolutePath
    val appVersion: String?
        get()
        {
            var version: String? = null

            try
            {
                version = _activity!!.packageManager.getPackageInfo(_activity!!.packageName, 0).versionName
            }
            catch (ex: Exception)
            {
            }

            return version
        }
    val screenDisplay: Display?
        get()
        {
            var display: Display? = null

            if (_activity != null)
            {
                val manager = _activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                display = manager.defaultDisplay
            }

            return display
        }
    val displayHeight: Int
        get()
        {
            val rect = Rect()
            val win = _activity!!.window
            win.decorView.getWindowVisibleDisplayFrame(rect)

            return rect.bottom - rect.top
        }
    val scaledDensity: Float
        get()
        {
            val dm = DisplayMetrics()
            _activity!!.windowManager.defaultDisplay.getMetrics(dm)

            return dm.scaledDensity
        }
    val density: Float
        get()
        {
            val dm = DisplayMetrics()
            _activity!!.windowManager.defaultDisplay.getMetrics(dm)

            return dm.density
        }
    val densityDpi: Int
        get()
        {
            val dm = DisplayMetrics()
            _activity!!.windowManager.defaultDisplay.getMetrics(dm)

            return dm.densityDpi
        }
    val widthDensity: Float
        get()
        {
            val display = screenDisplay

            return display!!.width / 320f
        }
    val heightDensity: Float
        get()
        {
            val display = screenDisplay

            return display!!.height / 480f
        }
    val screenLayout: Int
        get()
        {
            val config = _activity!!.resources.configuration

            return config.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK
        }

    fun initial(aActivity: Activity)
    {
        _activity = aActivity
    }

    fun release()
    {
        _activity = null
    }
} // in order to hide constructor
