package com.rsd96.drive

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_alert_view.*

/**
 * Created by Ramshad on 12/18/17.
 */
class ChatActivity: AppCompatActivity() {


    var TAG = "ChatActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)
        var data = intent.extras
        var from = data.getString("from")
        var fromId = data.getString("fromId")

        Log.d(TAG, "$from")
        Log.d(TAG, "$fromId")

        setSupportActionBar(toolbar_alert_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}