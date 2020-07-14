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
import android.widget.Toast
import com.xattacker.android.bingo.databinding.ActivityMainBinding
import com.xattacker.android.bingo.logic.BingoLogic
import com.xattacker.android.bingo.logic.PlayerType
import com.xattacker.android.bingo.logic.BingoLogicListener
import com.xattacker.android.bingo.util.*
import com.xattacker.android.bingo.view.CountView
import com.xattacker.android.bingo.view.GridView
import com.xattacker.android.bingo.view.LastViewRemover

class BingoActivity : Activity(), OnClickListener, BingoLogicListener
{
    private lateinit var binding: ActivityMainBinding

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

        // use view Binding mode
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        setupGrid(binding.layoutAiGrid, false)
        setupGrid(binding.layoutPlayerGrid, true)
        updateRecordView()

        binding.textVersion.text = "v " + (AppProperties.appVersion ?: "")

        setupCountView(binding.viewAiCount)
        setupCountView(binding.viewPlayerCount)

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
            ani.setAnimationListener(LastViewRemover(binding.layoutPlayerGrid))

            binding.layoutPlayerGrid.startAnimation(ani)
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
                    _count++
                    grid.value = _count

                    if (_count >= 25)
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

            else ->
            {
            }
        }
    }

    override fun onLineConnected(aType: PlayerType, aCount: Int)
    {
        if (aType == PlayerType.COMPUTER)
        {
            binding.viewAiCount.setCount(aCount)
        }
        else // PLAYER
        {
            binding.viewPlayerCount.setCount(aCount)
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
        showAnimation()
    }

    private fun updateRecordView()
    {
        val builder = StringBuilder()
        builder.append(AppUtility.getString(this, R.string.WIN_COUNT, _recorder?.winCount.toString(), _recorder?.lostCount.toString()))

        binding.textRecord.text = builder.toString()
    }

    private fun restart()
    {
        _status = GameStatus.PREPARE
        _count = 0
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

    private fun setupGrid(aTable: TableLayout?, aClickable: Boolean)
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

    private fun setupCountView(countView: CountView)
    {
        countView.layoutParams.height =  CustomProperties.getScreenWidth(0.1f)
        countView.layoutParams.width = CustomProperties.getScreenWidth(0.1f)
    }

    private fun showAnimation()
    {
        setLayoutAnimation(binding.layoutAiGrid, 0, -1)
        setLayoutAnimation(binding.layoutPlayerGrid, 0, 1)
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
