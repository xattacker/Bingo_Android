package com.xattacker.android.bingo;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.xattacker.android.bingo.logic.BingoLogic;
import com.xattacker.android.bingo.logic.BingoLogic.PlayerType;
import com.xattacker.android.bingo.logic.BingoLogicListener;
import com.xattacker.android.bingo.util.AlertDialogCreator;
import com.xattacker.android.bingo.util.AlertDialogCreator.AlertButtonStyle;
import com.xattacker.android.bingo.util.AlertDialogCreator.AlertTitleType;
import com.xattacker.android.bingo.util.AppProperties;
import com.xattacker.android.bingo.util.AppUtility;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BingoActivity extends Activity 
                           implements OnClickListener, BingoLogicListener
{
	private enum GameStatus
	{
		PREPARE,
		PLAYING,
		END
	}
	
	@BindView(R.id.layout_ai_grid)
   TableLayout _aiTable;
	
	@BindView(R.id.layout_player_grid)
   TableLayout _playerTable;
	
	@BindView(R.id.text_ai_count)
   TextView _aiCountView;
	
	@BindView(R.id.text_player_count)
   TextView _playerCountView;
	
	@BindView(R.id.text_record)
   TextView _recordView;
	
	private GameStatus _status;
	private int _count = 0; // 佈子數, 當玩家把25個數字都佈完後 開始遊戲 
	private BingoLogic _logic;
	private GradeRecorder _recorder;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// keep screen always lights without going to sleeping mode
		getWindow().setFlags
		(
		WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );
		
		AppProperties.initial(this);
		_logic = new BingoLogic(this);
		_recorder = new GradeRecorder();
		
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		
		addGrid(_aiTable, false);
		addGrid(_playerTable, true);
		updateRecordView();
		
		TextView text = (TextView)findViewById(R.id.text_version);
		text.setText("V " + AppProperties.getAppVersion());

		restart();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();

		//showAnimation();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);

		Handler handler = new Handler();
		handler.postDelayed
		(
		new Runnable() 
		{
		  public void run() 
		  {
			  Toast.makeText
			  (
			  BingoActivity.this, 
			  R.string.FILL_NUMBER, 
			  Toast.LENGTH_LONG
			  ).show();
			  
			  TranslateAnimation	ani = new TranslateAnimation
										      (  
										      Animation.RELATIVE_TO_PARENT, 0,  
										      Animation.RELATIVE_TO_PARENT, 0,  
										      Animation.RELATIVE_TO_PARENT, 0,  
										      Animation.RELATIVE_TO_PARENT, 0
										      );
			  	ani.setDuration(300);  
				ani.setInterpolator(new AccelerateInterpolator());  
		      ani.setAnimationListener(new LastViewRemover(_playerTable));
		      
		      _playerTable.startAnimation(ani);
		  }
		},
		500
		);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		_logic = null;
		AppProperties.release();
	}
	
	@Override
	public boolean onKeyDown(int aKeyCode, KeyEvent aEvent)
	{
		if (aKeyCode == KeyEvent.KEYCODE_BACK)
		{
			AlertDialogCreator.showDialog
			(
			AlertDialogCreator.AlertTitleType.CONFIRM_ALERT,
			AlertDialogCreator.AlertButtonStyle.BUTTON_YES_NO,
			getString(R.string.CONFIRM_EXIT),
			this,
			new DialogInterface.OnClickListener() 
			{  
				public void onClick(DialogInterface dialog, int which) 
				{  
					if (which == DialogInterface.BUTTON_POSITIVE) // yes
					{
						BingoActivity.this.finish();
					}
					
					dialog.dismiss();
				}
			}
			);
			
			return true;
		}

		return super.onKeyDown(aKeyCode, aEvent);
	}
	
	public void onClick(View aView)
	{
		GridView grid = (GridView)aView;
		
		switch (_status)
		{
			case PREPARE:
			{
				if (grid.getValue() <= 0)
				{
					_count++;
					grid.setValue(_count);
	
					if (_count >= 25)
					{
						_logic.resetComputer();
						_status = GameStatus.PLAYING;
						
						Toast.makeText
					   (
						this, 
					   R.string.GAME_START, 
					   Toast.LENGTH_SHORT
					   ).show();
					}
				}
			}
				break;
				
			case PLAYING:
			{
				if (!grid.isSelected())
				{
					grid.setSelected(true);
					_logic.winCheck(grid.getLocX(), grid.getLocY());
				}
			}
				break;
				
			default:
				break;
		}
	}
	
	public void onLineConnected(PlayerType aType, int aCount)
	{
		if (aType == PlayerType.COMPUTER)
		{
			_aiCountView.setText(String.valueOf(aCount));
		}
		else // PLAYER
		{
			_playerCountView.setText(String.valueOf(aCount));
		}
	}
	
	public void onWon(PlayerType aWinner)
	{	
		int res = -1;
		
		_status = GameStatus.END;

		if (aWinner == PlayerType.COMPUTER)
		{
			_recorder.addLoseCount();
			res = R.string.YOU_LOSE;
		}
		else // PLAYER
		{
			_recorder.addWinCount();
			res = R.string.YOU_WIN;
		}
		
		updateRecordView();
		
		AlertDialogCreator.showDialog
		(
		AlertTitleType.NOTIFICATION_ALERT, 
		AlertButtonStyle.BUTTON_OK,
		getString(res),
		this,
		new DialogInterface.OnClickListener() 
		{  
			public void onClick(DialogInterface dialog, int which) 
			{  
				restart();
				showAnimation();
			}
		}
		);
	}
	
	private void updateRecordView()
	{
		StringBuilder builder = new StringBuilder();
		builder.append
		(
		AppUtility.getString
		(
		this, 
		R.string.WIN_COUNT, 
		String.valueOf(_recorder.getWinCount()), 
		String.valueOf(_recorder.getLostCount())
		)
		);
		
		_recordView.setText(builder.toString());
	}
	
	private void restart()
	{
		_status = GameStatus.PREPARE;
		_count = 0;
		_aiCountView.setText("");
		_playerCountView.setText("");
		_logic.restart();
	}
	
	private void addGrid(TableLayout aTable, boolean aClickable)
	{
		TableRow row = null;
		GridView grid = null;
		int width = CustomProperties.getScreenWidth(0.125f);
		int padding = (int)(1.8 * AppProperties.getDensity());
		
		for (int i = 0; i < 5; i++)
		{
			row = new TableRow(this);
			row.setGravity(Gravity.CENTER);
			aTable.addView(row);
			
			for (int j = 0; j < 5; j++)
			{
				grid = new GridView(this);
				grid.setId(0);
				grid.setGravity(Gravity.CENTER);
				grid.setLocX(i);
				grid.setLocY(j);
				grid.setTextSize
       		(
 		 		TypedValue.COMPLEX_UNIT_PX, 
 		 		CustomProperties.getDimensionPxSize(FontType.NORMAL_FONT_SIZE, this)
 		 		);
         	
				if (aClickable)
				{
					_logic.addGrid(PlayerType.PLAYER, grid, i, j);
					grid.setOnClickListener(this);
				}
				else
				{
					_logic.addGrid(PlayerType.COMPUTER, grid, i, j);
				}
				
				TableRow.LayoutParams para = new TableRow.LayoutParams(width, width);
				para.setMargins(padding, padding, padding, padding);
				
				row.addView(grid, para);
			}
		}
	}
	
	private void showAnimation()
	{
		setLayoutAnimation(_aiTable, 0, -1);
		setLayoutAnimation(_playerTable, 0, 1);
	}
	
	private void setLayoutAnimation
	(
	ViewGroup aGroup, 
	int aXValue, 
	int aYValue
	)
	{
		if (aGroup != null)
		{
			AnimationSet set = new AnimationSet(true);  
			
			Animation animation = new AlphaAnimation(0, 1);       
			animation.setDuration(500);       
			set.addAnimation(animation);    
			
			animation = new TranslateAnimation
			            (
			            Animation.RELATIVE_TO_SELF, 
			            aXValue, 
			            Animation.RELATIVE_TO_SELF, 
			            0,               
			            Animation.RELATIVE_TO_SELF,
			            aYValue, 
			            Animation.RELATIVE_TO_SELF, 
			            0
			            );      
			animation.setDuration(500);        
			set.addAnimation(animation);    
			
			LayoutAnimationController ctrl = new LayoutAnimationController(set, 0.25f);			
			aGroup.setLayoutAnimation(ctrl);
		}
	}
}
