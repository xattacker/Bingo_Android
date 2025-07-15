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
import com.xattacker.android.bingo.view.*
import io.reactivex.disposables.CompositeDisposable

class BingoActivity : Activity(), FlippableViewListener
{
    companion object
    {
        private val GRID_DIMENSION = 5
    }

    private lateinit var binding: ActivityMainBinding
    private var viewModel: BingoViewModel? = null
    private val disposableBag = CompositeDisposable()

    public override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        // use view Binding mode
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        AppProperties.initial(this)
        this.initViewModel()

        setupGrid(binding.layoutAiGrid, PlayerType.COMPUTER)
        setupGrid(binding.layoutPlayerGrid, PlayerType.PLAYER)

        setupCountView(binding.viewAiCount, binding.viewPlayerCount)

        binding.textVersion.text = "v " + (AppProperties.appVersion)
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

        this.delay(500) {
            Toast.makeText(this@BingoActivity, R.string.FILL_NUMBER, Toast.LENGTH_LONG).show()
            showHintAnimation()
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()

        viewModel = null

        this.disposableBag.clear()
        this.disposableBag.dispose()

        AppProperties.release()
    }

    override fun onKeyDown(aKeyCode: Int, aEvent: KeyEvent): Boolean
    {
        if (aKeyCode == KeyEvent.KEYCODE_BACK)
        {
            showConfirmDialog(
                getString(R.string.CONFIRM_EXIT)) {
                which ->
                if (which == DialogInterface.BUTTON_POSITIVE)
                {
                    // yes
                    this@BingoActivity.finish()
                }
            }

            return true
        }

        return super.onKeyDown(aKeyCode, aEvent)
    }

    override fun onFlipStarted(flipped: FlippableView)
    {
        this.binding.layoutPlayerGrid.isEnabled = false
    }

    override fun onFlipEnded(flipped: FlippableView)
    {
        this.binding.layoutPlayerGrid.isEnabled = true
    }

    fun onAutoFillNumClick(view: View)
    {
        viewModel?.fillNumber()
    }

    fun onRestartClick(view: View)
    {
        viewModel?.restart()
    }

    private fun initViewModel()
    {
        viewModel = BingoViewModel(GRID_DIMENSION)

        // data binding
        var disposable = viewModel?.gradeRecord?.subscribe {
            grade: GradeRecord ->

            binding.textRecord.text = getString(R.string.WIN_COUNT, grade.winCount, grade.loseCount)
        }
        this.disposableBag.add(disposable)

        disposable = viewModel?.status?.subscribe {
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
        this.disposableBag.add(disposable)

        disposable = viewModel?.lineConnected?.subscribe {
            connected: Pair<PlayerType, Int> ->
            if (connected.first == PlayerType.COMPUTER)
            {
                binding.viewAiCount.count = connected.second
            }
            else // PLAYER
            {
                binding.viewPlayerCount.count = connected.second
            }
        }
        this.disposableBag.add(disposable)


        disposable = viewModel?.onWon?.subscribe {
            winner: PlayerType ->
            showDialog(
                AlertTitleType.Notification,
                getString(if (winner == PlayerType.COMPUTER) R.string.YOU_LOSE else R.string.YOU_WIN))
        }
        this.disposableBag.add(disposable)
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

    private fun setupGrid(container: TableLayout, type: PlayerType)
    {
        var row: TableRow?
        var grid: View?
        val width = CustomProperties.getScreenWidth(0.125f)
        val padding = (1.8 * AppProperties.density).toInt()

        container.clipChildren = false

        for (i in 0 .. GRID_DIMENSION - 1)
        {
            row = TableRow(this)
            row.gravity = Gravity.CENTER
            row.clipChildren = false
            container.addView(row)

            for (j in 0 .. GRID_DIMENSION - 1)
            {
                if (type == PlayerType.PLAYER)
                {
                    grid = GridView(this)
                }
                else // COMPUTER
                {
                    grid = FlippableCardView(this)
                    grid.clipChildren = false
                    grid.listener = this
                }

                if (grid is BingoGridView)
                {
                    grid.value = 0
                    grid.locX = i
                    grid.locY = j
                    grid.type = type
                    viewModel?.addGrid(grid)
                }

                val para = TableRow.LayoutParams(width, width)
                para.setMargins(padding, padding, padding, padding)
                row.addView(grid, para)
            }
        }
    }

    private fun setupCountView(vararg countViews: View)
    {
        for (view in countViews)
        {
            view.layoutParams.height = CustomProperties.getScreenWidth(0.1f)
            view.layoutParams.width = CustomProperties.getScreenWidth(0.1f)
        }
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
