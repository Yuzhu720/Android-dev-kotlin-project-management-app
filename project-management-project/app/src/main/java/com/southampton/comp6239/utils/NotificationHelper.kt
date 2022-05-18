package com.southampton.comp6239.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_MAIN
import android.content.Intent.CATEGORY_LAUNCHER
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.southampton.comp6239.MainActivity
import com.southampton.comp6239.ProjectActivity
import com.southampton.comp6239.R

class NotificationHelper(val context: Context, val NotificationId: Int) {
    private val CHANNEL_ID = "Notification"
    private val NOTIFICATION_ID = NotificationId

    fun createNotification(title: String, message: String) {
        createNotificationChannel()
        val intent = Intent(context, MainActivity::class.java).apply{
            setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            setAction(ACTION_MAIN)
            addCategory(CATEGORY_LAUNCHER)
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val icon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_chat_48px)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_chat_48px)
            .setLargeIcon(icon)
            .setContentTitle(title)
            .setContentText("You have a new message!")
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(message)
            )
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Here is description"
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}