package com.rsd96.drive

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_alert.*

/**
 * Created by Ramshad on 11/9/17.
 */
class AlertFragment: Fragment() {

    var TAG = "AlertFragment"
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_alert, container, false)

    }

    var owner = " "
    var carName = " "
    var ownerId = " "
    var dbRef = FirebaseDatabase.getInstance().reference

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        et_alert_search.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(p0: TextView?, actionID: Int, p2: KeyEvent?): Boolean {
                if (actionID == EditorInfo.IME_ACTION_SEARCH) {
                    var plate = et_alert_search.text.trim().toString().toLowerCase()
                    searchPlate(plate)
                    return true
                }
                return false
            }
        })

        btn_alert.setOnClickListener { v ->
            var message = et_alert_message.text.toString()
            if (message.isBlank())
                et_alert_message.error = "Message cannot be empty"
            else {
                var from = " "
                dbRef.child("users").child(FirebaseAuth.getInstance()?.uid).child("user_name").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snap: DataSnapshot?) {
                        from = snap?.value.toString()
                        sendNotification(from, ownerId, carName, message)
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
            }
        }
    }

    fun sendNotification(from: String, to: String, vehicle: String, message: String) {
        var notification = hashMapOf<String, String>()
        notification.put("from", from)
        notification.put("to", to)
        notification.put("time", ServerValue.TIMESTAMP.toString())
        notification.put("vehicle", vehicle)
        notification.put("message", message)

        dbRef.child("alerts").push().setValue(notification).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Snackbar.make(coordinator_alert, "$owner alerted, Thank you !", Snackbar.LENGTH_SHORT).show()
            }
        }

    }

    fun searchPlate(plate: String) {
        tv_alert_found.text = ""
        pb_alert.visibility = View.VISIBLE
        var found = false
        dbRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot?) {

                for (userBlock in snapshot?.children!!) {
                    ownerId = userBlock.key
                    owner = userBlock.child("user_name").value.toString()
                    for (carBlock in userBlock.child("cars").children) {
                        if (carBlock.key.toLowerCase() == plate) {
                            found = true
                            carName = carBlock.child("car_name").value.toString()
                        }
                        if (found) break else continue
                    }
                    if (found) break else continue
                }

                pb_alert.visibility = View.GONE
                if (found) {
                    tv_alert_found.text = "Car: $carName \nOwned by : $owner"
                    et_alert_message.visibility = View.VISIBLE
                    btn_alert.visibility = View.VISIBLE
                    var imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)

                } else {
                    tv_alert_found.text = "Vehicle not found!"
                    et_alert_message.visibility = View.GONE
                }
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}