package com.rsd96.drive

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_alert.*

/**
 * Created by Ramshad on 11/9/17.
 */
class AlertFragment: Fragment() {

    var TAG = "AlertFragment"
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_alert, container, false)

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        et_alert_search.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(p0: TextView?, actionID: Int, p2: KeyEvent?): Boolean {
                if (actionID == EditorInfo.IME_ACTION_SEARCH) {
                    var plate = et_alert_search.text.trim().toString()
                    searchPlate(plate)

                    //tv_alert_found.text = "Not found"
                    return true
                }
                return false
            }
        })
    }

    fun searchPlate(plate: String) {
        pb_alert.visibility = View.VISIBLE
        var found = false
        var database = FirebaseDatabase.getInstance().reference
        database.child("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot?) {
                var user = " "
                snapshot?.children?.forEach {
                    user = it.child("user_name").value.toString()
                    it.child("cars").children.forEach( {
                    Log.d(TAG, it.key)
                    if (it.key == plate) {
                        found = true
                        tv_alert_found.text = "User : $user"
                        et_alert_message.visibility = View.VISIBLE
                    }

                })}
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        /*database.orderByChild("cars").equalTo(plate).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot?) {
                Log.d(TAG, " " + snapshot)
                for (x in snapshot?.children!!)
                    Log.d(TAG, "Query : " + x.value)
                found = true
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        Log.d(TAG, "not found")*/

    }
}