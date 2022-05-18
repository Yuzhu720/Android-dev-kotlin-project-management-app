package com.southampton.comp6239.base

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat


class MyNotification {

    companion object {
        fun putNotification(
            nManager: NotificationManager,
            act: Activity,
            mtitle: String,
            mtext: String
        ) {
            //val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var notification: Notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mChannel =
                    NotificationChannel("id", "name", NotificationManager.IMPORTANCE_HIGH)
                nManager.createNotificationChannel(mChannel)
                notification = Notification.Builder(act)
                    .setChannelId("id")
                    .setContentTitle(mtitle)
                    .setContentText(mtext)
                    .setAutoCancel(true)
                    .setSmallIcon(android.R.drawable.ic_media_play).build()
            } else {
                val builder = NotificationCompat.Builder(act)
                    .setContentTitle(mtitle)
                    .setContentText(mtext)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSmallIcon(android.R.drawable.ic_media_play)
                notification = builder.build()
            }
            nManager.notify(0, notification)
        }
    }

}
