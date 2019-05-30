package com.xattacker.android.bingo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.View;

final class CountView extends View
{
	private int _count;
	
	public CountView(Context context)
	{
		super(context);
		
		_count = 0;
	}
	
	public void setCount(int aCount)
	{
		if (aCount >= 0 && aCount <= 5)
		{
			_count = aCount;
			invalidate(); // repaint
		}
	}
	
	public void addCount()
	{
		if (_count < 5)
		{
			_count++;
			invalidate(); // repaint
		}
	}

	@Override
	protected void onDraw(Canvas aCanvas)
	{	
		super.onDraw(aCanvas);

		int width = getWidth(), height = getHeight();
		float size = 2.5f;
		
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(size);
		paint.setAntiAlias(true);
		
		if (_count >= 1)
		{
			aCanvas.drawLine(0, 0, width, 0, paint);
		}
		
		if (_count >= 2)
		{
			aCanvas.drawLine(width/2, 0, width/2, height, paint);
		}
		
		if (_count >= 3)
		{
			aCanvas.drawLine(width/2, height/3, width, height/3, paint);
		}
		
		if (_count >= 4)
		{
			aCanvas.drawLine(width/3, (height/3)*2, width/3, height, paint);
		}
		
		if (_count >= 5)
		{
			aCanvas.drawLine(0, height, width, height, paint);
		}
	}
}
