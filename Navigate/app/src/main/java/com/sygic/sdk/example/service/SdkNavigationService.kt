package com.sygic.sdk.example.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.sygic.sdk.example.R
import com.sygic.sdk.example.activity.SdkActivity

private const val SDK_FOREGROUND_SERVICE_ID = 12345
private const val SDK_NOTIFICATION_CHANNEL = "sdk_channel"

class SdkNavigationService : Service() {

    inner class SdkServiceBinder : Binder() {
        fun startForeground() {
            this@SdkNavigationService.startForeground()
        }

        fun stopForeground() {
            this@SdkNavigationService.stopForeground()
        }
    }

    private val binder = SdkServiceBinder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // mandatory call to satisfy Context.startForegroundService() behavior
        startForeground()
        stopForeground()
        return super.onStartCommand(intent, flags, startId)
    }

    fun startForeground() {
        startForeground(SDK_FOREGROUND_SERVICE_ID, buildNotification())
    }

    fun stopForeground() {
        stopForeground(true)
    }

    private fun buildNotification(): Notification {
        createNotificationChannel()
        return NotificationCompat.Builder(this, SDK_NOTIFICATION_CHANNEL)
            .setContentTitle(getString(R.string.sdk_foreground_service_title))
            .setContentText(getString(R.string.sdk_foreground_service_content_text))
            .setContentIntent(getPendingIntent())
            .setSmallIcon(R.drawable.ic_notification)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                SDK_NOTIFICATION_CHANNEL,
                getString(R.string.sdk_foreground_service_channel_name),
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val contentIntent = Intent(baseContext, SdkActivity::class.java)
        return PendingIntent.getActivity(baseContext, 0, contentIntent, 0)
    }
}