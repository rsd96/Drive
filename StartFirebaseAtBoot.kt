package com.rsd96.drive

import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by Ramshad on 11/29/17.
 */
class StartFirebaseAtBoot: BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        Log.d(TAG, "startingService")
        context?.startActivity(Intent( NotificationService::class.java.name))
    }
}