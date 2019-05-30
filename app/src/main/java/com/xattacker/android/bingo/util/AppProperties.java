package com.xattacker.android.bingo.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

public final class AppProperties
{
	private static Activity _activity;

	// in order to hide constructor
	private AppProperties()
	{
	}
	
	public static void initial(Activity aActivity)
	{
		_activity = aActivity;
	}
	
	public static void release()
	{
		_activity = null;
	}
	
	public static String getAppPath()
	{
		return _activity.getFilesDir().getAbsolutePath(); 
	}
	
	public static String getAppVersion()
	{
		String version = null;
		
		try 
		{
			version = _activity.getPackageManager().getPackageInfo(_activity.getPackageName(), 0).versionName;
		} 
		catch (Exception ex) 
		{	
		}
		
		return version;
	}
	
	public static Display getScreenDisplay()
	{
		Display display = null;
		
		if (_activity != null)
		{
			WindowManager manager = (WindowManager) _activity.getSystemService(Context.WINDOW_SERVICE);
			display = manager.getDefaultDisplay();
		}
		
		return display;
	}
	
	public static int getDisplayHeight()
	{
		Rect rect = new Rect();
	   Window win = _activity.getWindow();
	   win.getDecorView().getWindowVisibleDisplayFrame(rect);

	   return rect.bottom - rect.top;
	}
	
	public static float getScaledDensity()
	{
		DisplayMetrics dm = new DisplayMetrics(); 
		_activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		return dm.scaledDensity;
	}
	
	public static float getDensity()
	{
		DisplayMetrics dm = new DisplayMetrics(); 
		_activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		return dm.density;
	}
	
	public static int getDensityDpi()
	{
		DisplayMetrics dm = new DisplayMetrics(); 
		_activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

		return dm.densityDpi;
	}
	
	public static float getWidthDensity()
	{
		Display display = getScreenDisplay();

		return display.getWidth() / 320f;
	}
	
	public static float getHeightDensity()
	{
		Display display = getScreenDisplay();

		return display.getHeight() / 480f;
	}
	
	public static int getScreenLayout()
	{   
		Configuration config = _activity.getResources().getConfiguration();
	
		return config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
	}
}
