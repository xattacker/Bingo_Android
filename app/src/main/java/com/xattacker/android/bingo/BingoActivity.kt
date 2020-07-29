package com.xattacker.android.bingo

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnClickListener
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
    companion object
    {
        private val GRID_DIMENSION = 5
    }

    private lateinit var binding: ActivityMainBinding
    private var viewModel: BingoViewModel? = null

    public override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        AppProperties.initial(this)

        this.initViewModel()

        // use view Binding mode
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        setupGrid(binding.layoutAiGrid, PlayerType.COMPUTER)
        setupGrid(binding.layoutPlayerGrid, PlayerType.PLAYER)

        updateRecordView()

        setupCountView(binding.viewAiCount)
        setupCountView(binding.viewPlayerCount)

        binding.textVersion.text = "v " + (AppProperties.appVersion ?: "")
    }

    override fun onStart()
    {
        super.onStart()
        //showAnimation();
    }

    override fun onPostCreate(savedInstanceState: Bundle?)
    {
        super.onPostCreate(savedInstanceState)

        viewModel?.restart()

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

        viewModel = null
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
        viewModel?.handleGridClick(grid, grid.locX, grid.locY)
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
        updateRecordView()

        AlertDialogCreator.showDialog(
            AlertTitleType.Notification,
            getString(if (aWinner == PlayerType.COMPUTER) R.string.YOU_LOSE else R.string.YOU_WIN),
            this)
    }

    fun onAutoFillNumClick(view: View)
    {
        viewModel?.fillNumber(PlayerType.PLAYER)
        viewModel?.startPlaying()
    }

    fun onRestartClick(view: View)
    {
        viewModel?.restart()
    }

    private fun updateRecordView()
    {
        viewModel?.record?.let {
            binding.textRecord.text = getString(R.string.WIN_COUNT, it.winCount, it.loseCount)
        }
    }

    private fun initViewModel()
    {
        viewModel = BingoViewModel(this, GRID_DIMENSION)

        viewModel?.onStatusUpdated = {
            status: GameStatus ->

            updateButtonWithStatus(status)

            when (status)
            {
                GameStatus.PREPARE ->
                {
                    binding.viewAiCount.reset()
                    binding.viewPlayerCount.reset()
                }

                GameStatus.PLAYING ->
                    Toast.makeText(this, R.string.GAME_START, Toast.LENGTH_SHORT).show()

                GameStatus.END -> {}
            }
        }
    }

    private fun updateButtonWithStatus(status: GameStatus)
    {
        when (status)
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
        }
    }

    private fun setupGrid(aTable: TableLayout?, type: PlayerType)
    {
        var row: TableRow?
        var grid: GridView?
        val width = CustomProperties.getScreenWidth(0.125f)
        val padding = (1.8 * AppProperties.density).toInt()

        for (i in 0 .. GRID_DIMENSION - 1)
        {
            row = TableRow(this)
            row.gravity = Gravity.CENTER
            aTable?.addView(row)

            for (j in 0 .. GRID_DIMENSION - 1)
            {
                grid = GridView(this)
                grid.value = 0
                grid.locX = i
                grid.locY = j
                grid.type = type

                viewModel?.addGrid(type, grid, i, j)

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
