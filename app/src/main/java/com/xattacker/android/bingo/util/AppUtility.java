package com.xattacker.android.bingo.util;

import java.util.ArrayList;

import android.content.Context;

public final class AppUtility
{
	// in order to hide constructor
	private AppUtility()
	{
	}
	
	public static String getString(Context aContext, int aResId, String... aParameters)
	{
		String str = aContext.getString(aResId);

		if (aParameters != null && aParameters.length > 0)
		{
			int index = -1;
			String replaced = null;
			String para = null;
			StringBuilder builder = new StringBuilder(str);
			
			for (int i = 0, size = aParameters.length; i < size; i++)
			{
				para = aParameters[i];
				if (para != null)
				{
					replaced = "[" + i + "]";
					
					index = builder.indexOf(replaced);
					if (index != -1)
					{
						builder.replace(index, index + replaced.length(), para);
					}
				}
			}
			
			str = builder.toString();
		}

		return str;
	}
	
	public static String getString(Context aContext, int aResId, int... aResParameters)
	{
		String str = aContext.getString(aResId);

		if (aResParameters != null && aResParameters.length > 0)
		{
			int index = -1;
			String replaced = null;
			int res = 0;
			StringBuilder builder = new StringBuilder(str);
			
			for (int i = 0, size = aResParameters.length; i < size; i++)
			{
				res = aResParameters[i];
				if (res != 0)
				{
					replaced = "[" + i + "]";
					
					index = builder.indexOf(replaced);
					if (index != -1)
					{
						builder.replace(index, index + replaced.length(), aContext.getString(res));
					}
				}
			}
			
			str = builder.toString();
		}

		return str;
	}
	
	public static String getString(Context aContext, int aResId, ArrayList<String> aParameters)
	{
		String[] array = aParameters.toArray(new String[aParameters.size()]);
		
		return getString(aContext, aResId, array);
	}
}
