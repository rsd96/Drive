package com.rsd96.drive

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by Ramshad on 11/28/17.
 */
class CarsRecycleAdapter(private var plateList: ArrayList<String>, private var nameList: ArrayList<String>) : RecyclerView.Adapter<CarsRecycleAdapter.ViewHolder>() {


    override fun getItemCount(): Int = plateList.size


    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.tvCarPlate?.text = plateList[position]
        holder?.tvCarName?.text = nameList[position]
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

    }
}