package com.rsd96.drive

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by Ramshad on 12/14/17.
 */
class AlertFeedAdapter(internal var context: Context, internal var alertList : MutableList<Alert>) : BaseAdapter() {


    override fun getView(pos: Int, convertView: View?, viewGroup: ViewGroup): View? {
        val message = alertList[pos].message
        val vehicle = alertList[pos].vehicle
        val time = alertList[pos].time
        var v: View? = convertView
        var viewHolder = ViewHolder()


        if (v == null) {
            val inflater = LayoutInflater.from(context)
            v = inflater.inflate(R.layout.alert_list_content, null)

            viewHolder.ivProfile = v.findViewById(R.id.iv_alert_list_profile)
            viewHolder.tvVehicle = v.findViewById(R.id.tv_alert_list_car)
            viewHolder.tvMessage = v.findViewById(R.id.tv_alert_list_message)
            viewHolder.tvTime = v.findViewById(R.id.tv_alert_list_time)
            v.tag = viewHolder
        } else {
            viewHolder = v.tag as ViewHolder
        }

        viewHolder.tvVehicle?.text = vehicle
        viewHolder.tvMessage?.text = message
        viewHolder.tvTime?.text = time

        return v
    }

    override fun getItem(pos: Int): Any {
        return alertList[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getCount(): Int = alertList.size


    internal class ViewHolder {
        var ivProfile: ImageView? = null
        var tvMessage: TextView? = null
        var tvVehicle: TextView? = null
        var tvTime: TextView? = null
    }
}