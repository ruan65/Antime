package org.premiumapp.antime

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*
import org.premiumapp.antime.broadcast.TimerExpiredReceiver
import org.premiumapp.antime.utils.NotificationUtils
import org.premiumapp.antime.utils.PrefsUtils
import java.util.*

enum class TimerState {
    STOPPED, RUNNING, PAUSED
}

class TimerActivity : AppCompatActivity() {

    companion object {

        fun setAlarm(ctx: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(ctx, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefsUtils.setAlarmSetTime(ctx, nowSeconds)
            return wakeUpTime
        }

        fun removeAlarm(ctx: Context) {
            val intent = Intent(ctx, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0)
            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefsUtils.setAlarmSetTime(ctx, 0)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }

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

        removeAlarm(this)
        NotificationUtils.hideTimerNotification(this)
    }

    private fun initTimer() {

        timerState = PrefsUtils.getTimerState(this)

        if (timerState == TimerState.STOPPED)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        timerSecondsRemaining = if (TimerState.RUNNING == timerState || TimerState.PAUSED == timerState)
            PrefsUtils.getTimerSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime = PrefsUtils.getAlarmTime(this)

        if (alarmSetTime > 0) {
            timerSecondsRemaining -= nowSeconds - alarmSetTime
        }

        if (timerSecondsRemaining <= 0) {
            onTimerFinished()
        } else if (timerState == TimerState.RUNNING) {
            startTimer()
        }

        updateButtons()
        updateCountdownUI()
    }

    private fun updateCountdownUI() {

        Log.d("mylog", "timerLengthSeconds: $timerLengthSeconds sec remaining: $timerSecondsRemaining")
        val minuteUntilFinish = timerSecondsRemaining / 60
        val secondsInMinuteLeft = timerSecondsRemaining - minuteUntilFinish * 60
        val secondsDivider = ":" + if (secondsInMinuteLeft < 10) "0" else ""
        val timerDisplay = "$minuteUntilFinish$secondsDivider$secondsInMinuteLeft"
        tv_countdown.text = timerDisplay
//        progress_countdown.max = timerLengthSeconds.toInt()
        val progress = (timerLengthSeconds - timerSecondsRemaining).toInt()
        progress_countdown.progress = progress

        Log.d("mylog", "progress: $progress")
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
                val wakeUpTime = setAlarm(this, nowSeconds, timerSecondsRemaining)
                NotificationUtils.showTimerRunning(this, wakeUpTime)
            }
            TimerState.PAUSED -> {
                NotificationUtils.showTimerPaused(this)
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
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
