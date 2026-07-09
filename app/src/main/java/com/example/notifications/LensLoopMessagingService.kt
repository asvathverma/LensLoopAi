package com.example.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

// Mock Firebase Messaging Service
class LensLoopMessagingService {

    fun onMessageReceived(remoteMessage: Map<String, String>) {
        // Handle incoming data message here.
        // E.g., if quiet hours are enabled in NotificationSettingsViewModel, do not show.
        
        // For rich notifications with action buttons:
        val title = remoteMessage["title"] ?: "LensLoop"
        val body = remoteMessage["body"] ?: "New notification"
        val type = remoteMessage["type"] ?: "message"

        sendNotification(title, body, type)
    }

    fun onNewToken(token: String) {
        // Send token to backend
    }

    private fun sendNotification(title: String, messageBody: String, type: String) {
        /*
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = "LensLoop_Channel_ID"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        // Action buttons
        val likeIntent = PendingIntent.getBroadcast(
            this, 1, Intent("ACTION_LIKE"), PendingIntent.FLAG_IMMUTABLE
        )
        val replyIntent = PendingIntent.getBroadcast(
            this, 2, Intent("ACTION_REPLY"), PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            
        if (type == "message") {
            notificationBuilder.addAction(
                android.R.drawable.ic_menu_edit, 
                "Reply", 
                replyIntent
            )
        } else if (type == "post") {
            notificationBuilder.addAction(
                android.R.drawable.ic_menu_revert, 
                "Like", 
                likeIntent
            )
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel for Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "LensLoop Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
        */
    }
}
