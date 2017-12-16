package com.rsd96.drive

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

/**
 * Created by Ramshad on 12/2/17.
 */
class FirebaseInstanceService: FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        var token = FirebaseInstanceId.getInstance().token
        var ref = FirebaseDatabase.getInstance().reference

        ref.child("users").child(FirebaseAuth.getInstance()?.uid)
                .child("device_token").setValue(token)
    }
}