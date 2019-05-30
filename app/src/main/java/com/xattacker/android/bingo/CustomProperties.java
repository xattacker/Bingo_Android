package com.xattacker.android.bingo;

import android.content.Context;
import android.view.Display;

import com.xattacker.android.bingo.util.AppProperties;

import java.util.Hashtable;

public final class CustomProperties
{
	private static Hashtable<FontType, Integer> FONT_SIZE = new Hashtable<FontType, Integer>();

	public static int getDimensionPxSize(FontType aType, Context aContext)
	{
		int size = 0;
		
		if (FONT_SIZE.contains(aType))
		{
			size = FONT_SIZE.get(aType);
		}
		else // not found, create new one
		{
			int res = 0;
			
			switch (aType)
			{
				case VERY_LARGE_FONT_SIZE:
					res = R.dimen.VERY_LARGE_FONT_SIZE;
					break;
					
				case LARGE_FONT_SIZE:
					res = R.dimen.LARGE_FONT_SIZE;
					break;
					
				case LARGE_NORMAL_FONT_SIZE:
					res = R.dimen.LARGE_NORMAL_FONT_SIZE;
					break;
					
				case NORMAL_FONT_SIZE:
					res = R.dimen.NORMAL_FONT_SIZE;
					break;
					
				case SMALL_NORMAL_FONT_SIZE:
					res = R.dimen.SMALL_NORMAL_FONT_SIZE;
					break;
					
				case SMALL_FONT_SIZE:
					res = R.dimen.SMALL_FONT_SIZE;
					break;
					
				case VERY_SMALL_FONT_SIZE:
					res = R.dimen.VERY_SMALL_FONT_SIZE;
					break;
			}

			size = aContext.getResources().getDimensionPixelSize(res);
			FONT_SIZE.put(aType, size);
		}
		
		return size;
	}
	
	public static int getScreenWidth(float aRatio)
	{
		int width = 0;
		
		Display display = AppProperties.getScreenDisplay();
		if (display != null)
		{
			width = (int)(display.getWidth() * aRatio);
		}
		
		return width;
	}
	
	public static int getScreenHeight(float aRatio)
	{
		int height = 0;
		
		Display display = AppProperties.getScreenDisplay();
		if (display != null)
		{
			height = (int)(display.getHeight() * aRatio);
		}
		
		return height;
	}
}
