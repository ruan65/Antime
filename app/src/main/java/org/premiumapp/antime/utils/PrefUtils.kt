package org.premiumapp.antime.utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.premiumapp.antime.TimerState

class PrefsUtils {

    companion object {
        fun getTimerLength(ctx: Context): Long {

            return 1
        }

        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "PREVIOUS_TIMER_LENGTH_SECONDS_ID"

        fun getPreviousTimerLengthSeconds(ctx: Context): Long {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)
        }

        fun setPreviousTimerLengthSeconds(ctx: Context, sec: Long) {
            getDefaultPrefsEditor(ctx)
                    .putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, sec)
                    .apply()
        }

        private const val TIMER_STATE_ID = "TIMER_STATE_ID"

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

        private const val SECONDS_REMAINING_ID = "PREVIOUS_TIMER_LENGTH_SECONDS_ID"

        fun getTimerSecondsRemaining(ctx: Context): Long {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getLong(SECONDS_REMAINING_ID, 0)
        }

        fun setSecondsRemaining(ctx: Context, sec: Long) {
            getDefaultPrefsEditor(ctx)
                    .putLong(SECONDS_REMAINING_ID, sec)
                    .apply()
        }

        fun getDefaultPrefsEditor(ctx: Context): SharedPreferences.Editor {
            return PreferenceManager.getDefaultSharedPreferences(ctx)
                    .edit()
        }
    }
}