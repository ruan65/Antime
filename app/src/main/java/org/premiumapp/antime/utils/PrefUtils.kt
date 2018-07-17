package org.premiumapp.antime.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.premiumapp.antime.TimerState

class PrefsUtils {

    companion object {

        private const val TIMER_LENGTH_ID = "org.premiumapp.antime.timer_length"
        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "PREVIOUS_TIMER_LENGTH_SECONDS_ID"
        private const val TIMER_STATE_ID = "TIMER_STATE_ID"
        private const val SECONDS_REMAINING_ID = "SECONDS_REMAINING_ID"
        private const val ALARM_SET_TIME_ID = "ALARM_SET_TIME_ID"

        fun getTimerLength(ctx: Context): Int {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getInt(TIMER_LENGTH_ID, 10)
        }

        fun getPreviousTimerLengthSeconds(ctx: Context): Long {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(ctx: Context, sec: Long) {
            getDefaultPrefsEditor(ctx)
                    .putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, sec)
                    .apply()
        }

        fun getTimerState(ctx: Context): TimerState {
            return TimerState.values()[
                    PreferenceManager.getDefaultSharedPreferences(ctx).getInt(TIMER_STATE_ID, 0)
            ]
        }

        fun setTimerState(ctx: Context, state: TimerState) {
            getDefaultPrefsEditor(ctx)
                    .putInt(TIMER_STATE_ID, state.ordinal)
                    .apply()
        }


        fun getTimerSecondsRemaining(ctx: Context): Long {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(ctx: Context, sec: Long) {
            getDefaultPrefsEditor(ctx)
                    .putLong(SECONDS_REMAINING_ID, sec)
                    .apply()
        }

        private fun getDefaultPrefsEditor(ctx: Context): SharedPreferences.Editor {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
                    .edit()
        }

        fun getAlarmTime(ctx: Context): Long {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getLong(ALARM_SET_TIME_ID, 0)
        }

        fun setAlarmSetTime(ctx: Context, time: Long) {
            getDefaultPrefsEditor(ctx).putLong(ALARM_SET_TIME_ID, time).apply()
        }
    }
}