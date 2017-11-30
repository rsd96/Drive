package com.rsd96.drive

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
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "From: " + remoteMessage?.getFrom())
        Log.d(TAG, "Notification Message Body: " + remoteMessage?.notification?.body)
    }
}