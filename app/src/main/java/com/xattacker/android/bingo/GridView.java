package com.xattacker.android.bingo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.TextView;

import com.xattacker.android.bingo.logic.BingoGrid;
import com.xattacker.android.bingo.logic.BingoLogic.PlayerType;
import com.xattacker.android.bingo.logic.ConnectedDirection;

public final class GridView extends TextView implements BingoGrid
{
	public enum BorderAngleType
	{
		ANGLE_ROUND,
		ANGLE_RIGHT;
	}

	private int _borderColor = Color.BLACK;
	private BorderAngleType _angle = BorderAngleType.ANGLE_RIGHT;
	private float _borderWidth = 1;
	private Paint _paint = null;
	
	// for BingoGrid implementation
	private PlayerType _type;
	private int _x, _y;
	private boolean _selected;
	private boolean _connected;
	private boolean[] _direction;
	
	public GridView(Context context)
	{
		super(context);
		
		initView();
	}
	
	public GridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		initView();
	}
	
	public int getBorderColor()
	{
		return _borderColor;
	}
	
	public void setBorderColor(int aColor)
	{
		_borderColor = aColor;
	}
	
	public BorderAngleType getBorderAngleType()
	{
		return _angle;
	}
	
	public void setBorderAngleType(BorderAngleType aType)
	{
		_angle = aType;
	}
	
	public float getBorderWidth()
	{
		return _borderWidth;
	}
	
	public void setBorderWidth(float aWidth)
	{
		_borderWidth = aWidth < 0 ? 0 : aWidth;
	}

	public int getLocX()
	{
		return _x;
	}

	public void setLocX(int aX)
	{
		_x = aX;
	}

	public int getLocY()
	{
		return _y;
	}

	public void setLocY(int aY)
	{
		_y = aY;
	}
	
	@Override
	protected void onDraw(Canvas aCanvas)
	{
		super.onDraw(aCanvas);

		int width = getWidth(), height = getHeight();
		
		_paint.setColor(_borderColor);
		_paint.setStrokeWidth(_borderWidth);

		switch (_angle)
		{
			case ANGLE_ROUND:	
				aCanvas.drawRoundRect(new RectF(0, 0, width-1, height-1), 5, 5, _paint);
				break;
				
			case ANGLE_RIGHT:
				aCanvas.drawRect(0, 0, width-1, height-1, _paint);
				break;
		}
		
		
		_paint.setColor(0x400000FF);
		_paint.setStrokeWidth(5f);
		
		// draw connected line
		for (int i = 0; i < _direction.length; i++)
		{
			if (_direction[i])
			{
				switch (ConnectedDirection.parse(i))
				{
					case OBLIQUE_1:
						aCanvas.drawLine(width, 0, 0, height, _paint);
						break;
	
					case OBLIQUE_2:
						aCanvas.drawLine(0, 0, width, height, _paint);
						break;
	
					case HORIZONTAL:
						aCanvas.drawLine(width/2, 0, width/2, height, _paint);
						break;
	
					case VERTICAL:
						aCanvas.drawLine(0, height/2, width, height/2, _paint);
						break;
						
					default:
						break;
				}
			}
		}
	}

	public void initial()
	{
		_selected = _connected = false;

		for (int i = 0; i < _direction.length; i++)
		{
			_direction[i] = false;
		}
		
		setValue(_type == PlayerType.COMPUTER ? _x * 5 + (_y + 1) : 0);
	}
	
	public PlayerType getType()
	{
		return _type;
	}

	public void setType(PlayerType aType)
	{
		_type = aType;
		updateBackgroundColor();
	}
	
	public int getValue()
	{
		return getId();
	}

	public void setValue(int aValue)
	{
		setId(aValue);
		setText(aValue > 0 ? String.valueOf(aValue) : "");
		updateBackgroundColor();
	}
	
	public boolean isSelected()
	{
		return _selected;
	}
	
	public void setSelected(boolean aSelected)
	{
		_selected = aSelected;
		updateBackgroundColor();
	}

	public boolean isConnected()
	{
		return _connected;
	}

	public void setConnected(boolean aConnected)
	{
		_connected = aConnected;
		updateBackgroundColor();
	}

	public boolean isLineConnected(ConnectedDirection aDirection)
	{
		return _direction[aDirection.value()];
	}

	public void setConnectedLine(ConnectedDirection aDirection, boolean aConnected)
	{
		_direction[aDirection.value()] = aConnected;

		if (!aConnected)
		{
			int dirs = _direction.length;
			
			for (int i = 0; i < _direction.length; i++)
			{
				if (_direction[i])
				{
					break;
				}
				else
				{
					dirs--;
				}
			}

			// it means there is no any connection line in the grid,
			// the connected state should be false
			if (dirs == 0)
			{
				_connected = false;
			}
		}
		else
		{
			_connected = aConnected;
		}
		
		updateBackgroundColor();
		invalidate(); // repaint
	}

	private void updateBackgroundColor()
	{
		setTextColor(Color.BLACK);
		
		if (_connected)
		{
			setBackgroundColor(Color.RED);
		}
		else if (_selected)
		{
			setBackgroundColor(Color.YELLOW);
		}
		else if (getValue() != 0 && _type == PlayerType.PLAYER)
		{
			setBackgroundColor(Color.LTGRAY);
		}
		else
		{
			if (_type == PlayerType.COMPUTER)
			{
				setTextColor(Color.TRANSPARENT);
			}
			
			setBackgroundColor(Color.GRAY);
		}
	}
	
	private void initView()
	{
		_selected = _connected = false;
		_direction = new boolean[4];
		
		_paint = new Paint();
		_paint.setStyle(Style.STROKE);
		_paint.setAntiAlias(true);
	}
}
