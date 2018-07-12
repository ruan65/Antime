package org.premiumapp.antime.utils

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import org.premiumapp.antime.R
import org.premiumapp.antime.TimerActivity
import org.premiumapp.antime.broadcast.TimerNotificationActionReceiver
import java.text.SimpleDateFormat
import java.util.*

class NotificationUtils {
    companion object {
        private const val CHANNEL_ID_TIMER = "CHANNEL_ID_TIMER"
        private const val CHANNEL_NAME_TIMER = "CHANNEL_NAME_TIMER"
        private const val TIMER_ID = 0xBABA

        fun showTimerExpired(ctx: Context) {
            val startIntent = Intent(ctx, TimerNotificationActionReceiver::class.java)

            startIntent.action = AppConstants.ACTION_START

            val startPendingIntent = PendingIntent.getBroadcast(ctx, 0, startIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

            val notificationBuilder = getBasicNotificationBuilder(ctx, CHANNEL_ID_TIMER, true)

            notificationBuilder.setContentTitle("Timer Expired")
                    .setContentText("Start again?")
                    .setContentIntent(getPendingIntentWithStack(ctx, TimerActivity::class.java))
                    .addAction(R.drawable.ic_play, "Start", startPendingIntent)

            val nManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)

            nManager.notify(TIMER_ID, notificationBuilder.build())
        }

        fun showTimerRunning(ctx: Context, wakeUpTime: Long) {

            val stopIntent = Intent(ctx, TimerNotificationActionReceiver::class.java)
            stopIntent.action = AppConstants.ACTION_STOP
            val stopPendingIntent = PendingIntent.getBroadcast(ctx, 0, stopIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

            val pauseIntent = Intent(ctx, TimerNotificationActionReceiver::class.java)
            stopIntent.action = AppConstants.ACTION_PAUSE
            val pausePendingIntent = PendingIntent.getBroadcast(ctx, 0, stopIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

            val df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

            val notificationBuilder = getBasicNotificationBuilder(ctx, CHANNEL_ID_TIMER, true)

            notificationBuilder.setContentTitle("Timer is Running")
                    .setContentText("End: ${df.format(Date(wakeUpTime))}")
                    .setContentIntent(getPendingIntentWithStack(ctx, TimerActivity::class.java))
                    .setOngoing(true)
                    .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
                    .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)

            val nManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)

            nManager.notify(TIMER_ID, notificationBuilder.build())
        }

        fun showTimerPaused(ctx: Context) {
            val resumeIntent = Intent(ctx, TimerNotificationActionReceiver::class.java)
            resumeIntent.action = AppConstants.ACTION_RESUME
            val resumePendingIntent = PendingIntent.getBroadcast(ctx, 0, resumeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

            val notificationBuilder = getBasicNotificationBuilder(ctx, CHANNEL_ID_TIMER, true)

            notificationBuilder.setContentTitle("Timer is Paused")
                    .setContentText("Resume?")
                    .setContentIntent(getPendingIntentWithStack(ctx, TimerActivity::class.java))
                    .setOngoing(true)
                    .addAction(R.drawable.ic_play, "Resume", resumePendingIntent)

            val nManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nManager.createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)

            nManager.notify(TIMER_ID, notificationBuilder.build())
        }

        private fun getBasicNotificationBuilder(ctx: Context, channelId: String,
                                                playSound: Boolean): NotificationCompat.Builder {

            val notifSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val nBuilder = NotificationCompat.Builder(ctx, channelId)
                    .setSmallIcon(R.drawable.ic_timer)
                    .setAutoCancel(true)
                    .setDefaults(0)

            if (playSound) nBuilder.setSound(notifSound)
            return nBuilder
        }

        private fun <T> getPendingIntentWithStack(ctx: Context, jClass: Class<T>): PendingIntent {
            val resultIntent = Intent(ctx, jClass)
            resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(ctx)
            stackBuilder.addParentStack(jClass)
            stackBuilder.addNextIntent(resultIntent)

            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        @TargetApi(24)
        private fun NotificationManager.createNotificationChannel(channelId: String,
                                                                  channelName: String,
                                                                  playSound: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
                else NotificationManager.IMPORTANCE_LOW

                val nChannel = NotificationChannel(channelId, channelName, channelImportance)
                nChannel.enableLights(true)
                nChannel.lightColor = Color.BLUE
                this.createNotificationChannel(nChannel)
            }
        }
    }
}