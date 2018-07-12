package org.premiumapp.antime.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.premiumapp.antime.TimerState
import org.premiumapp.antime.utils.PrefsUtils

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // TODO show notification

        PrefsUtils.setTimerState(context, TimerState.STOPPED)

        PrefsUtils.setAlarmSetTime(context, 0)
    }
}
