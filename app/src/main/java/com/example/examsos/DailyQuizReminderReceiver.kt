package com.example.examsos

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class DailyQuizReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "dailyQuizChannel",
                "Daily Quiz Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Channel for daily quiz reminders"
            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val notification = NotificationCompat.Builder(context, "dailyQuizChannel")
            .setSmallIcon(R.raw.tree_healthy)
            .setContentTitle("ExamSOS: Daily Quiz Ready")
            .setContentText("The daily trivia quiz is ready to play!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Show the notification
        notificationManager.notify(1, notification)
    }
}
