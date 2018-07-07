package org.premiumapp.antime

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*
import org.premiumapp.antime.utils.PrefsUtils

enum class TimerState {
    STOPPED, RUNNING, PAUSED
}

class TimerActivity : AppCompatActivity() {

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = 0L
    private var timerSecondsRemaining: Long = 0L
    private var timerState = TimerState.STOPPED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "    Timer"
        supportActionBar?.setIcon(R.drawable.ic_timer)
        initButtons()
    }

    override fun onResume() {
        super.onResume()

        initTimer()

        //TODO: remove background timer, hide notification
    }

    private fun initTimer() {

        timerState = PrefsUtils.getTimerState(this)

        timerSecondsRemaining = when (timerState) {
            TimerState.STOPPED -> {
                setNewTimerLength()
                timerLengthSeconds
            }
            else -> {
                setPreviousTimerLength()
                PrefsUtils.getTimerSecondsRemaining(this)
            }
        }

        if (timerState == TimerState.RUNNING) {
            startTimer()
        }

        updateButtons()
        updateCountdownUI()
    }

    private fun updateCountdownUI() {

        val minuteUntilFinish = timerSecondsRemaining / 60
        val secondsInMinuteLeft = timerSecondsRemaining - minuteUntilFinish * 60
        val secondsDivider = ":" + if (secondsInMinuteLeft < 10) "0" else ""
        val timerDisplay = "$minuteUntilFinish$secondsDivider$secondsInMinuteLeft"
        tv_countdown.text = timerDisplay
        progress_countdown.progress = (timerLengthSeconds - timerSecondsRemaining).toInt()

    }

    private fun setPreviousTimerLength() {

        timerLengthSeconds = PrefsUtils.getPreviousTimerLengthSeconds(this)
        progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun setNewTimerLength() {

        val lengthInMinutes = PrefsUtils.getTimerLength(this)
        timerLengthSeconds = lengthInMinutes * 60L
        progress_countdown.max = timerLengthSeconds.toInt()

    }

    override fun onPause() {
        super.onPause()

        when (timerState) {
            TimerState.RUNNING -> {
                timer.cancel()
                //TODO: start background timer, show notification
            }
            TimerState.PAUSED -> {
                //TODO: show notification
            }
            else -> {
            }
        }

        PrefsUtils.setPreviousTimerLengthSeconds(this, timerLengthSeconds)
        PrefsUtils.setSecondsRemaining(this, timerSecondsRemaining)
        PrefsUtils.setTimerState(this, timerState)
    }

    private fun initButtons() {
        fab_start.setOnClickListener { _ ->
            startTimer()
            timerState = TimerState.RUNNING
            updateButtons()
        }

        fab_pause.setOnClickListener { _ ->
            timer.cancel()
            timerState = TimerState.PAUSED
            updateButtons()
        }

        fab_stop.setOnClickListener {
            timer.cancel()
            onTimerFinished()
        }
    }

    private fun onTimerFinished() {

        timerState = TimerState.STOPPED
        setNewTimerLength()
        progress_countdown.progress = 0

        PrefsUtils.setPreviousTimerLengthSeconds(this, timerLengthSeconds)
        timerSecondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun updateButtons() {

        fab_stop.isEnabled = true
        fab_start.isEnabled = true
        fab_pause.isEnabled = true
        when (timerState) {
            TimerState.RUNNING -> fab_start.isEnabled = false
            TimerState.PAUSED -> fab_pause.isEnabled = false
            TimerState.STOPPED -> fab_stop.isEnabled = false
        }
    }

    private fun startTimer() {

        timerState = TimerState.RUNNING

        timer = object : CountDownTimer(timerSecondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(msUntilFinished: Long) {

                timerSecondsRemaining = msUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_timer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
