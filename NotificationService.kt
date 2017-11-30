package com.rsd96.drive

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


/**
 * Created by Ramshad on 11/29/17.
 */
class NotificationService: Service() {


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        var dbReference = FirebaseDatabase.getInstance().reference
        var user = FirebaseAuth.getInstance().currentUser
        dbReference.child("users").child(user?.uid).child("cars").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot?) {
                Log.d(TAG, "service data changed")
                postNotification()
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun postNotification() {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // The id of the channel.
        val id = "my_channel_01"
        // The user-visible name of the channel.
        val name = getString(R.string.channel_alert_name)
        // The user-visible description of the channel.
        val description = getString(R.string.channel_alert_desc)
        val importance = NotificationManager.IMPORTANCE_HIGH

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //######################################
            // Create notification channel for Alert
            //######################################
            var mChannel = NotificationChannel(id, name, importance)
            // Configure the notification channel.
            mChannel.description = description
            mChannel.enableLights(true)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.lightColor = Color.GREEN
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mNotificationManager.createNotificationChannel(mChannel)


            //######################################
            // Create notification
            //######################################
            var notificationBuilder = Notification.Builder(this, getString(R.string.channel_alert_id))
                    .setContentTitle(getString(R.string.notification_alert_title))
                    .setContentText(getString(R.string.notification_alert_body))
                    .setSmallIcon(android.R.drawable.stat_notify_more)
                    .setAutoCancel(true)

            var notificationID = 101
            var notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(101, notificationBuilder.build())
        }


    }
}