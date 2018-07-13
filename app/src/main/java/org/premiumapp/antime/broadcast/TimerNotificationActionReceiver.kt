package org.premiumapp.antime.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.premiumapp.antime.TimerActivity
import org.premiumapp.antime.TimerState
import org.premiumapp.antime.utils.AppConstants
import org.premiumapp.antime.utils.NotificationUtils
import org.premiumapp.antime.utils.PrefsUtils

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        when (intent.action) {
            AppConstants.ACTION_STOP -> {
                TimerActivity.removeAlarm(context)
                PrefsUtils.setTimerState(context, TimerState.STOPPED)
                NotificationUtils.hideTimerNotification(context)
            }
            AppConstants.ACTION_PAUSE -> {
                var secondsRemaining = PrefsUtils.getTimerSecondsRemaining(context)
                val alarmSetTime = PrefsUtils.getAlarmTime(context)
                val nowSeconds = TimerActivity.nowSeconds

                secondsRemaining -= nowSeconds - alarmSetTime
                PrefsUtils.setSecondsRemaining(context, secondsRemaining)

                TimerActivity.removeAlarm(context)
                PrefsUtils.setTimerState(context, TimerState.PAUSED)
                NotificationUtils.showTimerPaused(context)
            }
            AppConstants.ACTION_RESUME -> {
                val secondsRemaining = PrefsUtils.getTimerSecondsRemaining(context)
                val wakeUpTime = TimerActivity.setAlarm(context, TimerActivity.nowSeconds, secondsRemaining)

                PrefsUtils.setTimerState(context, TimerState.RUNNING)
                NotificationUtils.showTimerRunning(context, wakeUpTime)
            }
            AppConstants.ACTION_START -> {

                val minutesRemaining = PrefsUtils.getTimerLength(context)
                val secondsRemaining = minutesRemaining * 60L
                val wakeUpTime = TimerActivity.setAlarm(context, TimerActivity.nowSeconds, secondsRemaining)

                PrefsUtils.setTimerState(context, TimerState.RUNNING)
                PrefsUtils.setSecondsRemaining(context, secondsRemaining)
                NotificationUtils.showTimerRunning(context, wakeUpTime)
            }
        }
    }
}
