package com.xattacker.android.bingo

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.*
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.xattacker.android.bingo.logic.BingoLogic
import com.xattacker.android.bingo.logic.BingoLogic.PlayerType
import com.xattacker.android.bingo.logic.BingoLogicListener
import com.xattacker.android.bingo.util.*


class BingoActivity : Activity(), OnClickListener, BingoLogicListener
{
    private var _aiTable: TableLayout? = null
    private var _playerTable: TableLayout? = null
    private var _aiCountView: TextView? = null
    private var _playerCountView: TextView? = null
    private var _recordView: TextView? = null

    private var _status: GameStatus? = null
    private var _count = 0 // 佈子數, 當玩家把25個數字都佈完後 開始遊戲
    private var _logic: BingoLogic? = null
    private var _recorder: GradeRecorder? = null

    private enum class GameStatus
    {
        PREPARE,
        PLAYING,
        END
    }

    public override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        AppProperties.initial(this)
        _logic = BingoLogic(this)
        _recorder = GradeRecorder()

        setContentView(R.layout.activity_main)

        _aiTable = findViewById(R.id.layout_ai_grid)
        _playerTable = findViewById(R.id.layout_player_grid)
        _aiCountView = findViewById(R.id.text_ai_count)
        _playerCountView = findViewById(R.id.text_player_count)
        _recordView = findViewById(R.id.text_record)

        addGrid(_aiTable, false)
        addGrid(_playerTable, true)
        updateRecordView()

        val text = findViewById<View>(R.id.text_version) as TextView
        text.text = "V " + (AppProperties.appVersion ?: "")

        restart()
    }

    override fun onStart()
    {
        super.onStart()
        //showAnimation();
    }

    override fun onPostCreate(savedInstanceState: Bundle?)
    {
        super.onPostCreate(savedInstanceState)

        val handler = Handler()
        handler.postDelayed({
            Toast.makeText(this@BingoActivity, R.string.FILL_NUMBER, Toast.LENGTH_LONG).show()

            val ani = TranslateAnimation(
                            Animation.RELATIVE_TO_PARENT,
                            0f,
                            Animation.RELATIVE_TO_PARENT,
                            0f,
                            Animation.RELATIVE_TO_PARENT,
                            0f,
                            Animation.RELATIVE_TO_PARENT,
                            0f)
            ani.duration = 300
            ani.interpolator = AccelerateInterpolator()
            ani.setAnimationListener(LastViewRemover(_playerTable))

            _playerTable?.startAnimation(ani)
        },
            500)
    }

    override fun onDestroy()
    {
        super.onDestroy()

        _logic = null
        AppProperties.release()
    }

    override fun onKeyDown(aKeyCode: Int, aEvent: KeyEvent): Boolean
    {
        if (aKeyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialogCreator.showDialog(
                AlertTitleType.CONFIRM_ALERT,
                AlertButtonStyle.BUTTON_YES_NO,
                getString(R.string.CONFIRM_EXIT),
                this) {
                dialog, which ->
                if (which == DialogInterface.BUTTON_POSITIVE)
                {
                    // yes
                    this@BingoActivity.finish()
                }

                dialog?.dismiss()
            }

            return true
        }

        return super.onKeyDown(aKeyCode, aEvent)
    }

    override fun onClick(aView: View)
    {
        val grid = aView as GridView

        when (_status)
        {
            GameStatus.PREPARE ->
            {
                if (grid.value <= 0)
                {
                    _count++
                    grid.value = _count

                    if (_count >= 25)
                    {
                        _logic?.resetComputer()
                        _status = GameStatus.PLAYING

                        Toast.makeText(this, R.string.GAME_START, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            GameStatus.PLAYING ->
            {
                if (!grid.isSelectedOn)
                {
                    grid.isSelectedOn = true
                    _logic?.winCheck(grid.locX, grid.locY)
                }
            }

            else ->
            {
            }
        }
    }

    override fun onLineConnected(aType: PlayerType, aCount: Int)
    {
        if (aType == PlayerType.COMPUTER)
        {
            _aiCountView?.text = aCount.toString()
        }
        else // PLAYER
        {
            _playerCountView?.text = aCount.toString()
        }
    }

    override fun onWon(aWinner: PlayerType?)
    {
        var res = -1

        _status = GameStatus.END

        if (aWinner == PlayerType.COMPUTER)
        {
            _recorder?.addLoseCount()
            res = R.string.YOU_LOSE
        }
        else // PLAYER
        {
            _recorder?.addWinCount()
            res = R.string.YOU_WIN
        }

        updateRecordView()

        AlertDialogCreator.showDialog(
            AlertTitleType.NOTIFICATION_ALERT,
            AlertButtonStyle.BUTTON_OK,
            getString(res),
            this) {
            dialog, which ->
            restart()
            showAnimation()
        }
    }

    private fun updateRecordView()
    {
        val builder = StringBuilder()
        builder.append(AppUtility.getString(this, R.string.WIN_COUNT, _recorder?.winCount.toString(), _recorder?.lostCount.toString()))

        _recordView?.text = builder.toString()
    }

    private fun restart()
    {
        _status = GameStatus.PREPARE
        _count = 0
        _aiCountView?.text = ""
        _playerCountView?.text = ""
        _logic?.restart()
    }

    private fun addGrid(aTable: TableLayout?, aClickable: Boolean)
    {
        var row: TableRow? = null
        var grid: GridView? = null
        val width = CustomProperties.getScreenWidth(0.125f)
        val padding = (1.8 * AppProperties.density).toInt()

        for (i in 0 .. 4)
        {
            row = TableRow(this)
            row.gravity = Gravity.CENTER
            aTable?.addView(row)

            for (j in 0 .. 4)
            {
                grid = GridView(this)
                grid.id = 0
                grid.gravity = Gravity.CENTER
                grid.locX = i
                grid.locY = j
                grid.setTextSize(TypedValue.COMPLEX_UNIT_PX, CustomProperties.getDimensionPxSize(FontType.NORMAL_FONT_SIZE, this).toFloat())

                if (aClickable)
                {
                    _logic?.addGrid(PlayerType.PLAYER, grid, i, j)
                    grid.setOnClickListener(this)
                }
                else
                {
                    _logic?.addGrid(PlayerType.COMPUTER, grid, i, j)
                }

                val para = TableRow.LayoutParams(width, width)
                para.setMargins(padding, padding, padding, padding)
                row.addView(grid, para)
            }
        }
    }

    private fun showAnimation()
    {
        setLayoutAnimation(_aiTable, 0, -1)
        setLayoutAnimation(_playerTable, 0, 1)
    }

    private fun setLayoutAnimation(aGroup: ViewGroup?, aXValue: Int, aYValue: Int)
    {
        if (aGroup != null)
        {
            val set = AnimationSet(true)
            var animation: Animation = AlphaAnimation(0f, 1f)
            animation.duration = 500
            set.addAnimation(animation)

            animation = TranslateAnimation(Animation.RELATIVE_TO_SELF,
                                aXValue.toFloat(),
                                Animation.RELATIVE_TO_SELF,
                                0f,
                                Animation.RELATIVE_TO_SELF,
                                aYValue.toFloat(),
                                Animation.RELATIVE_TO_SELF,
                                0f)
            animation.setDuration(500)
            set.addAnimation(animation)

            val ctrl = LayoutAnimationController(set, 0.25f)
            aGroup.layoutAnimation = ctrl
        }
    }
}
