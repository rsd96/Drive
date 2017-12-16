package com.rsd96.drive

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_alert_feed.*

/**
 * Created by Ramshad on 12/14/17.
 */
class AlertFeedFragment : Fragment() {

    lateinit var database: DatabaseReference
    var user: FirebaseUser? = null
    var alertList = mutableListOf<Alert>()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_alert_feed, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        user = FirebaseAuth.getInstance().currentUser

        database.child("alerts").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snap: DataSnapshot?) {
                if (pb_alert_feed != null)
                    pb_alert_feed.visibility = View.VISIBLE
                alertList.clear()
                if (snap != null) {
                    for (x in snap.children) {
                        var uid = x.key
                        Log.d(TAG, "$uid")
                        if ((x.child("to").value) == user?.uid) {
                            Log.d(TAG, "${x.child("time").value}")
                            var alert = Alert()
                            alert.vehicle = x.child("vehicle").value.toString()
                            alert.message = x.child("message").value.toString()
                            alert.from = x.child("from").value.toString()
                            alert.uid = uid
//                            var timestamp = x.child("time").value as Long
//                            var sfd : SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
//                            var time = sfd.format( Date(timestamp))
//                            Log.d(TAG, time)
                            alert.time = x.child("time").value.toString()
                            Log.d(TAG, "${alert.time}")
                            alertList.add(alert)
                        }
                    }
                    for ( x in alertList) {
                        Log.d(TAG, "Message ${x.message}")
                    }
                    pb_alert_feed.visibility = View.GONE
                    var adapter = AlertFeedAdapter(activity, alertList)
                    lv_alert_feed.adapter = adapter
                }
            }
            override fun onCancelled(snap: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })


        lv_alert_feed.setOnItemClickListener({ adapterView, view, i, l ->
            val adapter = adapterView.adapter
            val obj : Alert = adapter.getItem(i) as Alert


            var intent = Intent(activity, AlertViewActivity::class.java)
            intent.putExtra("alert_parcel", obj)
            startActivity(intent)

        })
    }
}