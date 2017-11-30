package com.rsd96.drive

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_user.*

/**
 * Created by Ramshad on 11/26/17.
 */
class UserFragment : Fragment() {

    companion object {
        private val TAG = "UserFragment"
    }

    lateinit var reLayoutManager: RecyclerView.LayoutManager
    lateinit var reAdapter : RecyclerView.Adapter<CarsRecycleAdapter.ViewHolder>
    var carList = ArrayList<String>()
    var nameList = ArrayList<String>()
    lateinit var database: DatabaseReference
    var user: FirebaseUser? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater?.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance().reference
        user = FirebaseAuth.getInstance().currentUser

        rv_user_cars.setHasFixedSize(true)
        reLayoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        rv_user_cars.layoutManager = reLayoutManager
        reAdapter = CarsRecycleAdapter(carList, nameList)


        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            Log.d(TAG, "auth state changed !")
            if (firebaseAuth.currentUser != null) {
                Log.d(TAG, firebaseAuth.currentUser?.uid)
                user = firebaseAuth.currentUser

                database.child("users").child(user?.uid).child("cars").addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot?) {
                        nameList.clear()
                        carList.clear()
                        if (snapshot != null) {
                            for (x in snapshot.children) {
                                nameList.add(x.key.toString())
                                carList.add(x.child("car_name").value.toString())
                            }
                            rv_user_cars.adapter = reAdapter
                            reAdapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(snapshot: DatabaseError?) {

                    }
                })
            }
        }


        btn_user_add_cars.setOnClickListener({v ->
            var builder = AlertDialog.Builder(activity)
            var inflater = activity.layoutInflater
            var dialogLayout = inflater.inflate(R.layout.dialog_add_cars, null)
            builder.setView(dialogLayout)
            builder.setPositiveButton("ADD", { dialogInterface, i ->

                var etCarName = dialogLayout.findViewById<EditText>(R.id.et_add_car_name) as EditText
                var etCarPlate = dialogLayout.findViewById<EditText>(R.id.et_add_car_plate) as EditText

                var plate = etCarPlate.text.trim().toString()
                var name = etCarName.text.toString()

                database.child("users").child(user?.uid).child("cars").child("$plate").child("car_name").setValue(name)
            })
            builder.setNegativeButton("CANCEL", null)
            builder.create().show()
        })
        btn_user_logout.setOnClickListener({ view ->
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity.finish()
        })
    }
}