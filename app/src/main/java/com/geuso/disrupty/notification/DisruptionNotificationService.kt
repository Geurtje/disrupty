package com.geuso.disrupty.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.geuso.disrupty.App
import com.geuso.disrupty.R
import com.geuso.disrupty.disruption.model.DisruptionCheck
import com.geuso.disrupty.ns.traveloption.DisruptionStatus
import com.geuso.disrupty.subscription.model.Status
import com.geuso.disrupty.subscription.model.Subscription


object DisruptionNotificationService {

    private val NOTIFICATION_MANAGER : NotificationManager = App.context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    private const val NOTIFICATION_CHANNEL_NAME = "Disruption notifications"
    private const val NOTIFICATION_CHANNEL_ID = "disruption_notification_channel"
    private const val DISRUPTION_NOTIFICATION_ID = 1


    fun sendNotification(subscription: Subscription, disruptionCheck: DisruptionCheck, newStatus: Status, disruptionStatusCause: DisruptionStatus) {

        val title: String
        val content: String

        if (newStatus == Status.NOT_OK || newStatus == Status.UNKNOWN) {
            title = App.context.resources.getString(R.string.notify_disruption_detected_title)
            content = App.context.resources.getString(R.string.notify_disruption_detected_content,
                    subscription.stationFrom, subscription.stationTo, disruptionStatusCause.key, disruptionCheck.message)
        }
        else {
            title = App.context.resources.getString(R.string.notify_disruption_resolved_title)
            content = App.context.resources.getString(R.string.notify_disruption_resolved_content,
                    subscription.stationFrom, subscription.stationTo)
        }

        notify(title, content)
    }

    private fun notify(title: String, content: String) {
        val notificationBuilder = NotificationCompat.Builder(App.context, NOTIFICATION_CHANNEL_ID)

        val notification = notificationBuilder
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle())
                .setContentTitle(title)
                .setContentText(content)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH)
            NOTIFICATION_MANAGER.createNotificationChannel(notificationChannel)
        }

        NOTIFICATION_MANAGER.notify(DISRUPTION_NOTIFICATION_ID, notification)
    }



}