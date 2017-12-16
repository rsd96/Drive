package com.rsd96.drive

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.github.florent37.viewanimator.ViewAnimator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_alert_view.*

/**
 * Created by Ramshad on 12/16/17.
 */
class AlertViewActivity : AppCompatActivity(), View.OnClickListener{

    var TAG = "AlertViewActivity"
    lateinit var database: DatabaseReference
    var user: FirebaseUser? = null
    lateinit var alert: Alert
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert_view)

        var i = intent
        var data = intent.extras
        alert = data.getParcelable<Alert>("alert_parcel")

        if (alert != null) {
            alert.from.let { tv_alert_view_user.text = it }
            alert.message.let { tv_alert_view_message.text = it }
            alert.vehicle.let { tv_alert_view_vehicle.text = it }
        }

        database = FirebaseDatabase.getInstance().reference
        user = FirebaseAuth.getInstance().currentUser

        setSupportActionBar(toolbar_alert_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btn_alert_view_delete.setOnClickListener(this)
        btn_alert_view_like.setOnClickListener(this)
        btn_alert_view_report.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_alert_view_delete -> {
                Log.d(TAG, "DELETING ALERT....")
                ViewAnimator
                        .animate(cl_alert_view)
                        .rollOut()
                        .backgroundColor(Color.RED)
                        .duration(500)
                        .onStop { deleteAlert() }
                        .start()
            }

            R.id.btn_alert_view_like -> {
                database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snap: DataSnapshot?) {
                        var id = ""
                        if (snap != null) {
                            for (x in snap.children) {
                                id = x.key
                                if (x.child("user_name").value == alert.from) {

                                    ViewAnimator
                                            .animate(btn_alert_view_like)
                                            .duration(1500)
                                            .bounce()
                                            .start()

                                    Snackbar.make(cl_alert_view, "${alert.from} REP+1 !", Snackbar.LENGTH_SHORT).show()
                                    if (x.child("rep").exists()) {
                                        var value = x.child("rep").value as Long
                                        value++
                                        x.ref.child("rep").setValue(value)
                                    } else {
                                        var value : Long = 1
                                        x.ref.child("rep").setValue(value)
                                    }

                                    break
                                }
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
            }

            R.id.btn_alert_view_report -> {

                var builder = AlertDialog.Builder(this)
                builder.setTitle("Report ${alert.from}")
                builder.setMessage("Are you sure you want to report ${alert.from} ?")
                builder.setPositiveButton("Yes", { dialogInterface, i ->
                    deleteAlert()
                })
                builder.setNegativeButton("Cancel", {dialogInterface, i ->  })
                var dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun deleteAlert() {
        database.child("alerts").addListenerForSingleValueEvent( object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot?) {
                if (snap != null) {
                    for (x in snap.children) {
                        if ( x.key == alert.uid) {
                            x.ref.removeValue()
                            finish()
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}
