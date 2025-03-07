package com.amitesh.defaulttvapplication

import android.app.ActivityOptions
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

class BootService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.d("BootService", "Foreground Service Started")

        startForeground(1, createNotification())

        startMainActivity()
//        Handler(Looper.getMainLooper()).postDelayed({
//            startMainActivity()
//        }, 5000) // 5 seconds delay
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val activityOptions = ActivityOptions.makeTaskLaunchBehind()
            startActivity(intent, activityOptions.toBundle())
        } else {
            startActivity(intent)
        }

        Log.d("BootService", "Starting MainActivity")

        // Stop service after launching the activity
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val channelId = "boot_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Boot Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Launching TV App")
            .setContentText("Starting News Channel App...")
            .build()
    }
}
