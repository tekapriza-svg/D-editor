package com.deditor.export

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder

class ExportService : Service() {

    override fun onCreate() {
        super.onCreate()
        createChannel()

        val notification = createNotification("Preparing export...")

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                5001,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROCESSING
            )
        } else {
            startForeground(5001, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(text: String): Notification {
        return if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder(this, "deditor_export")
                .setSmallIcon(android.R.drawable.ic_menu_upload)
                .setContentTitle("D Editor Export")
                .setContentText(text)
                .setOngoing(true)
                .build()
        } else {
            Notification.Builder(this)
                .setSmallIcon(android.R.drawable.ic_menu_upload)
                .setContentTitle("D Editor Export")
                .setContentText(text)
                .setOngoing(true)
                .build()
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                "deditor_export",
                "D Editor Export",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }
}
