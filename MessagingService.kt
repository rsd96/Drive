package com.rsd96.drive

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage





/**
 * Created by Ramshad on 11/29/17.
 */
class MessagingService : FirebaseMessagingService() {

    companion object {
        private var TAG = "MessagingService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        if (!remoteMessage?.data?.isEmpty()!!) {
            var payload: Map<String, String> = remoteMessage.data

            Log.d(TAG, "Message: " + payload.get("message"))
            Log.d(TAG, "Vehicle: " + payload.get("vehicle"))
            showNotification(payload)
        }
    }

    private fun showNotification(payload: Map<String, String>) {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // The id of the channel.
        val id = "my_channel_01"
        // The user-visible name of the channel.
        val name = getString(R.string.channel_alert_name)
        // The user-visible description of the channel.
        val description = getString(R.string.channel_alert_desc)
        val importance = NotificationManager.IMPORTANCE_HIGH

        var mChannel: NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //######################################
            // Create notification channel for Alert
            //######################################
            mChannel = NotificationChannel(id, name, importance)
            // Configure the notification channel.
            mChannel.description = description
            mChannel.enableLights(true)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.lightColor = Color.GREEN
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mNotificationManager.createNotificationChannel(mChannel)
        }

        val contentIntent = PendingIntent.getActivity(this, 0,
                Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)


        //#####################################
        // Create notification
        //#####################################
        var notificationIcon = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.alarm)
        var notificationBuilder = NotificationCompat.Builder(this, getString(R.string.channel_alert_id))
                .setContentTitle("Alert on ${payload["vehicle"]} !")
                .setContentText("${payload["message"]}")
                .setSmallIcon(R.drawable.alarm)
                .setColor(ContextCompat.getColor(applicationContext, R.color.colorBackground))
                .setAutoCancel(true)
                .setContentIntent(contentIntent)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notificationBuilder.setChannelId(id)

        var notificationID = 101
        var notificationManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationID, notificationBuilder.build())
    }
}