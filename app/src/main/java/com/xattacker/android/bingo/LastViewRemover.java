package com.xattacker.android.bingo;

import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

public class LastViewRemover implements AnimationListener
{
	private View _lastView;
	private int _count;
	
	public LastViewRemover(View aView)
	{
		_lastView = aView;
		_count = 0;
	}

	public void onAnimationStart(Animation arg0)
	{
		if (_lastView != null)
		{
			_lastView.setBackgroundColor(Color.RED);
		}
	}
	
	public void onAnimationEnd(final Animation arg0)
	{
		if (_lastView != null)
		{
			_lastView.setBackgroundColor(Color.TRANSPARENT);
		}
		
		
		_count++;
		
		if (_count < 2)
		{
			// repeat 2 times
			Handler handler = new Handler();
			handler.postDelayed
			(
			new Runnable() 
			{
			  public void run() 
			  {	
				  _lastView.startAnimation(arg0);
			  }
			},
			300
			);
		}
	}
	
	public void onAnimationRepeat(Animation arg0)
	{
	}
}
