package com.xattacker.android.bingo

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.animation.*
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.Toast
import com.xattacker.android.bingo.databinding.ActivityMainBinding
import com.xattacker.android.bingo.logic.BingoLogic
import com.xattacker.android.bingo.logic.PlayerType
import com.xattacker.android.bingo.logic.BingoLogicListener
import com.xattacker.android.bingo.util.*
import com.xattacker.android.bingo.view.CountView
import com.xattacker.android.bingo.view.GridView
import com.xattacker.android.bingo.view.BlinkViewAnimator

class BingoActivity : Activity(), OnClickListener, BingoLogicListener
{
    private lateinit var binding: ActivityMainBinding

    private var _status: GameStatus? = null
    private var _numDoneCount = 0 // 佈子數, 當玩家把25個數字都佈完後 開始遊戲
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

        // use view Binding mode
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        setupGrid(binding.layoutAiGrid, PlayerType.COMPUTER)
        setupGrid(binding.layoutPlayerGrid, PlayerType.PLAYER)

        updateRecordView()

        setupCountView(binding.viewAiCount)
        setupCountView(binding.viewPlayerCount)

        binding.textVersion.text = "v " + (AppProperties.appVersion ?: "")

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
            showHintAnimation()
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
                AlertTitleType.Confirm,
                AlertButtonStyle.YesNo,
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
                    _numDoneCount++
                    grid.value = _numDoneCount

                    if (_numDoneCount >= 25)
                    {
                        startPlaying()
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
            
            GameStatus.END ->
            {
                restart()
            }
        }
    }

    override fun onLineConnected(aTurn: PlayerType, aCount: Int)
    {
        if (aTurn == PlayerType.COMPUTER)
        {
            binding.viewAiCount.count = aCount
        }
        else // PLAYER
        {
            binding.viewPlayerCount.count = aCount
        }
    }

    override fun onWon(aWinner: PlayerType)
    {
        var res = -1

        _status = GameStatus.END
        updateButtonWithStatus()

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
            AlertTitleType.Notification,
            getString(res),
            this)
    }

    fun onAutoFillNumClick(view: View)
    {
        _logic?.fillNumber(PlayerType.PLAYER)
        startPlaying()
    }

    fun onRestartClick(view: View)
    {
        restart()
    }

    private fun updateRecordView()
    {
        binding.textRecord.text = AppUtility.getString(this, R.string.WIN_COUNT, _recorder?.winCount.toString(), _recorder?.lostCount.toString())
    }

    private fun restart()
    {
        _status = GameStatus.PREPARE
        _numDoneCount = 0
        updateButtonWithStatus()

        binding.viewAiCount.reset()
        binding.viewPlayerCount.reset()

        _logic?.restart()
    }

    private fun startPlaying()
    {
        _logic?.fillNumber()
        _status = GameStatus.PLAYING
        updateButtonWithStatus()

        Toast.makeText(this, R.string.GAME_START, Toast.LENGTH_SHORT).show()
    }

    private fun updateButtonWithStatus()
    {
        when (_status)
        {
            GameStatus.PREPARE ->
             {
                 binding.buttonAutoFill.visibility = View.VISIBLE
                 binding.buttonRestart.visibility = View.GONE
             }

            GameStatus.PLAYING ->
            {
                binding.buttonAutoFill.visibility = View.INVISIBLE
                binding.buttonRestart.visibility = View.INVISIBLE
            }

            GameStatus.END ->
            {
                binding.buttonAutoFill.visibility = View.GONE
                binding.buttonRestart.visibility = View.VISIBLE
            }

            else ->
            {
                binding.buttonAutoFill.visibility = View.INVISIBLE
                binding.buttonRestart.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupGrid(aTable: TableLayout?, type: PlayerType)
    {
        var row: TableRow?
        var grid: GridView?
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
                grid.value = 0
                grid.locX = i
                grid.locY = j
                grid.type = type

                _logic?.addGrid(type, grid, i, j)

                if (type == PlayerType.PLAYER)
                {
                    grid.setOnClickListener(this)
                }

                val para = TableRow.LayoutParams(width, width)
                para.setMargins(padding, padding, padding, padding)
                row.addView(grid, para)
            }
        }
    }

    private fun setupCountView(countView: CountView)
    {
        countView.layoutParams.height = CustomProperties.getScreenWidth(0.1f)
        countView.layoutParams.width = CustomProperties.getScreenWidth(0.1f)
    }

    private fun showHintAnimation()
    {
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
        ani.setAnimationListener(BlinkViewAnimator(binding.layoutPlayerGrid))

        binding.layoutPlayerGrid.startAnimation(ani)
    }
}
