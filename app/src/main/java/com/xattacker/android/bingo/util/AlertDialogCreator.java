package com.xattacker.android.bingo.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ListAdapter;

import com.xattacker.android.bingo.R;

public final class AlertDialogCreator
{
	public enum AlertTitleType
	{
		NOTIFICATION_ALERT,
		WARNING_ALERT,
		ERROR_ALERT,
		CONFIRM_ALERT
	};

	public enum AlertButtonStyle
	{
		BUTTON_YES_NO,
		BUTTON_OK
	};

	// in order to hide constructor
	private AlertDialogCreator()
	{	
	}

	public static void showDialog
	(
	AlertTitleType aType, 
	String aText,
	Context aContext 
	)
	{
	   AlertDialog.Builder builder = createBuilder(aContext);
		builder.setTitle(createTitleText(aType, aContext));
		builder.setMessage(aText);
		  
		builder.setPositiveButton
		(
		aContext.getString(R.string.OK), 
		new DialogInterface.OnClickListener() 
		{  
			public void onClick(DialogInterface dialog, int which) 
			{  
				dialog.dismiss();
			}
		}
		);  
		
		builder.create();
		builder.show();
	}
	
	public static void showDialog
	(
	AlertTitleType aType, 
	AlertButtonStyle aStyle, 
	String aText,
	Context aContext,
	DialogInterface.OnClickListener aListener
	)
	{
		String button1 = null, button2 = null;
		
	   AlertDialog.Builder builder = createBuilder(aContext);
		builder.setTitle(createTitleText(aType, aContext));
		builder.setMessage(aText);
		
		switch (aStyle)
		{
			case BUTTON_YES_NO:
				button1 = aContext.getString(R.string.YES);
				button2 = aContext.getString(R.string.NO);
				break;
				
			case BUTTON_OK:
				button1 = aContext.getString(R.string.OK);
				break;
		}
		
		builder.setPositiveButton(button1, aListener); 
		
		if (button2 != null)
		{
			builder.setNegativeButton(button2, aListener); 
		}
		
		builder.create();
		builder.show();
	}
	
	public static void showDialog
	(
	String aTitle,
	ListAdapter aAdapter,
	Context aContext,
	DialogInterface.OnClickListener aListener
	)
	{
	   AlertDialog.Builder builder = createBuilder(aContext);
		builder.setTitle(aTitle);
		builder.setAdapter(aAdapter, aListener);
		
		builder.setPositiveButton
		(
		aContext.getString(R.string.CANCEL), 
		new DialogInterface.OnClickListener() 
		{  
			public void onClick(DialogInterface dialog, int which) 
			{  
				dialog.dismiss();
			}
		}
		);  
		
		builder.create();
		builder.show();
	}
	
	private static AlertDialog.Builder createBuilder(Context aContext)
	{
	   AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
		builder.setIcon(R.drawable.app_icon);
		builder.setInverseBackgroundForced(true);
		
		return builder;
	}
	
	private static String createTitleText(AlertTitleType aType, Context aContext)
	{
		switch (aType)
		{
			case NOTIFICATION_ALERT:
				return aContext.getString(R.string.NOTIFICATION);

			case WARNING_ALERT:
				return aContext.getString(R.string.WARNING);

			case ERROR_ALERT:
				return aContext.getString(R.string.ERROR);
				
			case CONFIRM_ALERT:
				return aContext.getString(R.string.CONFIRM);
		}
		
		return "";
	}
}
