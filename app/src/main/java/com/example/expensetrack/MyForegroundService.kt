package com.example.expensetrack

import android.app.Notification.Builder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi

class MyForegroundService() : Service() {
    private lateinit var serviceChannel: NotificationChannel
    private lateinit var manager: NotificationManager


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        createNotification()
        super.onCreate()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var total = intent?.getDoubleExtra("totalSum", 9.0)!!
        Log.d("Services", "onStartCommand: Started Service, totalSum: $total")

        showNotification(total)
        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(total: Double) {
        var notification = Builder(this, "CHANNEL_ID")
            .setContentTitle("Total Expenses")
            .setContentText("$total$")
            .setSmallIcon(R.drawable.ic_baseline_account_balance_wallet_24)
            .setContentIntent(getMainActivityPendingIntent())
            .setAutoCancel(false)
            .setOngoing(true)
            .build()
        startForeground(200, notification)
    }

    private fun getMainActivityPendingIntent() =
        PendingIntent.getActivity(
            this,
            123,
            Intent(this, MainActivity::class.java).apply {
                this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serviceChannel = NotificationChannel(
                "CHANNEL_ID",
                "Expense Tracker",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }


}

