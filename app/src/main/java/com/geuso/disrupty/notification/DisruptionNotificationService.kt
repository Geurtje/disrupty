package com.geuso.disrupty.notification

import android.app.*
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.geuso.disrupty.R
import com.geuso.disrupty.disruption.log.DisruptionCheckDetailActivity
import com.geuso.disrupty.disruption.model.DisruptionCheck
import com.geuso.disrupty.ns.traveloption.DisruptionStatus
import com.geuso.disrupty.subscription.model.Status
import com.geuso.disrupty.subscription.model.Subscription


object DisruptionNotificationService {

    private const val NOTIFICATION_CHANNEL_NAME = "Disruption notifications"
    private const val NOTIFICATION_CHANNEL_ID = "disruption_notification_channel"
    private const val DISRUPTION_NOTIFICATION_ID = 1


    fun sendNotification(context: Context,
                         subscription: Subscription,
                         disruptionCheck: DisruptionCheck,
                         newStatus: Status,
                         disruptionStatusCause: DisruptionStatus) {

        val title: String
        val content: String

        if (newStatus == Status.NOT_OK || newStatus == Status.UNKNOWN) {
            title = context.resources.getString(R.string.notify_disruption_detected_title)
            content = context.resources.getString(R.string.notify_disruption_detected_content,
                    subscription.stationFrom, subscription.stationTo, disruptionStatusCause.key, disruptionCheck.message)
        }
        else {
            title = context.resources.getString(R.string.notify_disruption_resolved_title)
            content = context.resources.getString(R.string.notify_disruption_resolved_content,
                    subscription.stationFrom, subscription.stationTo)
        }

        notify(context, title, content, disruptionCheck)
    }

    private fun notify(context: Context,
                       title: String,
                       content: String,
                       disruptionCheck: DisruptionCheck) {

        val notificationBuilder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // Notification with back stack
        // https://developer.android.com/training/notify-user/navigation#build_a_pendingintent_with_a_back_stack
        val targetIntent = DisruptionCheckDetailActivity.intent(context, disruptionCheck)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(targetIntent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }


        val notification = notificationBuilder
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(NotificationCompat.BigTextStyle())
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(DISRUPTION_NOTIFICATION_ID, notification)
    }



}