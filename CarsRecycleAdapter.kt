package com.rsd96.drive

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/**
 * Created by Ramshad on 11/28/17.
 */
class CarsRecycleAdapter(private var context: Context, private var plateList: ArrayList<String>, private var nameList: ArrayList<String>) : RecyclerView.Adapter<CarsRecycleAdapter.ViewHolder>() {


    override fun getItemCount(): Int = plateList.size

    var TAG = "CarsRecycleAdapter"

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.tvCarPlate?.text = plateList[position]
        holder?.tvCarName?.text = nameList[position]
        holder?.ivRemoveCar?.setOnClickListener({view ->
            var database = FirebaseDatabase.getInstance().reference
            var plate = plateList[position]
            var user = FirebaseAuth.getInstance().currentUser

            var builder = AlertDialog.Builder(context)
            builder
                    .setTitle("Remove vehicle")
                    .setMessage("Are you sure you want to remove this vehicle with plate number ${plate} ?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Remove", {dialogInterface, i ->
                        database.child("users").child(user?.uid).child("cars").addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(snap: DataSnapshot?) {
                                Log.d(TAG,"deleting vehicle $plate : $position")
                                if ( snap != null) {
                                    for (x in snap.children) {
                                        if (x.key == plate) {
                                            Log.d(TAG,"deleting vehicle ${x.key} : $plate")
                                            Log.d(TAG, "${x.key}")
                                            x.ref.removeValue()
                                            notifyDataSetChanged()
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(p0: DatabaseError?) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }
                        })
                    })

            builder.create().show()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        // create a new view

        var v = LayoutInflater.from(parent?.context)
                .inflate(R.layout.car_recycle_content, parent, false)
        var vh = ViewHolder(v)
        return vh
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvCarPlate : TextView = view.findViewById(R.id.tv_cars_recycle_plate)
        var tvCarName : TextView = view.findViewById(R.id.tv_cars_recycle_name)
        var ivRemoveCar : ImageView = view.findViewById(R.id.iv_user_car_remove)
    }
}